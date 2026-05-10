package com.nidro.multihotbar;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.nidro.multihotbar.hotbar.HotbarAttachment;
import com.nidro.multihotbar.hotbar.ModAttachments;
import com.nidro.multihotbar.network.HotbarSyncPayload;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@EventBusSubscriber(modid = MultiHotbarMod.MOD_ID)
public class ModCommands {
  private final static int HOTBAR_SIZE = 9;

  /** Persists the new count to the config file. */
  private static void applyConfig(int count) {
    ModConfig.HOTBAR_COUNT.set(count);
    ModConfig.HOTBAR_COUNT.save();
  }

  @SubscribeEvent
  public static void onRegisterCommands(RegisterCommandsEvent event) {
    event.getDispatcher().register(
        Commands.literal("multihotbar")
            .requires(source -> source.hasPermission(2)) // requires operator level 2
            .then(Commands.literal("setcount")
                .then(Commands.argument("count", IntegerArgumentType.integer(1, 5))
                    // /multihotbar setcount <n> — saves, applied on relog
                    .executes(commandContext -> {
                      int count = IntegerArgumentType.getInteger(commandContext, "count");
                      applyConfig(count);
                      commandContext.getSource().sendSuccess(() -> Component.literal(
                              "[MultiHotbar] hotbarCount set to " + count + ". Players must relog for changes to take effect."
                      ), true);
                      return 1;
                    })
                    // /multihotbar setcount <n> force — saves + applies immediately
                    .then(Commands.literal("force")
                      .executes(commandContext -> {
                        int count = IntegerArgumentType.getInteger(commandContext, "count");
                        applyConfig(count);
                        List<ServerPlayer> players = commandContext.getSource().getServer().getPlayerList().getPlayers();
                        int affected = 0;
                        for (ServerPlayer player : players) {
                          applyToPlayer(player, count);
                          affected++;
                        }
                        int finalAffected = affected;
                        commandContext.getSource().sendSuccess(() -> Component.literal(
                                "[MultiHotbar] hotbarCount set to " + count + " and applied to " + finalAffected + " connected player(s)."
                        ), true);
                        return 1;
                      })
                    )
                )
            )
    );
  }

  /**
   * Applies the new hotbar count to a connected player immediately.
   * Redistributes items from removed hotbars, drops excess at player's feet.
   */
  private static void applyToPlayer(ServerPlayer player, int newCount) {
    HotbarAttachment data = player.getData(ModAttachments.HOTBARS.get());
    int oldCount = data.size();
    if (oldCount == newCount) return;

    try {
      if (newCount < oldCount) {
        reduceHotbars(player, data, newCount, oldCount);
      } else {
        expandHotbars(data, newCount, oldCount);
      }
    } catch (Exception e) {
      resetOnError(player, data, newCount);
    }
    syncInventoryAndHud(player, data);
  }

  /** Trims hotbars down to newCount, redistributing or dropping overflow items. */
  private static void reduceHotbars(ServerPlayer player, HotbarAttachment data, int newCount, int oldCount) {
    List<ItemStack> overflow = collectOverflow(data, newCount, oldCount);
    data.getHotbars().subList(newCount, oldCount).clear();
    if (data.getCurrentIndex() >= newCount) data.setCurrentIndex(newCount - 1);
    for (ItemStack stack : overflow) {
      if (!placeInFreeSlot(data, stack)) dropAtFeet(player, stack);
    }
  }

  /** Collects all items from hotbars that are about to be removed. */
  private static List<ItemStack> collectOverflow(HotbarAttachment data, int newCount, int oldCount) {
    List<ItemStack> overflow = new ArrayList<>();
    for (int i = newCount; i < oldCount; i++) {
      for (ItemStack stack : data.getHotbars().get(i)) {
        if (stack != null && !stack.isEmpty()) overflow.add(stack.copy());
      }
    }
    return overflow;
  }

  /** Tries to place a stack in the first free slot of any remaining hotbar. */
  private static boolean placeInFreeSlot(HotbarAttachment data, ItemStack stack) {
    for (ItemStack[] bar : data.getHotbars()) {
      for (int j = 0; j < HOTBAR_SIZE; j++) {
        if (bar[j].isEmpty()) {
          bar[j] = stack.copy();
          return true;
        }
      }
    }
    return false;
  }

  /** Appends empty hotbars up to newCount. */
  private static void expandHotbars(HotbarAttachment data, int newCount, int oldCount) {
    for (int i = oldCount; i < newCount; i++) {
      ItemStack[] bar = new ItemStack[HOTBAR_SIZE];
      Arrays.fill(bar, ItemStack.EMPTY);
      data.getHotbars().add(bar);
    }
  }

  /** Drops all items on the ground and resets to a clean state. */
  private static void resetOnError(ServerPlayer player, HotbarAttachment data, int newCount) {
    for (ItemStack[] bar : data.getHotbars()) {
      for (ItemStack stack : bar) {
        if (stack != null && !stack.isEmpty()) dropAtFeet(player, stack);
      }
    }
    data.getHotbars().clear();
    for (int i = 0; i < newCount; i++) {
      ItemStack[] bar = new ItemStack[HOTBAR_SIZE];
      Arrays.fill(bar, ItemStack.EMPTY);
      data.getHotbars().add(bar);
    }
    data.setCurrentIndex(0);
  }

  /** Drops a single ItemStack at the player's feet. */
  private static void dropAtFeet(ServerPlayer player, ItemStack stack) {
    ItemEntity entity = new ItemEntity(
            player.level(),
            player.getX(), player.getY(), player.getZ(),
            stack.copy()
    );
    entity.setDefaultPickUpDelay();
    player.level().addFreshEntity(entity);
  }

  /** Syncs the active hotbar slots to the player's inventory and updates the HUD. */
  private static void syncInventoryAndHud(ServerPlayer player, HotbarAttachment data) {
    ItemStack[] active = data.getHotbars().get(data.getCurrentIndex());
    for (int i = 0; i < HOTBAR_SIZE; i++) {
      player.getInventory().setItem(i, active[i] == null ? ItemStack.EMPTY : active[i].copy());
    }
    player.inventoryMenu.broadcastChanges();
    PacketDistributor.sendToPlayer(player, new HotbarSyncPayload(data.getCurrentIndex(), data.size()));
  }
}

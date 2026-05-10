package com.nidro.multihotbar;

import com.nidro.multihotbar.hotbar.HotbarAttachment;
import com.nidro.multihotbar.hotbar.HotbarManager;
import com.nidro.multihotbar.hotbar.ModAttachments;
import com.nidro.multihotbar.network.HotbarSyncPayload;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.List;

@EventBusSubscriber(modid = MultiHotbarMod.MOD_ID)
public class ServerEvents {
  private final static int HOTBAR_SIZE = 9;

  @SubscribeEvent
  public static void onPlayerLogin(PlayerEvent.PlayerLoggedInEvent event) {
    if (!(event.getEntity() instanceof ServerPlayer serverPlayer)) return;
    // sync HUD state on join so the client shows the correct index immediately
    HotbarAttachment data = HotbarManager.get(serverPlayer);

    // TODO
    // restore the active hotbar from our attachment into the player's inventory
    // this ensures consistency since MC saves/loads the inventory independently
    ItemStack[] active = data.getHotbars().get(data.getCurrentIndex());
    for (int i = 0; i < HOTBAR_SIZE; i++) {
      ItemStack stack = active[i];
      serverPlayer.getInventory().setItem(i, (stack == null || stack.isEmpty()) ? ItemStack.EMPTY : stack.copy());
    }
    serverPlayer.inventoryMenu.broadcastChanges();

    // drop any items that couldn't fit in the hotbars after a count reduction —
    //  we do this before syncing the index to ensure the client has the correct hotbar count
    List<ItemStack> drops = data.getPendingDrops();
    if (!drops.isEmpty()) {
      for (ItemStack stack : drops) {
        ItemEntity entity = new ItemEntity( // spawn an item entity for each dropped stack
                serverPlayer.level(),
                serverPlayer.getX(), serverPlayer.getY(), serverPlayer.getZ(),
                stack.copy()
        );
        entity.setDefaultPickUpDelay();
        serverPlayer.level().addFreshEntity(entity);  // spawn the entity in the world
      }
      drops.clear();
    }
    // sync HUD
    PacketDistributor.sendToPlayer(serverPlayer, new HotbarSyncPayload(data.getCurrentIndex(), data.size()));
  }

  @SubscribeEvent
  public static void onPlayerLogout(PlayerEvent.PlayerLoggedOutEvent event) {
    if (!(event.getEntity() instanceof ServerPlayer serverPlayer)) return;
    // snapshot the current hotbar into the attachment before the player disconnects
    // so it's saved correctly even if they log out mid-session
    HotbarAttachment data = HotbarManager.get(serverPlayer);
    ItemStack[] snapshot = new ItemStack[HOTBAR_SIZE];
    for (int i = 0; i < HOTBAR_SIZE; i++) {
      snapshot[i] = serverPlayer.getInventory().getItem(i).copy();
    }
    data.getHotbars().set(data.getCurrentIndex(), snapshot);
  }

  @SubscribeEvent
  public static void onPlayerClone(PlayerEvent.Clone event) {
    if (!(event.getEntity() instanceof ServerPlayer newPlayer)) return;
    if (!(event.getOriginal() instanceof ServerPlayer oldPlayer)) return;
    // copy hotbar data on death/respawn — attachment is not copied automatically
    if (!event.isWasDeath()) return;

    HotbarAttachment oldData = oldPlayer.getData(ModAttachments.HOTBARS.get());
    HotbarAttachment newData = newPlayer.getData(ModAttachments.HOTBARS.get());

    newData.getHotbars().clear();
    for (ItemStack[] bar : oldData.getHotbars()) {
      ItemStack[] copy = new net.minecraft.world.item.ItemStack[HOTBAR_SIZE];
      for (int i = 0; i < HOTBAR_SIZE; i++) copy[i] = bar[i].copy();
      newData.getHotbars().add(copy);
    }
    newData.setCurrentIndex(oldData.getCurrentIndex());
    newData.getPendingDrops().addAll(oldData.getPendingDrops());
  }
}
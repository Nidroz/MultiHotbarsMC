package com.nidro.multihotbar.hotbar;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class HotbarManager {
  // temporary in-memory store — persistence (capability) comes in V2 TODO
  private static final Map<UUID, HotbarData> DATA = new HashMap<>();
  private static final int HOTBAR_SIZE = 9;

  public static HotbarData getHotbarData(ServerPlayer player) {
    return DATA.computeIfAbsent(player.getUUID(), id -> new HotbarData());
  }

  /**
   * Saves the current hotbar, moves to the next/previous one, then loads it.
   *
   * @param player    the server player performing the swap
   * @param direction +1 for next, -1 for previous
   */
  public static void swap(ServerPlayer player, int direction) {
    HotbarData data = getHotbarData(player);
    // get the current hotbar from the player's inventory
    ItemStack[] currentHotbar = new ItemStack[HOTBAR_SIZE];
    for (int i = 0; i < HOTBAR_SIZE; i++) {
      // copy is mandatory — avoids shared references and item duplication
      currentHotbar[i] = player.getInventory().getItem(i).copy();
    }
    data.getHotbars().set(data.getCurrentIndex(), currentHotbar);

    // compute the new index with wrap-around
    int newIndex = (data.getCurrentIndex() + direction) % data.size();
    data.setCurrentIndex(newIndex);

    // load the new hotbar into the player's inventory
    ItemStack[] newHotbar = data.getHotbars().get(newIndex);
    for (int i = 0; i < HOTBAR_SIZE; i++) {
      ItemStack stack = newHotbar[i];
      player.getInventory().setItem(i, (stack == null || stack.isEmpty()) ? ItemStack.EMPTY : stack.copy());
    }

    // synchronize the hotbar change with the client
    player.inventoryMenu.broadcastChanges();
  }

  /** Cleans up data when the player leaves to avoid memory leaks. */
  public static void remove(ServerPlayer player) {
    DATA.remove(player.getUUID());
  }
}

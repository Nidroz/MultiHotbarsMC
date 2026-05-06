package com.nidro.multihotbar.hotbar;

import com.nidro.multihotbar.network.HotbarSyncPayload;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.PacketDistributor;

public class HotbarManager {

  /** Gets (or creates) the persistent attachment for this player. */
  public static HotbarAttachment get(ServerPlayer player) {
    return player.getData(ModAttachments.HOTBARS.get());
  }

  /**
   * Saves the current hotbar, moves to the next/previous one, then loads it.
   *
   * @param player    the server player performing the swap
   * @param direction +1 for next, -1 for previous
   */
  public static void swap(ServerPlayer player, int direction) {
    HotbarAttachment data = get(player);

    if (data.size() < 2) return;

    int currentIndex = Math.floorMod(data.getCurrentIndex(), data.size());

    // 1. snapshot the current hotbar from the player's inventory
    ItemStack[] snapshot = new ItemStack[9];
    for (int i = 0; i < 9; i++) {
      // copy is mandatory — avoids shared references and item duplication
      snapshot[i] = player.getInventory().getItem(i).copy();
    }
    data.getHotbars().set(currentIndex, snapshot);

    // 2. compute the new index with wrap-around (floorMod handles negatives)
    int newIndex = Math.floorMod(currentIndex + direction, data.size());
    data.setCurrentIndex(newIndex);

    // 3. load the target hotbar into the player's inventory
    ItemStack[] target = data.getHotbars().get(newIndex);
    for (int i = 0; i < 9; i++) {
      ItemStack stack = target[i];
      player.getInventory().setItem(i, (stack == null || stack.isEmpty()) ? ItemStack.EMPTY : stack.copy());
    }

    // 4. sync inventory to client
    player.inventoryMenu.broadcastChanges();

    // 5. notify the HUD
    PacketDistributor.sendToPlayer(player, new HotbarSyncPayload(newIndex, data.size()));
  }
}
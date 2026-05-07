package com.nidro.multihotbar.hotbar;

import com.nidro.multihotbar.ModConfig;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Persistent attachment stored in the player's NBT data.
 * Replaces the in-memory HotbarData + HashMap approach.
 */
public class HotbarAttachment {

  private static final int HOTBAR_SIZE    = 9;
  private static final int FALLBACK_COUNT = 3;

  private List<ItemStack[]> hotbars;
  private int currentIndex = 0;

  // items that couldn't fit in remaining hotbars after a count reduction
  private final List<ItemStack> pendingDrops = new ArrayList<>();

  public HotbarAttachment() {
    this.hotbars = buildEmpty(safeCount());
  }

  // --- accessors ---

  public List<ItemStack[]> getHotbars()    { return hotbars; }
  public int getCurrentIndex()             { return currentIndex; }
  public int size()                        { return hotbars.size(); }
  public List<ItemStack> getPendingDrops() { return pendingDrops; }

  public void setCurrentIndex(int index) {
    this.currentIndex = Math.floorMod(index, hotbars.size());
  }

  // --- NBT serialization ---

  public CompoundTag serialize(HolderLookup.Provider provider) {
    CompoundTag root = new CompoundTag();
    root.putInt("currentIndex", currentIndex);

    ListTag hotbarList = new ListTag();
    for (ItemStack[] bar : hotbars) {
      ListTag slotList = new ListTag();
      for (ItemStack stack : bar) {
        CompoundTag slotTag = new CompoundTag();
        if (stack != null && !stack.isEmpty()) {
          slotTag = (CompoundTag) stack.save(provider);
        }
        slotList.add(slotTag);
      }
      hotbarList.add(slotList);
    }
    root.put("hotbars", hotbarList);

    // persist pending drops in case the player logs out before they're dropped
    ListTag dropList = new ListTag();
    for (ItemStack stack : pendingDrops) {
      if (!stack.isEmpty()) dropList.add(stack.save(provider));
    }
    root.put("pendingDrops", dropList);

    return root;
  }

  public void deserialize(CompoundTag tag, HolderLookup.Provider provider) {
    int count = safeCount();

    // load raw hotbars from NBT first (without count limit)
    ListTag hotbarList = tag.getList("hotbars", Tag.TAG_LIST);
    List<ItemStack[]> loaded = new ArrayList<>();
    for (Tag value : hotbarList) {
      ItemStack[] bar = new ItemStack[HOTBAR_SIZE];
      ListTag slotList = (ListTag) value;
      for (int j = 0; j < HOTBAR_SIZE; j++) {
        bar[j] = ItemStack.EMPTY;
        if (j < slotList.size()) {
          CompoundTag slotTag = (CompoundTag) slotList.get(j);
          if (!slotTag.isEmpty()) {
            bar[j] = ItemStack.parseOptional(provider, slotTag);
          }
        }
      }
      loaded.add(bar);
    }

    // keep only hotbars that fit within the current config count
    hotbars = buildEmpty(count);
    for (int i = 0; i < Math.min(loaded.size(), count); i++) {
      hotbars.set(i, loaded.get(i));
    }

    // clamp currentIndex in case config was reduced
    currentIndex = Math.floorMod(tag.getInt("currentIndex"), count);

    // handle hotbars that were cut off by a count reduction
    for (int i = count; i < loaded.size(); i++) {
      for (ItemStack stack : loaded.get(i)) {
        if (stack == null || stack.isEmpty()) continue;

        // try to place the item in a free slot of a remaining hotbar
        boolean placed = false;
        outer:
        for (ItemStack[] bar : hotbars) {
          for (int j = 0; j < HOTBAR_SIZE; j++) {
            if (bar[j].isEmpty()) {
              bar[j] = stack.copy();
              placed = true;
              break outer;
            }
          }
        }

        // no free slot found — queue for drop on next login
        if (!placed) pendingDrops.add(stack.copy());
      }
    }

    // restore previously pending drops
    ListTag dropList = tag.getList("pendingDrops", Tag.TAG_COMPOUND);
    for (int i = 0; i < dropList.size(); i++) {
      ItemStack stack = ItemStack.parseOptional(provider, dropList.getCompound(i));
      if (!stack.isEmpty()) pendingDrops.add(stack);
    }
  }

  // --- helpers ---

  private static int safeCount() {
    try {
      int v = ModConfig.HOTBAR_COUNT.get();
      return v >= 1 ? v : FALLBACK_COUNT;
    } catch (Exception e) {
      return FALLBACK_COUNT;
    }
  }

  private static List<ItemStack[]> buildEmpty(int count) {
    List<ItemStack[]> list = new ArrayList<>();
    for (int i = 0; i < count; i++) {
      ItemStack[] bar = new ItemStack[HOTBAR_SIZE];
      Arrays.fill(bar, ItemStack.EMPTY);
      list.add(bar);
    }
    return list;
  }
}
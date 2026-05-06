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

  public HotbarAttachment() {
    this.hotbars = buildEmpty(safeCount());
  }

  // --- accessors ---

  public List<ItemStack[]> getHotbars() { return hotbars; }
  public int getCurrentIndex()          { return currentIndex; }
  public int size()                     { return hotbars.size(); }

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
    return root;
  }

  public void deserialize(CompoundTag tag, HolderLookup.Provider provider) {
    int count = safeCount();
    hotbars = buildEmpty(count);

    currentIndex = tag.getInt("currentIndex");
    // clamp in case config changed since last save
    currentIndex = Math.floorMod(currentIndex, count);

    ListTag hotbarList = tag.getList("hotbars", Tag.TAG_LIST);
    for (int i = 0; i < Math.min(hotbarList.size(), count); i++) {
      ListTag slotList = (ListTag) hotbarList.get(i);
      for (int j = 0; j < Math.min(slotList.size(), HOTBAR_SIZE); j++) {
        CompoundTag slotTag = (CompoundTag) slotList.get(j);
        // empty compound = empty slot
        if (!slotTag.isEmpty()) {
          hotbars.get(i)[j] = ItemStack.parseOptional(provider, slotTag);
        }
      }
    }
  }

  // --- helpers ---

  private static int safeCount() {
    try {
      int v = ModConfig.HOTBAR_COUNT.get();
      return v >= 2 ? v : FALLBACK_COUNT;
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
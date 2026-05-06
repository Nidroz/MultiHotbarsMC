package com.nidro.multihotbar.hotbar;

import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class HotbarData {
  private static final int HOTBAR_COUNT = 5; // TODO configurable
  private static final int HOTBAR_SIZE = 9;

  // each entry is a snapshot of 9 slots
  private final List<ItemStack[]> hotbars = new ArrayList<>();
  private int currentIndex = 0;

  public HotbarData() {
    for (int i = 0; i < HOTBAR_COUNT; i++) {
      ItemStack[] hotbar = new ItemStack[HOTBAR_SIZE];
      Arrays.fill(hotbar, ItemStack.EMPTY);
      hotbars.add(hotbar);
    }
  }

  public List<ItemStack[]> getHotbars() {
    return hotbars;
  }

  public int getCurrentIndex() {
    return currentIndex;
  }

  public void setCurrentIndex(int currentIndex) {
    this.currentIndex = currentIndex;
  }

  public int size() {
    return hotbars.size();
  }
}

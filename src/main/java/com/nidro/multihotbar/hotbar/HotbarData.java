package com.nidro.multihotbar.hotbar;

import com.nidro.multihotbar.ModConfig;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class HotbarData {
  private static final int HOTBAR_SIZE = 9; // will always be 9 (vanilla hotbar size)
  private static final int FALLBACK_COUNT = 3;

  // each entry is a snapshot of 9 slots
  private final List<ItemStack[]> hotbars = new ArrayList<>();
  private int currentIndex = 0;

  public HotbarData() {
    int count = FALLBACK_COUNT;
    try {
      // config may not be loaded yet in some edge cases (e.g. early init)
      int configured = ModConfig.HOTBAR_COUNT.get();
      if (configured >= 2) count = configured;
    } catch (Exception e) {
      // ignore and use fallback
    }
    for (int i = 0; i < count; i++) {
      hotbars.add(emptyBar());
    }
  }

  private static ItemStack[] emptyBar() {
    ItemStack[] hotbar = new ItemStack[HOTBAR_SIZE];
    Arrays.fill(hotbar, ItemStack.EMPTY);
    return hotbar;
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

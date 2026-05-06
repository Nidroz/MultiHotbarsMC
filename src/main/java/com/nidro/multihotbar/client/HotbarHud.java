package com.nidro.multihotbar.client;

import com.nidro.multihotbar.MultiHotbarMod;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RenderGuiEvent;

@EventBusSubscriber(modid = MultiHotbarMod.MOD_ID, value = Dist.CLIENT)
public class HotbarHud {
  // white and dark-gray colors (ARGB)
  private static final int COLOR_ACTIVE   = 0xFFFFFFFF;
  private static final int COLOR_DISABLED = 0xFF555555;

  private static int currentIndex = 0;
  private static int total = 3;

  /** Called from HotbarSyncPayload when a swap packet arrives. */
  public static void update(int newIndex, int count) {
    currentIndex = newIndex;
    total = count;
  }

  @SubscribeEvent
  public static void onRenderGui(RenderGuiEvent.Post event) {
    Minecraft mc = Minecraft.getInstance();
    if (mc.player == null || mc.options.hideGui) return;

    GuiGraphics graphics = event.getGuiGraphics();

    int screenW = mc.getWindow().getGuiScaledWidth();
    int screenH = mc.getWindow().getGuiScaledHeight();

    // vanilla hotbar is 182px wide, centered at screen bottom with 2px margin
    int hotbarWidth  = 182;
    int hotbarHeight = 22;
    int hotbarLeft   = (screenW - hotbarWidth) / 2;
    int hotbarTop    = screenH - hotbarHeight - 2;

    // widget sits just to the left of the hotbar, vertically centered on it
    int widgetX = hotbarLeft - 14;
    int widgetCenterY = hotbarTop + hotbarHeight / 2;

    // ▲ arrow — always active since hotbars are cyclic, gray only if total == 1
    int arrowColor = total > 1 ? COLOR_ACTIVE : COLOR_DISABLED;

    graphics.drawString(mc.font, "▲", widgetX, widgetCenterY - 10, arrowColor, false);

    // hotbar number (1-based)
    String label = String.valueOf(currentIndex + 1);
    int labelX = widgetX + (mc.font.width("▲") - mc.font.width(label)) / 2;
    graphics.drawString(mc.font, label, labelX, widgetCenterY - 4, COLOR_ACTIVE, false);

    // ▼ arrow
    graphics.drawString(mc.font, "▼", widgetX, widgetCenterY + 2, arrowColor, false);
  }
}

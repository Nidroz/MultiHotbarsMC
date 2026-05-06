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

  // arrow characters that render correctly with MC's font
  private static final String ARROW_UP   = "/\\";
  private static final String ARROW_DOWN = "\\/";

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

    // vanilla hotbar: 182px wide, centered, 2px above bottom
    int hotbarWidth  = 182;
    int hotbarHeight = 22;
    int hotbarLeft   = (screenW - hotbarWidth) / 2;
    int hotbarTop    = screenH - hotbarHeight - 2;

    // widget sits just to the left of the hotbar, vertically centered
    int widgetX       = hotbarLeft - 16;
    int widgetCenterY = hotbarTop + hotbarHeight / 2;

    // arrows are always active since hotbars are cyclic
    int arrowColor = total > 1 ? COLOR_ACTIVE : COLOR_DISABLED;

    // ▲ — top arrow
    graphics.drawString(mc.font, ARROW_UP, widgetX, widgetCenterY - 10, arrowColor, false);

    // hotbar number, centered under the arrow
    String label = String.valueOf(currentIndex + 1);
    int labelW = mc.font.width(label);
    int arrowW = mc.font.width(ARROW_UP);
    int labelX = widgetX + (arrowW - labelW) / 2;
    graphics.drawString(mc.font, label, labelX, widgetCenterY - 4, COLOR_ACTIVE, false);

    // ▼ — bottom arrow
    graphics.drawString(mc.font, ARROW_DOWN, widgetX, widgetCenterY + 3, arrowColor, false);
  }
}

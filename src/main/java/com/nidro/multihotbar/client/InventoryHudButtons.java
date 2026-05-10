package com.nidro.multihotbar.client;

import com.nidro.multihotbar.MultiHotbarMod;
import com.nidro.multihotbar.network.SwapHotbarPayload;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ScreenEvent;
import net.neoforged.neoforge.network.PacketDistributor;

@EventBusSubscriber(modid = MultiHotbarMod.MOD_ID, value = Dist.CLIENT)
public class InventoryHudButtons {

  private static final int BTN_W   = 18;
  private static final int BTN_H   = 12;
  private static final int LABEL_H = 8; // font height
  private static final int GAP     = 1;  // gap between button and label

  // total widget height: btn + gap + label + gap + btn
  private static final int WIDGET_H = BTN_H + GAP + LABEL_H + GAP + BTN_H;

  @SubscribeEvent
  public static void onScreenInit(ScreenEvent.Init.Post event) {
    if (!(event.getScreen() instanceof AbstractContainerScreen<?> screen)) return;

    int btnX      = screen.getGuiLeft() + 7 + 162 + 4;  // guiLeft + hotbar width + gap
    int widgetY   = computeWidgetY(screen);

    // ▲ top button — next hotbar (higher index)
    event.addListener(Button.builder(
                    Component.literal("/\\"),
                    btn -> PacketDistributor.sendToServer(new SwapHotbarPayload(1))
            )
            .pos(btnX, widgetY)
            .size(BTN_W, BTN_H)
            .build());

    // ▼ bottom button — prev hotbar (lower index)
    event.addListener(Button.builder(
                    Component.literal("\\/"),
                    btn -> PacketDistributor.sendToServer(new SwapHotbarPayload(-1))
            )
            .pos(btnX, widgetY + BTN_H + GAP + LABEL_H + GAP)
            .size(BTN_W, BTN_H)
            .build());
  }

  @SubscribeEvent
  public static void onScreenRender(ScreenEvent.Render.Post event) {
    if (!(event.getScreen() instanceof AbstractContainerScreen<?> screen)) return;

    int btnX    = screen.getGuiLeft() + 7 + 162 + 4;
    int widgetY = computeWidgetY(screen);

    // draw current hotbar index centered between the two buttons
    String label = String.valueOf(HotbarHud.getCurrentIndex() + 1);
    int labelW = event.getScreen().getMinecraft().font.width(label);
    int labelX = btnX + (BTN_W - labelW) / 2;
    int labelY = widgetY + BTN_H + GAP;

    event.getGuiGraphics().drawString(
            event.getScreen().getMinecraft().font,
            label,
            labelX,
            labelY,
            0xFFFFFFFF,
            false
    );
  }

  /** Centers the widget vertically on the hotbar row of the container gui. */
  private static int computeWidgetY(AbstractContainerScreen<?> screen) {
    int hotbarRowY = screen.getGuiTop() + screen.getYSize() - 18; // hotbar is always 18px high and at the bottom of the container
    // center widget on the 18px hotbar slot height
    return hotbarRowY + (18 - WIDGET_H) / 2 - 7; // -7px to nudge it slightly upwards for better visual alignment
  }
}
package com.nidro.multihotbar.client;

import com.nidro.multihotbar.MultiHotbarMod;
import com.nidro.multihotbar.network.SwapHotbarPayload;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.network.PacketDistributor;

@EventBusSubscriber(modid = MultiHotbarMod.MOD_ID, value = Dist.CLIENT)
public class ClientEvents {
  @SubscribeEvent
  public static void onClientTick(ClientTickEvent.Post event) {
    Minecraft mc = Minecraft.getInstance();
    // ignore inputs when no player
    if (mc.player == null) return;
    // allow keybinds in-game AND in any container screen (inventory, chest, etc.)
    // block only for non-container screens (main menu, pause, chat...)
    if (mc.screen != null && !(mc.screen instanceof AbstractContainerScreen<?>)) return;

    while (KeyBindings.NEXT_HOTBAR.consumeClick()) {
      PacketDistributor.sendToServer(new SwapHotbarPayload(1));
    }
    while (KeyBindings.PREV_HOTBAR.consumeClick()) {
      PacketDistributor.sendToServer(new SwapHotbarPayload(-1));
    }
  }
}

package com.nidro.multihotbar.client;

import com.nidro.multihotbar.MultiHotbarMod;
import com.nidro.multihotbar.network.SwapHotbarPayload;
import net.minecraft.client.Minecraft;
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
    // ignore inputs when no player or a screen is open
    if (mc.player == null || mc.screen != null) return;

    while (KeyBindings.NEXT_HOTBAR.consumeClick()) {
      PacketDistributor.sendToServer(new SwapHotbarPayload(1));
    }
    while (KeyBindings.PREV_HOTBAR.consumeClick()) {
      PacketDistributor.sendToServer(new SwapHotbarPayload(-1));
    }
  }
}

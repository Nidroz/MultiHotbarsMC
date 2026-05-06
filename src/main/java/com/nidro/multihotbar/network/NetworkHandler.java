package com.nidro.multihotbar.network;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

public class NetworkHandler {
  private static void onRegisterPayloads(RegisterPayloadHandlersEvent event) {
    PayloadRegistrar registrar = event.registrar("1");  // "1" is the channel name, can be anything but should be unique to avoid conflicts with other mods
    registrar.playToServer(
            SwapHotbarPayload.TYPE,
            SwapHotbarPayload.STREAM_CODEC,
            SwapHotbarPayload::handle
    );
  }

  public static void register(IEventBus modEventBus) {
    modEventBus.addListener(NetworkHandler::onRegisterPayloads);
  }
}

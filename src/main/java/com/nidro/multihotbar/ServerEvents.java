package com.nidro.multihotbar;

import com.nidro.multihotbar.hotbar.HotbarManager;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;

@EventBusSubscriber(modid = MultiHotbarMod.MOD_ID)
public class ServerEvents {
  @SubscribeEvent
  public static void onPlayerLogout(PlayerEvent.PlayerLoggedOutEvent event) {
    // clear hotbar data when player logs out to prevent memory leaks
    if (event.getEntity() instanceof ServerPlayer serverPlayer) {
      HotbarManager.remove(serverPlayer);
    }
  }
}

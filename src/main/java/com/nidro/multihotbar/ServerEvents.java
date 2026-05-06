package com.nidro.multihotbar;

import com.nidro.multihotbar.hotbar.HotbarData;
import com.nidro.multihotbar.hotbar.HotbarManager;
import com.nidro.multihotbar.network.HotbarSyncPayload;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.network.PacketDistributor;

@EventBusSubscriber(modid = MultiHotbarMod.MOD_ID)
public class ServerEvents {
  @SubscribeEvent
  public static void onPlayerLogin(PlayerEvent.PlayerLoggedOutEvent event) {
    // clear hotbar data when player logs out to prevent memory leaks
    if (event.getEntity() instanceof ServerPlayer serverPlayer) {
      // initialize data for this player and sync the HUD immediately
      HotbarData data = HotbarManager.getHotbarData(serverPlayer);
      PacketDistributor.sendToPlayer(serverPlayer, new HotbarSyncPayload(data.getCurrentIndex(), data.size()));
    }
  }

  @SubscribeEvent
  public static void onPlayerLogout(PlayerEvent.PlayerLoggedOutEvent event) {
    // clear hotbar data when player logs out to prevent memory leaks
    if (event.getEntity() instanceof ServerPlayer serverPlayer) {
      HotbarManager.remove(serverPlayer);
    }
  }
}

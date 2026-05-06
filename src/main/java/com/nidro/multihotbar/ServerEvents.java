package com.nidro.multihotbar;

import com.nidro.multihotbar.hotbar.HotbarAttachment;
import com.nidro.multihotbar.hotbar.HotbarManager;
import com.nidro.multihotbar.hotbar.ModAttachments;
import com.nidro.multihotbar.network.HotbarSyncPayload;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.network.PacketDistributor;

@EventBusSubscriber(modid = MultiHotbarMod.MOD_ID)
public class ServerEvents {

  @SubscribeEvent
  public static void onPlayerLogin(PlayerEvent.PlayerLoggedInEvent event) {
    if (!(event.getEntity() instanceof ServerPlayer serverPlayer)) return;

    // sync HUD state on join so the client shows the correct index immediately
    HotbarAttachment data = HotbarManager.get(serverPlayer);
    PacketDistributor.sendToPlayer(serverPlayer, new HotbarSyncPayload(data.getCurrentIndex(), data.size()));
  }

  @SubscribeEvent
  public static void onPlayerClone(PlayerEvent.Clone event) {
    if (!(event.getEntity() instanceof ServerPlayer newPlayer)) return;
    if (!(event.getOriginal() instanceof ServerPlayer oldPlayer)) return;

    // copy hotbar data on death/respawn — attachment is not copied automatically
    if (!event.isWasDeath()) return;

    HotbarAttachment oldData = oldPlayer.getData(ModAttachments.HOTBARS.get());
    HotbarAttachment newData = newPlayer.getData(ModAttachments.HOTBARS.get());

    newData.getHotbars().clear();
    for (var bar : oldData.getHotbars()) {
      var copy = new net.minecraft.world.item.ItemStack[9];
      for (int i = 0; i < 9; i++) copy[i] = bar[i].copy();
      newData.getHotbars().add(copy);
    }
    newData.setCurrentIndex(oldData.getCurrentIndex());
  }
}
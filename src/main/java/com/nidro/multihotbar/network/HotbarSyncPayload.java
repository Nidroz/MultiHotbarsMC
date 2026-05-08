package com.nidro.multihotbar.network;

import com.nidro.multihotbar.MultiHotbarMod;
import com.nidro.multihotbar.client.HotbarHud;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;

public record HotbarSyncPayload(int currentIndex, int totalHotbars) implements CustomPacketPayload {
  public static final CustomPacketPayload.Type<HotbarSyncPayload> TYPE =
          new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(MultiHotbarMod.MOD_ID, "hotbar_sync"));

  public static final StreamCodec<FriendlyByteBuf, HotbarSyncPayload> STREAM_CODEC =
          StreamCodec.of(
                  (buffer, payload) -> {
                    buffer.writeInt(payload.currentIndex());
                    buffer.writeInt(payload.totalHotbars());
                  },
                  buffer -> new HotbarSyncPayload(buffer.readInt(), buffer.readInt())
          );

  @Override
  public @NotNull Type<? extends CustomPacketPayload> type() {
    return TYPE;
  }

  // called on the client thread
  public static void handle(HotbarSyncPayload payload, IPayloadContext context) {
    context.enqueueWork(() -> {
      if (FMLEnvironment.dist == Dist.CLIENT) {
        HotbarHud.update(payload.currentIndex(), payload.totalHotbars());
      }
    });
  }
}

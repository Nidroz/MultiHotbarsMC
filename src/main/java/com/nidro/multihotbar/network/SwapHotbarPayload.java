package com.nidro.multihotbar.network;

import com.nidro.multihotbar.MultiHotbarMod;
import com.nidro.multihotbar.hotbar.HotbarManager;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;

public record SwapHotbarPayload(int direction) implements CustomPacketPayload {
  public static final CustomPacketPayload.Type<SwapHotbarPayload> TYPE =
          new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(MultiHotbarMod.MOD_ID, "swap_hotbar"));

  // codec to read/write the packet over the network
  public static final StreamCodec<FriendlyByteBuf, SwapHotbarPayload> STREAM_CODEC =
          StreamCodec.of(
                  (buffer, payload) -> buffer.writeInt(payload.direction()),
                  buffer -> new SwapHotbarPayload(buffer.readInt())
          );

  @Override
  public CustomPacketPayload.@NotNull Type<? extends CustomPacketPayload> type() {
    return TYPE;
  }

  // called on the server thread
  public static void handle(SwapHotbarPayload payload, IPayloadContext context) {
    context.enqueueWork(() -> {
      if (context.player() instanceof ServerPlayer serverPlayer) {
        HotbarManager.swap(serverPlayer, payload.direction());
      }
    });
  }
}

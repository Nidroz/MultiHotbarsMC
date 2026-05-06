package com.nidro.multihotbar.hotbar;

import com.nidro.multihotbar.MultiHotbarMod;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.attachment.IAttachmentSerializer;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;
import org.jetbrains.annotations.NotNull;

import java.util.function.Supplier;

public class ModAttachments {

  private static final DeferredRegister<AttachmentType<?>> ATTACHMENT_TYPES =
          DeferredRegister.create(NeoForgeRegistries.ATTACHMENT_TYPES, MultiHotbarMod.MOD_ID);

  public static final Supplier<AttachmentType<HotbarAttachment>> HOTBARS =
          ATTACHMENT_TYPES.register("hotbars", () ->
              AttachmentType.builder(HotbarAttachment::new)
                  .serialize(new IAttachmentSerializer<CompoundTag, HotbarAttachment>() {
                    @Override
                    public @NotNull HotbarAttachment read(net.neoforged.neoforge.attachment.@NotNull IAttachmentHolder holder,
                                                          @NotNull CompoundTag tag, HolderLookup.@NotNull Provider provider) {
                      HotbarAttachment attachment = new HotbarAttachment();
                      attachment.deserialize(tag, provider);
                      return attachment;
                    }

                    @Override
                    public CompoundTag write(@NotNull HotbarAttachment attachment, HolderLookup.@NotNull Provider provider) {
                      return attachment.serialize(provider);
                    }
                  })
                  .build()
          );

  public static void register(IEventBus modEventBus) {
    ATTACHMENT_TYPES.register(modEventBus);
  }
}
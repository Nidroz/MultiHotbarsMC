package com.nidro.multihotbar;

import com.nidro.multihotbar.client.KeyBindings;
import com.nidro.multihotbar.hotbar.ModAttachments;
import com.nidro.multihotbar.network.NetworkHandler;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig.Type;
import net.neoforged.fml.loading.FMLEnvironment;

@Mod(MultiHotbarMod.MOD_ID)
public class MultiHotbarMod {
  public static final String MOD_ID = "multihotbar";

  public MultiHotbarMod(IEventBus modEventBus, ModContainer modContainer) {
    // register server config (generates config/multihotbar-server.toml)
    modContainer.registerConfig(Type.SERVER, ModConfig.SPEC);

    ModAttachments.register(modEventBus);
    NetworkHandler.register(modEventBus);
    // register keybindings only on client side
    if (FMLEnvironment.dist == Dist.CLIENT) {
      KeyBindings.register(modEventBus);
    }
  }
}

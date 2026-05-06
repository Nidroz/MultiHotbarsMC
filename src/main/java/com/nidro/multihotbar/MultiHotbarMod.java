package com.nidro.multihotbar;

import com.nidro.multihotbar.client.KeyBindings;
import com.nidro.multihotbar.network.NetworkHandler;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.loading.FMLEnvironment;

@Mod(MultiHotbarMod.MOD_ID)
public class MultiHotbarMod {
  public static final String MOD_ID = "multihotbar";

  public MultiHotbarMod(IEventBus modEventBus) {
    //NetworkHandler.register(modEventBus); TODO
    // register keybinds only on client side
    if (FMLEnvironment.dist == Dist.CLIENT) {
      KeyBindings.register(modEventBus);
    }
  }
}

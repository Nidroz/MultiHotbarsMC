package com.nidro.multihotbar.client;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import org.lwjgl.glfw.GLFW;

public class KeyBindings {
  public static final KeyMapping NEXT_HOTBAR = new KeyMapping(
          "key.multihotbar.next",
          InputConstants.Type.KEYSYM,
          GLFW.GLFW_KEY_R,
          "key.categories.multihotbar"
  );

  public static final KeyMapping PREV_HOTBAR = new KeyMapping(
          "key.multihotbar.prev",
          InputConstants.Type.KEYSYM,
          GLFW.GLFW_KEY_F,
          "key.categories.multihotbar"
  );

  public static void onRegisterKeyMappings(RegisterKeyMappingsEvent event) {
    event.register(NEXT_HOTBAR);
    event.register(PREV_HOTBAR);
  }

  public static void register(IEventBus modEventBus) {
    modEventBus.addListener(KeyBindings::onRegisterKeyMappings);
  }
}

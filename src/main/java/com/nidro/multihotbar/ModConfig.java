package com.nidro.multihotbar;

import net.neoforged.neoforge.common.ModConfigSpec;

public class ModConfig {
  public static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();
  public static final ModConfigSpec SPEC;
  public static final ModConfigSpec.IntValue HOTBAR_COUNT;

  static {
    BUILDER.push("hotbars");
    HOTBAR_COUNT = BUILDER
            .comment("Number of hotbars available to the player (min 2, max 5))")
            .defineInRange("hotbarCount", 3, 2, 5);
    BUILDER.pop();
    SPEC = BUILDER.build();
  }
}

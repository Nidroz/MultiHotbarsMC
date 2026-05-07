package com.nidro.multihotbar;

import net.neoforged.neoforge.common.ModConfigSpec;

public class ModConfig {
  public static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();
  public static final ModConfigSpec SPEC;
  public static final ModConfigSpec.IntValue HOTBAR_COUNT;

  static {
    BUILDER.push("hotbars");
    HOTBAR_COUNT = BUILDER
            .comment("Number of hotbars available to the player (min 1, max 5))")
            .defineInRange("hotbarCount", 3, 1, 5);
    BUILDER.pop();
    SPEC = BUILDER.build();
  }

  // TODO: si jamais on baisse le nb de hotbars, alors qu'il y a des items dedans, on perd ces items.
  //  faudrait peut-être les drop ou les déplacer dans les emplacements libres ?à réfléchir
  //  si plus de place dans les emplacements restants, on peut les drop, sinon on les déplace dans les emplacements restants.
}

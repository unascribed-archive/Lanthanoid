package com.unascribed.lanthanoid.gen;

import net.minecraft.world.World;

public interface BlockModifier {
	int perform(World w, float x, float y, float z);
}

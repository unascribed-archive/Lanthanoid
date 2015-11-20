package com.unascribed.lanthanoid.tile;

import net.minecraft.world.World;

public interface IBreakable {
	void breakBlock(World world, int x, int y, int z);
}

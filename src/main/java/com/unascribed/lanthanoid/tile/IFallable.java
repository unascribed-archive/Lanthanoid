package com.unascribed.lanthanoid.tile;

import net.minecraft.entity.Entity;
import net.minecraft.world.World;

public interface IFallable {
	void onFallenUpon(World world, int x, int y, int z, Entity entity, float fallDistance);
}

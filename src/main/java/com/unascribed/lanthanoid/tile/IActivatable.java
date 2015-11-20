package com.unascribed.lanthanoid.tile;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;

public interface IActivatable {
	boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side, float subX, float subY, float subZ);
}

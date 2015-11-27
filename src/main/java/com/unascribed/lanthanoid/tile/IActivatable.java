package com.unascribed.lanthanoid.tile;

import net.minecraft.entity.player.EntityPlayer;

public interface IActivatable {
	boolean onBlockActivated(EntityPlayer player, int side, float subX, float subY, float subZ);
}

package com.unascribed.lanthanoid;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;

public class Generate {
	public static int spike(World world, Block b, int meta, int x, int y, int z, float xdir, float ydir, float zdir, int length) {
		boolean mode = (b != Blocks.air);
		float diameter = length / 5f;
		float posX = x;
		float posY = y;
		float posZ = z;
		int changed = 0;
		for (int i = 0; i < length; i++) {
			float radius = (diameter/2)*(1-(i/(float)length));
			for (float xCur = posX-radius; xCur < posX+radius; xCur+=0.5f) {
				for (float yCur = posY-radius; yCur < posY+radius; yCur+=0.5f) {
					for (float zCur = posZ-radius; zCur < posZ+radius; zCur+=0.5f) {
						float dx = posX - xCur;
				        float dy = posY - yCur;
				        float dz = posZ - zCur;
				        float dist = dx * dx + dy * dy + dz * dz;
				        if (dist > radius*radius) continue;
				        
						int xr = Math.round(xCur);
						int yr = Math.round(yCur);
						int zr = Math.round(zCur);
						if (world.isAirBlock(xr, yr, zr) == mode) {
							world.setBlock(xr, yr, zr, b, meta, 2);
							changed++;
						}
					}
				}
			}
			posX += xdir;
			posY += ydir;
			posZ += zdir;
		}
		return changed;
	}
}

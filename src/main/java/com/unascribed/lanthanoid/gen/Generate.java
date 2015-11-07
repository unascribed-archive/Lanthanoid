package com.unascribed.lanthanoid.gen;

import com.unascribed.lanthanoid.init.LBlocks;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;

public class Generate {
	public static int spike(World world, Block b, int meta, int x, int y, int z, float xdir, float ydir, float zdir, int length) {
		float diameter = length / 5f;
		float posX = x;
		float posY = y;
		float posZ = z;
		int changed = 0;
		for (int i = 0; i < length; i++) {
			float radius = (diameter/2)*(1-(i/(float)length));
			changed += sphere(world, b, meta, posX, posY, posZ, radius);
			posX += xdir;
			posY += ydir;
			posZ += zdir;
		}
		return changed;
	}
	
	public static int nacelle(World world, Block b, int meta, int oX, int oY, int oZ, float xdir, float ydir, float zdir) {
		float posX = oX;
		float posY = oY;
		float posZ = oZ;
		int changed = 0;
		for (int i = 0; i < 10; i++) {
			changed += diamond(world, b, meta, posX, posY, posZ);
			
			posX += xdir;
			posY += ydir;
			posZ += zdir;
		}
		changed += diamond(world, LBlocks.energized_lutetium, 0, posX, posY, posZ);
		return changed;
	}
	
	public static int powerCell(World world, int oX, int oY, int oZ, float xdir, float ydir, float zdir) {
		float posX = oX;
		float posY = oY;
		float posZ = oZ;
		int changed = 0;
		changed += cube(world, LBlocks.plating, LBlocks.plating.getMetaForName("platingLutetium"), posX-2, posY-2, posZ-2, posX+1, posY+1, posZ+1);
		changed += cube(world, LBlocks.plating, LBlocks.plating.getMetaForName("platingLutetium"),
				posX+(xdir*7)-2, posY+(ydir*7)-2, posZ+(zdir*7)-2,
				posX+(xdir*7)+1, posY+(ydir*7)+1, posZ+(zdir*7)+1);
		posX += xdir;
		posY += ydir;
		posZ += zdir;
		for (int i = 0; i < 6; i++) {
			for (float xCur = posX-2; xCur < posX+1; xCur+=0.5f) {
				for (float yCur = posY-2; yCur < posY+1; yCur+=0.5f) {
					for (float zCur = posZ-2; zCur < posZ+1; zCur+=0.5f) {
						int xr = Math.round(xCur);
						int yr = Math.round(yCur);
						int zr = Math.round(zCur);
						if (world.getBlock(xr, yr, zr) == LBlocks.plating || world.getBlock(xr, yr, zr).isReplaceable(world, xr, yr, zr) || world.isAirBlock(xr, yr, zr)) {
							if (world.rand.nextInt(4) != 0) {
								world.setBlock(xr, yr, zr, Blocks.air, 0, 2);
							} else {
								world.setBlock(xr, yr, zr, LBlocks.energized_lutetium, 0, 2);
								changed++;
							}
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

	public static int diamond(World world, Block b, int meta, float posX, float posY, float posZ) {
		int changed = 0;
		BlockModifier set = (w, x, y, z) -> {
			w.setBlock((int)x, (int)y, (int)z, b, meta, 2);
			return 1;
		};
		changed += set.perform(world, posX, posY, posZ);
		
		changed += set.perform(world, posX-1, posY, posZ);
		changed += set.perform(world, posX, posY-1, posZ);
		changed += set.perform(world, posX, posY, posZ-1);
		changed += set.perform(world, posX+1, posY, posZ);
		changed += set.perform(world, posX, posY+1, posZ);
		changed += set.perform(world, posX, posY, posZ+1);
		return changed;
	}

	public static int cube(World world, Block b, int meta, float startX, float startY, float startZ, float endX, float endY, float endZ) {
		int changed = 0;
		boolean mode = (b != Blocks.air);
		for (float xCur = startX; xCur < endX; xCur+=0.5f) {
			for (float yCur = startY; yCur < endY; yCur+=0.5f) {
				for (float zCur = startZ; zCur < endZ; zCur+=0.5f) {
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
		return changed;
	}
	
	public static int sphere(World world, Block b, int meta, float posX, float posY, float posZ, float radius) {
		int changed = 0;
		boolean mode = (b != Blocks.air);
		for (float xCur = posX-radius; xCur < posX+radius; xCur+=0.5f) {
			for (float yCur = posY-radius; yCur < posY+radius; yCur+=0.5f) {
				for (float zCur = posZ-radius; zCur < posZ+radius; zCur+=0.5f) {
					float dx = posX - xCur;
					float dy = posY - yCur;
					float dz = posZ - zCur;
					float dist = dx * dx + dy * dy + dz * dz;
					if (dist > radius * radius) continue;
					
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
		return changed;
	}
	
	
}

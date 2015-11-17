package com.unascribed.lanthanoid.gen;

import java.util.Random;

import cpw.mods.fml.common.FMLLog;
import cpw.mods.fml.common.IWorldGenerator;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.feature.WorldGenMinable;

public class OreGenerator implements IWorldGenerator {
	public static final int NETHER = -1;
	public static final int OVERWORLD = 0;
	public static final int THE_END = 1;
	
	private String name;
	private Block target = Blocks.stone;
	private Block block = null;
	private int meta = -1;
	private int minHeight = 0;
	private int maxHeight = 64;
	private int size = -1;
	private int frequency = -1;
	private int dimension = 0;

	private OreGenerator(String name) {
		this.name = name;
	}

	@Override
	public void generate(Random random, int chunkX, int chunkZ, World world, IChunkProvider chunkGenerator, IChunkProvider chunkProvider) {
		if (world.provider.dimensionId == dimension) {
			boolean didAnything = false;
			for (int i = 0; i < frequency; i++) {
				int x = (chunkX*16) + random.nextInt(16);
				int y = random.nextInt(maxHeight-minHeight)+minHeight;
				int z = (chunkZ*16) + random.nextInt(16);
				
				
				float f = random.nextFloat() * (float)Math.PI;
				double d0 = (double)((float)(x + 8) + MathHelper.sin(f) * (float)size / 8.0F);
				double d1 = (double)((float)(x + 8) - MathHelper.sin(f) * (float)size / 8.0F);
				double d2 = (double)((float)(z + 8) + MathHelper.cos(f) * (float)size / 8.0F);
				double d3 = (double)((float)(z + 8) - MathHelper.cos(f) * (float)size / 8.0F);
				double d4 = (double)(y + random.nextInt(3) - 2);
				double d5 = (double)(y + random.nextInt(3) - 2);
				
				for (int l = 0; l <= size; ++l) {
					double d6 = d0 + (d1 - d0) * (double) l / (double) size;
					double d7 = d4 + (d5 - d4) * (double) l / (double) size;
					double d8 = d2 + (d3 - d2) * (double) l / (double) size;
					double d9 = random.nextDouble() * (double) size / 16.0D;
					double d10 = (double) (MathHelper.sin((float) l * (float) Math.PI / (float) size) + 1.0F) * d9 + 1.0D;
					double d11 = (double) (MathHelper.sin((float) l * (float) Math.PI / (float) size) + 1.0F) * d9 + 1.0D;
					int i1 = MathHelper.floor_double(d6 - d10 / 2.0D);
					int j1 = MathHelper.floor_double(d7 - d11 / 2.0D);
					int k1 = MathHelper.floor_double(d8 - d10 / 2.0D);
					int l1 = MathHelper.floor_double(d6 + d10 / 2.0D);
					int i2 = MathHelper.floor_double(d7 + d11 / 2.0D);
					int j2 = MathHelper.floor_double(d8 + d10 / 2.0D);

					for (int k2 = i1; k2 <= l1; ++k2) {
						double d12 = ((double) k2 + 0.5D - d6) / (d10 / 2.0D);

						if (d12 * d12 < 1.0D) {
							for (int l2 = j1; l2 <= i2; ++l2) {
								double d13 = ((double) l2 + 0.5D - d7) / (d11 / 2.0D);

								if (d12 * d12 + d13 * d13 < 1.0D) {
									for (int i3 = k1; i3 <= j2; ++i3) {
										double d14 = ((double) i3 + 0.5D - d8) / (d10 / 2.0D);

										if (d12 * d12 + d13 * d13 + d14 * d14 < 1.0D && world.getBlock(k2, l2, i3).isReplaceableOreGen(world, k2, l2, i3, target)) {
											world.setBlock(k2, l2, i3, block, meta, 2);
											didAnything = true;
										}
									}
								}
							}
						}
					}
				}
			}
			if (!didAnything) {
				//FMLLog.bigWarning("COULD NOT GENERATE ANY "+name.toUpperCase()+" AT "+chunkX+", "+chunkZ+" IN DIMENSION "+world.provider.dimensionId);
			}
		}
	}
	
	@Override
	public String toString() {
		return name+" Ore Generator";
	}
	
	public OreGenerator target(Block target) {
		this.target = target;
		return this;
	}
	
	public OreGenerator block(Block block, int meta) {
		this.block = block;
		this.meta = meta;
		return this;
	}
	
	public OreGenerator range(int minHeight, int maxHeight) {
		this.minHeight = minHeight;
		this.maxHeight = maxHeight;
		return this;
	}
	
	public OreGenerator dimension(int dimension) {
		this.dimension = dimension;
		return this;
	}
	
	public OreGenerator frequency(int frequency) {
		this.frequency = frequency;
		return this;
	}
	
	public OreGenerator size(int size) {
		this.size = size;
		return this;
	}

	public static OreGenerator create(String name) {
		return new OreGenerator(name);
	}

}

package com.unascribed.lanthanoid;

import java.util.Random;

import cpw.mods.fml.common.IWorldGenerator;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
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
	
	private boolean dirty = true;

	private WorldGenMinable gen;
	
	private OreGenerator(String name) {
		this.name = name;
	}

	@Override
	public void generate(Random random, int chunkX, int chunkZ, World world, IChunkProvider chunkGenerator, IChunkProvider chunkProvider) {
		if (dirty) {
			if (block == null || meta == -1 || size == -1 || frequency == -1) {
				throw new IllegalStateException("OreGenerator is incomplete");
			}
			gen = new WorldGenMinable(block, meta, size, target);
			dirty = false;
		}
		
		if (world.provider.dimensionId == dimension) {
			for (int i = 0; i < frequency; i++) {
				int x = (chunkX*16) + random.nextInt(16);
				int y = random.nextInt(maxHeight-minHeight)+minHeight;
				int z = (chunkZ*16) + random.nextInt(16);
				gen.generate(world, random, x, y, z);
			}
		}
	}
	
	@Override
	public String toString() {
		return name+" Ore Generator";
	}
	
	public OreGenerator target(Block target) {
		this.target = target;
		dirty = true;
		return this;
	}
	
	public OreGenerator block(Block block, int meta) {
		this.block = block;
		this.meta = meta;
		dirty = true;
		return this;
	}
	
	public OreGenerator range(int minHeight, int maxHeight) {
		this.minHeight = minHeight;
		this.maxHeight = maxHeight;
		dirty = true;
		return this;
	}
	
	public OreGenerator dimension(int dimension) {
		this.dimension = dimension;
		dirty = true;
		return this;
	}
	
	public OreGenerator frequency(int frequency) {
		this.frequency = frequency;
		dirty = true;
		return this;
	}
	
	public OreGenerator size(int size) {
		this.size = size;
		dirty = true;
		return this;
	}

	public static OreGenerator create(String name) {
		return new OreGenerator(name);
	}

}

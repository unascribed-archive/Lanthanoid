package com.unascribed.lanthanoid.gen;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Random;

import cpw.mods.fml.common.IWorldGenerator;
import net.minecraft.world.World;
import net.minecraft.world.chunk.IChunkProvider;

public class GeneratorGroup extends ArrayList<IWorldGenerator> implements IWorldGenerator {
	private static final long serialVersionUID = -840506724968382866L;

	public GeneratorGroup() {}
	public GeneratorGroup(IWorldGenerator... generators) {
		for (IWorldGenerator g : generators) {
			add(g);
		}
	}
	public GeneratorGroup(Collection<? extends IWorldGenerator> generators) {
		addAll(generators);
	}
	
	@Override
	public void generate(Random random, int chunkX, int chunkZ, World world, IChunkProvider chunkGenerator, IChunkProvider chunkProvider) {
		for (IWorldGenerator gen : this) {
			gen.generate(random, chunkX, chunkZ, world, chunkGenerator, chunkProvider);
		}
	}
}

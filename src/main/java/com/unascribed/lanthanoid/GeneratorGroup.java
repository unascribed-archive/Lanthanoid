package com.unascribed.lanthanoid;

import java.util.Collection;
import java.util.List;
import java.util.Random;

import com.google.common.collect.Lists;

import cpw.mods.fml.common.IWorldGenerator;
import net.minecraft.world.World;
import net.minecraft.world.chunk.IChunkProvider;

public class GeneratorGroup implements IWorldGenerator {
	private List<IWorldGenerator> generators = Lists.newArrayList();
	public GeneratorGroup() {}
	public GeneratorGroup(IWorldGenerator... generators) {
		for (IWorldGenerator g : generators) {
			this.generators.add(g);
		}
	}
	public GeneratorGroup(Collection<? extends IWorldGenerator> generators) {
		this.generators.addAll(generators);
	}
	
	public void add(IWorldGenerator gen) {
		generators.add(gen);
	}
	
	@Override
	public void generate(Random random, int chunkX, int chunkZ, World world, IChunkProvider chunkGenerator, IChunkProvider chunkProvider) {
		for (IWorldGenerator gen : generators) {
			gen.generate(random, chunkX, chunkZ, world, chunkGenerator, chunkProvider);
		}
	}
}

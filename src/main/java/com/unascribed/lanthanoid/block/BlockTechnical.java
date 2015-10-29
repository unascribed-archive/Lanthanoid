package com.unascribed.lanthanoid.block;

import java.util.Random;

import net.minecraft.block.BlockAir;
import net.minecraft.init.Blocks;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockTechnical extends BlockAir {

	public BlockTechnical() {
		setTickRandomly(true);
	}
	
	@Override
	public int getLightValue(IBlockAccess world, int x, int y, int z) {
		return world.getBlockMetadata(x, y, z) == 0 ? 15 : 0;
	}
	
	@Override
	public void updateTick(World world, int x, int y, int z, Random rand) {
		world.setBlock(x, y, z, Blocks.air, 0, 2);
	}
}

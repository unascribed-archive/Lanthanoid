package com.unascribed.lanthanoid.block;

import java.util.Random;

import com.unascribed.lanthanoid.init.LBlocks;
import com.unascribed.lanthanoid.init.LItems;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.BlockRedstoneDiode;
import net.minecraft.block.BlockRedstoneRepeater;
import net.minecraft.item.Item;
import net.minecraft.world.World;

public class BlockCobbleRepeater extends BlockRedstoneRepeater {

	public BlockCobbleRepeater(boolean p_i45424_1_) {
		super(p_i45424_1_);
	}

	@Override
	protected BlockRedstoneDiode getBlockPowered() {
		return LBlocks.powered_cobble_repeater;
	}

	@Override
	protected BlockRedstoneDiode getBlockUnpowered() {
		return LBlocks.unpowered_cobble_repeater;
	}

	@Override
	public Item getItemDropped(int meta, Random random, int fortune) {
		return LItems.cobble_repeater;
	}

	/**
	 * Gets an item for the block being called on. Args: world, x, y, z
	 */
	@Override
	@SideOnly(Side.CLIENT)
	public Item getItem(World worldIn, int x, int y, int z) {
		return LItems.cobble_repeater;
	}

}

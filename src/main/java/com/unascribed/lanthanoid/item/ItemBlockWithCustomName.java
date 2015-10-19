package com.unascribed.lanthanoid.item;

import com.unascribed.lanthanoid.util.NameDelegate;

import net.minecraft.block.Block;
import net.minecraft.item.ItemBlockWithMetadata;
import net.minecraft.item.ItemStack;

public class ItemBlockWithCustomName extends ItemBlockWithMetadata {
	private final NameDelegate block;
	public ItemBlockWithCustomName(Block block) {
		super(block, block);
		this.block = (NameDelegate) block;
	}
	
	@Override
	public String getItemStackDisplayName(ItemStack p_77653_1_) {
		return block.getItemStackDisplayName(p_77653_1_);
	}
}

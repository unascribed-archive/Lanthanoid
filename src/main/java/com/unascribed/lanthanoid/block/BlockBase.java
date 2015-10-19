package com.unascribed.lanthanoid.block;

import com.unascribed.lanthanoid.Lanthanoid;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;

public class BlockBase extends Block {

	protected BlockBase(Material materialIn) {
		super(materialIn);
		setCreativeTab(Lanthanoid.inst.creativeTab);
	}

}

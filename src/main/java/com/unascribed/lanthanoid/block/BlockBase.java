package com.unascribed.lanthanoid.block;

import com.unascribed.lanthanoid.Lanthanoid;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;

public class BlockBase extends Block {

	protected BlockBase(CreativeTabs tab, Material materialIn) {
		super(materialIn);
		setCreativeTab(tab);
	}

}

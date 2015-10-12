package com.unascribed.lanthanoid;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.util.IIcon;

public class BlockOre extends Block {
	private static String[] names = {
			"copper",
			"holm",
			"neodym",
			"praseodym",
			"thulite",
			"ytterb",
			"yttr"
	};
	
	private IIcon[] icons = new IIcon[names.length];
	
	protected BlockOre() {
		super(Material.rock);
	}
	
	@Override
	public IIcon getIcon(int side, int meta) {
		return icons[meta];
	}
	
	@Override
	public void registerIcons(IIconRegister reg) {
		for (int i = 0; i < names.length; i++) {
			icons[i] = reg.registerIcon("lanthanoid_compositor:"+names[i]);
		}
	}

}

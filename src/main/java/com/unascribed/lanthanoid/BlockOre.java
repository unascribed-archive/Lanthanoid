package com.unascribed.lanthanoid;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;

public class BlockOre extends Block implements NameDelegate {
	private static String[] names = {
			"copper",
			"yttr",
			"ytterb",
			"praseodym",
			"neodym",
			"holm",
			"yttr",
			"bar",
			"europ",
			"gadolin",
			"dyspros",
			"actinolite",
			"diaspore",
			"raspite",
			"rosasite",
			"thulite"
	};
	
	private IIcon[] icons = new IIcon[names.length];
	
	protected BlockOre() {
		super(Material.rock);
		setCreativeTab(Lanthanoid.inst.creativeTab);
	}
	
	@Override
	public IIcon getIcon(int side, int meta) {
		return icons[meta];
	}
	
	@Override
	public void getSubBlocks(Item itemIn, CreativeTabs tab, List list) {
		for (int i = 0; i < names.length; i++) {
			list.add(new ItemStack(itemIn, 1, i));
		}
	}
	
	@Override
	public void registerIcons(IIconRegister reg) {
		for (int i = 0; i < names.length; i++) {
			icons[i] = reg.registerIcon("lanthanoid_compositor:"+names[i]);
		}
	}

	@Override
	public String getUnlocalizedName(int meta) {
		return "tile.ore_"+names[meta];
	}

}

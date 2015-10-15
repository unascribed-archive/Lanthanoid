package com.unascribed.lanthanoid;

import java.util.List;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraftforge.oredict.OreDictionary;

public class BlockOre extends Block implements NameDelegate {
	private String[] names;
	private IIcon errorIcon;
	private IIcon[] icons;
	
	protected BlockOre(String... names) {
		super(Material.rock);
		this.names = names;
		icons = new IIcon[names.length];
		setCreativeTab(Lanthanoid.inst.creativeTab);
		setHardness(3);
		setHarvestLevel("pickaxe", 2);
		for (int i = 0; i < names.length; i++) {
			OreDictionary.registerOre(names[i], new ItemStack(this, 1, i));
		}
	}
	
	@Override
	public Item getItemDropped(int meta, Random random, int fortune) {
		return Item.getItemFromBlock(this);
	}
	
	@Override
	public int damageDropped(int meta) {
		return meta;
	}
	
	@Override
	public IIcon getIcon(int side, int meta) {
		if (meta < 0 || meta >= icons.length) {
			return errorIcon;
		}
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
		errorIcon = reg.registerIcon("lanthanoid:error");
		for (int i = 0; i < names.length; i++) {
			icons[i] = reg.registerIcon("lanthanoid_compositor:"+names[i]);
		}
	}

	@Override
	public String getUnlocalizedName(int meta) {
		if (meta < 0 || meta >= names.length) return "tile.error";
		return "tile."+names[meta];
	}

}

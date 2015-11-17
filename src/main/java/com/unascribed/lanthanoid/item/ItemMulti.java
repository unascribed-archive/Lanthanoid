package com.unascribed.lanthanoid.item;

import java.util.List;
import com.google.common.collect.ImmutableList;
import com.unascribed.lanthanoid.util.MultiHelper;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraftforge.oredict.OreDictionary;

public class ItemMulti extends ItemBase {
	protected MultiHelper helper;
	
	protected IIcon errorIcon;
	protected IIcon[] icons;
	protected boolean useCompositor = true;
	
	public ItemMulti(String... names) {
		helper = new MultiHelper("item", names);
		icons = new IIcon[names.length];
	}
	
	public boolean hasName(String name) {
		return helper.hasName(name);
	}
	
	public int getMetaForName(String name) {
		return helper.getMetaForName(name);
	}
	
	public ItemStack getStackForName(String name) {
		return getStackForName(name, 1);
	}
	
	public ItemStack getStackForName(String name, int amount) {
		return new ItemStack(this, amount, getMetaForName(name));
	}
	
	public String getNameForMeta(int meta) {
		return helper.getNameForMeta(meta);
	}
	
	public ItemMulti disableCompositor() {
		useCompositor = false;
		return this;
	}
	
	@Override
	public String getUnlocalizedName(ItemStack stack) {
		return helper.getUnlocalizedNameForMeta(stack.getItemDamage());
	}
	
	@Override
	public IIcon getIconFromDamage(int meta) {
		if (meta < 0 || meta >= icons.length) {
			return errorIcon;
		}
		return icons[meta];
	}
	
	@Override
	public void getSubItems(Item itemIn, CreativeTabs tab, List list) {
		for (int i = 0; i < helper.getNames().size(); i++) {
			list.add(new ItemStack(itemIn, 1, i));
		}
	}
	
	@Override
	public String getItemStackDisplayName(ItemStack p_77653_1_) {
		return helper.getDisplayNameForMeta(p_77653_1_.getItemDamage());
	}
	
	@Override
	public boolean getHasSubtypes() {
		return true;
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public void registerIcons(IIconRegister reg) {
		errorIcon = reg.registerIcon("lanthanoid:error");
		ImmutableList<String> names = helper.getNames();
		for (int i = 0; i < names.size(); i++) {
			String domain;
			if (useCompositor) {
				domain = "lanthanoid_compositor";
			} else {
				domain = "lanthanoid";
			}
			icons[i] = reg.registerIcon(domain+":"+names.get(i));
		}
	}
	
	public void registerOres() {
		ImmutableList<String> names = helper.getNames();
		for (int i = 0; i < names.size(); i++) {
			OreDictionary.registerOre(names.get(i), new ItemStack(this, 1, i));
		}
	}
	
}

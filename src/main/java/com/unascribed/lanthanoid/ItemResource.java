package com.unascribed.lanthanoid;

import java.util.List;

import gnu.trove.map.TObjectIntMap;
import gnu.trove.map.hash.TObjectIntHashMap;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraftforge.oredict.OreDictionary;

public class ItemResource extends Item {
	private String[] names;
	private TObjectIntMap<String> reverseNames;
	private IIcon errorIcon;
	private IIcon[] icons;
	
	protected ItemResource(String... names) {
		this.names = names;
		icons = new IIcon[names.length];
		reverseNames = new TObjectIntHashMap<>(names.length);
		for (int i = 0; i < names.length; i++) {
			reverseNames.put(names[i], i);
		}
		setCreativeTab(Lanthanoid.inst.creativeTab);
	}
	
	public int getMetaForName(String name) {
		return reverseNames.get(name);
	}
	
	@Override
	public boolean getHasSubtypes() {
		return true;
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
	public String getUnlocalizedName(ItemStack stack) {
		int meta = stack.getItemDamage();
		if (meta < 0 || meta >= names.length) return "tile.error";
		return "item."+names[meta];
	}
	
	public void registerOres() {
		for (int i = 0; i < names.length; i++) {
			OreDictionary.registerOre(names[i], new ItemStack(this, 1, i));
		}
	}
}

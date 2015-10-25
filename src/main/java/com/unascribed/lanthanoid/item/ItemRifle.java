package com.unascribed.lanthanoid.item;

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;

public class ItemRifle extends ItemBase {
	private IIcon base;
	private IIcon[] overlays = new IIcon[12];
	
	public ItemRifle() {
		setUnlocalizedName("rifle");
		setMaxStackSize(1);
	}
	
	@Override
	public IIcon getIconFromDamage(int meta) {
		return base;
	}
	
	@Override
	public IIcon getIcon(ItemStack stack, int pass) {
		return pass == 0 ? base : overlays[0];
	}
	
	@Override
	public void registerIcons(IIconRegister register) {
		base = register.registerIcon("lanthanoid_compositor:rifle");
		for (int i = 0; i < overlays.length; i++) {
			overlays[i] = register.registerIcon("lanthanoid:rifle_overlay_"+i);
		}
	}
}

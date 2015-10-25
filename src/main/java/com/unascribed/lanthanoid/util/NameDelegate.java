package com.unascribed.lanthanoid.util;

import net.minecraft.item.ItemStack;

public interface NameDelegate {

	public String getUnlocalizedName(ItemStack stack);
	
	public String getItemStackDisplayName(ItemStack stack);

}

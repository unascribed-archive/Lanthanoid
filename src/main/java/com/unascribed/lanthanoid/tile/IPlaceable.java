package com.unascribed.lanthanoid.tile;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;

public interface IPlaceable {
	void onBlockPlacedBy(EntityLivingBase placer, ItemStack stack);
}

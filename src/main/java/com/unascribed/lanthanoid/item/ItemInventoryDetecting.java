package com.unascribed.lanthanoid.item;

import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

public class ItemInventoryDetecting extends ItemBase {
	@Override
	public void onUpdate(ItemStack stack, World world, Entity entity, int slot, boolean held) {
		if (!stack.hasTagCompound()) {
			stack.setTagCompound(new NBTTagCompound());
		}
		stack.getTagCompound().setBoolean("HasBeenInInventory", true);
	}
}

package com.unascribed.lanthanoid.item;

import java.util.List;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

public class ItemDisabled extends ItemBase {
	public ItemDisabled(CreativeTabs tab) {
		super(tab);
		setTextureName("lanthanoid:disabled");
		setHasSubtypes(true);
	}
	@Override
	public void addInformation(ItemStack stack, EntityPlayer player, List list, boolean advanced) {
		list.add("\u00a7cDisabled in the config file.");
	}
}

package com.unascribed.lanthanoid.item;

import com.unascribed.lanthanoid.Lanthanoid;

import net.minecraft.item.Item;

public class ItemBase extends Item {
	public ItemBase() {
		setCreativeTab(Lanthanoid.inst.creativeTab);
	}
}

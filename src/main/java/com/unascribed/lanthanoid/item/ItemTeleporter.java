package com.unascribed.lanthanoid.item;

import net.minecraft.item.ItemStack;

public class ItemTeleporter extends ItemMulti {
	public static String[] flavors = {
			"Dysprosium",
			"Erbium",
			"Gadolinium",
			"Holmium",
			"Neodymium",
			"Praseodymium",
			"Ytterbium"
	};
	public ItemTeleporter() {
		super(prefix(flavors));
	}
	private static String[] prefix(String[] arr) {
		String[] rtrn = new String[arr.length];
		for (int i = 0; i < arr.length; i++) {
			rtrn[i] = "teleporter"+arr[i];
		}
		return rtrn;
	}
	@Override
	public int getItemStackLimit(ItemStack stack) {
		return 1;
	}
}

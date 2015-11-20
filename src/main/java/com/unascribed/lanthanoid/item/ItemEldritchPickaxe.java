package com.unascribed.lanthanoid.item;

import com.unascribed.lanthanoid.glyph.IGlyphHolderItem;

import net.minecraft.item.ItemPickaxe;
import net.minecraft.item.ItemStack;

public class ItemEldritchPickaxe extends ItemPickaxe implements IGlyphHolderItem {

	protected ItemEldritchPickaxe(ToolMaterial mat) {
		super(mat);
	}

	@Override
	public int getMaxMilliglyphs(ItemStack stack) {
		return 200;
	}
	
}

package com.unascribed.lanthanoid.glyph;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

public interface IGlyphHolderItem {
	default int getMilliglyphs(ItemStack stack) {
		return stack.hasTagCompound() ?
				stack.getTagCompound().getInteger("MilliGlyphs") : 0;
	};
	int getMaxMilliglyphs(ItemStack stack);
	default void setMilliglyphs(ItemStack stack, int milliglyphs) {
		if (!stack.hasTagCompound()) {
			stack.setTagCompound(new NBTTagCompound());
		}
		stack.getTagCompound().setInteger("MilliGlyphs", milliglyphs);
	};
}

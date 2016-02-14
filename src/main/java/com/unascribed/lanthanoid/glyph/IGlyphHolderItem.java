package com.unascribed.lanthanoid.glyph;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.IIcon;

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
	IIcon getGlyphs(ItemStack stack);
	float getGlyphColorRed(ItemStack stack);
	float getGlyphColorGreen(ItemStack stack);
	float getGlyphColorBlue(ItemStack stack);
	float getGlyphColorAlpha(ItemStack stack);
	default int getGlyphColor(ItemStack stack) {
		int i = 0;
		i |= ((int)((getGlyphColorRed(stack)*255))&0xFF)<<16;
		i |= ((int)((getGlyphColorGreen(stack)*255))&0xFF)<<8;
		i |= (int)((getGlyphColorBlue(stack)*255))&0xFF;
		i |= ((int)((getGlyphColorAlpha(stack)*255))&0xFF)<<24;
		return i;
	}
}

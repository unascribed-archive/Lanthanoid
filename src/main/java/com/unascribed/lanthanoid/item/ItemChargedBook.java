package com.unascribed.lanthanoid.item;

import com.unascribed.lanthanoid.glyph.IGlyphHolderItem;

import net.minecraft.item.ItemStack;

public class ItemChargedBook extends ItemBase implements IGlyphHolderItem {

	public ItemChargedBook() {
		super(null);
		setUnlocalizedName("charged_book");
		setTextureName("minecraft:book_normal");
		setMaxStackSize(1);
	}
	
	@Override
	public boolean hasEffect(ItemStack par1ItemStack, int pass) {
		return getMilliglyphs(par1ItemStack) > 10000;
	}
	
	@Override
	public boolean showDurabilityBar(ItemStack stack) {
		return true;
	}
	
	@Override
	public double getDurabilityForDisplay(ItemStack stack) {
		return 1-((double)getMilliglyphs(stack)/(double)getMaxMilliglyphs(stack));
	}
	
	@Override
	public int getMaxMilliglyphs(ItemStack stack) {
		return 20_000;
	}

}

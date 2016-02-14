package com.unascribed.lanthanoid.item;

import com.unascribed.lanthanoid.glyph.IGlyphHolderItem;

import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;

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
	
	@Override
	public IIcon getGlyphs(ItemStack is) {
		return null;
	}
	
	@Override
	public float getGlyphColorRed(ItemStack is) {
		return GlyphItemHelper.getDefaultGlyphColorRed(this, is);
	}
	
	@Override
	public float getGlyphColorGreen(ItemStack is) {
		return GlyphItemHelper.getDefaultGlyphColorGreen(this, is);
	}
	
	@Override
	public float getGlyphColorBlue(ItemStack is) {
		return GlyphItemHelper.getDefaultGlyphColorBlue(this, is);
	}
	
	@Override
	public float getGlyphColorAlpha(ItemStack is) {
		return GlyphItemHelper.getDefaultGlyphColorAlpha(this, is);
	}

}

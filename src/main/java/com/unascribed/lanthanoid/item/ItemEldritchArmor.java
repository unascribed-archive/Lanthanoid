package com.unascribed.lanthanoid.item;

import com.unascribed.lanthanoid.Lanthanoid;
import com.unascribed.lanthanoid.glyph.IGlyphHolderItem;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;

public class ItemEldritchArmor extends ItemArmor implements IGlyphHolderItem {

	public ItemEldritchArmor(ArmorMaterial material, int armorType) {
		super(material, 1, armorType);
		setCreativeTab(Lanthanoid.inst.creativeTabEquipment);
		setUnlocalizedName("eldritch_"+getArmorTypeName());
		setTextureName("lanthanoid:eldritch_"+getArmorTypeName());
	}
	
	private IIcon glyphs;
	
	private String getArmorTypeName() {
		switch (armorType) {
			case 0: return "helmet";
			case 1: return "chestplate";
			case 2: return "leggings";
			case 3: return "boots";
			default: return null;
		}
	}
	
	public IIcon getGlyphs() {
		return glyphs;
	}
	
	@Override
	public int getMaxMilliglyphs(ItemStack stack) {
		return 500_000;
	}
	
	@Override
	public String getArmorTexture(ItemStack stack, Entity entity, int slot, String type) {
		return armorType == 2 ? "lanthanoid:textures/models/eldritch_layer_2.png" : "lanthanoid:textures/models/eldritch_layer_1.png";
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public void registerIcons(IIconRegister register) {
		super.registerIcons(register);
		switch (armorType) {
			case 0: {
				glyphs = register.registerIcon("lanthanoid:eldritch_glyph_heal"); 
				break;
			}
			case 1: {
				glyphs = register.registerIcon("lanthanoid:eldritch_glyph_help"); 
				break;
			}
			case 2: {
				glyphs = register.registerIcon("lanthanoid:eldritch_glyph_absorb"); 
				break;
			}
			case 3: {
				glyphs = register.registerIcon("lanthanoid:eldritch_glyph_fast"); 
				break;
			}
		}
	}

}

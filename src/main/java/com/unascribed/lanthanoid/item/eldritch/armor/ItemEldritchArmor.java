package com.unascribed.lanthanoid.item.eldritch.armor;

import java.util.List;

import com.unascribed.lanthanoid.Lanthanoid;
import com.unascribed.lanthanoid.glyph.IGlyphHolderItem;
import com.unascribed.lanthanoid.item.GlyphToolHelper;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;

public abstract class ItemEldritchArmor extends ItemArmor implements IGlyphHolderItem {

	private boolean enhanced;
	
	public ItemEldritchArmor(ArmorMaterial material, int armorType, boolean enhanced) {
		super(material, 1, armorType);
		this.enhanced = enhanced;
		setCreativeTab(Lanthanoid.inst.creativeTabEquipment);
		setUnlocalizedName("eldritch_"+getArmorTypeName());
		if (armorType == 0 && enhanced) {
			setTextureName("lanthanoid:eldritch_helmet_raspite");
		} else {
			setTextureName("lanthanoid:eldritch_"+getArmorTypeName());
		}
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
		if (armorType == 0) {
			if (enhanced) {
				return "lanthanoid:textures/models/eldritch_helmet_raspite.png";
			} else {
				return "lanthanoid:textures/models/eldritch_helmet.png";
			}
		}
		return armorType == 2 ? "lanthanoid:textures/models/eldritch_layer_2.png" : "lanthanoid:textures/models/eldritch_layer_1.png";
	}
	
	@Override
	public void addInformation(ItemStack stack, EntityPlayer player, List list, boolean advanced) {
		GlyphToolHelper.doAddInformation(this, stack, player, list, advanced);
		list.add("");
		list.add((hasSetBonus(player) ? "\u00A79" : "\u00A78")+StatCollector.translateToLocal("ui.eldritch_set_bonus"));
	}
	
	@Override
	public void onArmorTick(World world, EntityPlayer player, ItemStack stack) {
		GlyphToolHelper.doUpdate(this, stack, world, player, 3-armorType, true);
	}
	
	@Override
	public void onUpdate(ItemStack stack, World world, Entity entity, int slot, boolean equipped) {
		GlyphToolHelper.doUpdate(this, stack, world, entity, slot, equipped);
		if (entity instanceof EntityPlayer) {
			EntityPlayer player = (EntityPlayer)entity;
			if (world.isRemote && FMLCommonHandler.instance().getSide().isClient()) {
				if (hasSetBonus(player)) {
					if (!(entity == Minecraft.getMinecraft().thePlayer && Minecraft.getMinecraft().gameSettings.thirdPersonView == 0)) { 
						world.spawnParticle("enchantmenttable", entity.posX+(world.rand.nextGaussian()*(entity.width/2)), (entity.posY-0.8)+(world.rand.nextGaussian()*(entity.height/2)), entity.posZ+(world.rand.nextGaussian()*(entity.width/2)), 0, 0, 0);
					}
				}
			}
		}
	}
	
	public static boolean hasSetBonus(EntityPlayer player) {
		for (ItemStack is : player.inventory.armorInventory) {
			if (is == null || !(is.getItem() instanceof ItemEldritchArmor)) {
				return false;
			}
		}
		return true;
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

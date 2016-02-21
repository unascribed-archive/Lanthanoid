package com.unascribed.lanthanoid.item.eldritch.armor;

import java.util.List;

import com.unascribed.lanthanoid.Lanthanoid;
import com.unascribed.lanthanoid.glyph.IGlyphHolderItem;
import com.unascribed.lanthanoid.item.GlyphItemHelper;
import com.unascribed.lanthanoid.tile.TileEntityEldritchInductor;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
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
	
	@Override
	public IIcon getGlyphs(ItemStack stack) {
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
		GlyphItemHelper.doAddInformation(this, stack, player, list, advanced);
		list.add("");
		list.add((hasSetBonus(player) ? "\u00A79" : "\u00A78")+StatCollector.translateToLocal("ui.eldritch_set_bonus"));
	}
	
	@Override
	public void onArmorTick(World world, EntityPlayer player, ItemStack stack) {
		GlyphItemHelper.doUpdate(this, stack, world, player, 3-armorType, true);
	}
	
	@Override
	public void onUpdate(ItemStack stack, World world, Entity entity, int slot, boolean equipped) {
		GlyphItemHelper.doUpdate(this, stack, world, entity, slot, equipped);
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
	
	@Override
	public ItemStack onItemRightClick(ItemStack itemStackIn, World worldIn, EntityPlayer player) {
		return itemStackIn;
	}
	
	@Override
	public boolean onItemUse(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side, float subX, float subY, float subZ) {
		TileEntity te = world.getTileEntity(x, y, z);
		if (te instanceof TileEntityEldritchInductor) {
			if (subY >= ((int)((31/32D)*16))/16f) {
				return false;
			}
		}
		player.setCurrentItemOrArmor(0, super.onItemRightClick(stack, world, player));
		return false;
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

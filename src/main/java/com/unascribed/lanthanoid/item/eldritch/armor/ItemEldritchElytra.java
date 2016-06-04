package com.unascribed.lanthanoid.item.eldritch.armor;

import java.util.List;

import com.unascribed.backlytra.ItemElytra;
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
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;

public class ItemEldritchElytra extends ItemElytra implements IGlyphHolderItem {
	private static final ResourceLocation TEXTURE_ELYTRA = new ResourceLocation("lanthanoid", "textures/models/eldritch_elytra.png");
	public ItemEldritchElytra(ArmorMaterial mat) {
		super(mat);
		setCreativeTab(Lanthanoid.inst.creativeTabEquipment);
		setUnlocalizedName("eldritch_elytra");
		setTextureName("lanthanoid:eldritch_elytra");
		setMaxDurability(432);
	}
	
	private IIcon glyphs;
	
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
		return "lanthanoid:textures/models/eldritch_elytra_layer.png";
	}
	
	@Override
	public ResourceLocation getElytraTexture(ItemStack itemstack) {
		return TEXTURE_ELYTRA;
	}
	
	@Override
	public ResourceLocation getElytraSound(ItemStack itemstack) {
		return null;
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
		glyphs = register.registerIcon("lanthanoid:eldritch_glyph_fly"); 
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

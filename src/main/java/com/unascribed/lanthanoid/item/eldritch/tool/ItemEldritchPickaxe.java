package com.unascribed.lanthanoid.item.eldritch.tool;

import java.util.List;

import com.google.common.base.Strings;
import com.unascribed.lanthanoid.Lanthanoid;
import com.unascribed.lanthanoid.glyph.IGlyphHolderItem;
import com.unascribed.lanthanoid.init.LBlocks;
import com.unascribed.lanthanoid.item.GlyphItemHelper;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemPickaxe;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;

public class ItemEldritchPickaxe extends ItemPickaxe implements IGlyphHolderItem {

	private IIcon glyphs;
	
	public ItemEldritchPickaxe(ToolMaterial mat) {
		super(mat);
		setCreativeTab(Lanthanoid.inst.creativeTabEquipment);
		setTextureName("lanthanoid:eldritch_pickaxe");
		setUnlocalizedName("eldritch_pickaxe");
	}
	
	@Override
	public int getMaxMilliglyphs(ItemStack stack) {
		return 500_000;
	}
	
	@Override
	public float getStrVsBlock(ItemStack stack, Block block) {
		return getMilliglyphs(stack) > 0 ? super.getStrVsBlock(stack, block)*(block.isToolEffective("pickaxe", 0) ? 1+getBoost(stack) : 0.5f) : 0.5f;
	}
	
	private float getBoost(ItemStack stack) {
		return stack.hasTagCompound() ? stack.getTagCompound().getFloat("SpeedBoost") : 0;
	}
	
	private void setBoost(ItemStack stack, float boost) {
		if (boost > 1.1f) boost = 1.1f;
		if (!stack.hasTagCompound()) {
			stack.setTagCompound(new NBTTagCompound());
		}
		stack.getTagCompound().setFloat("SpeedBoost", boost);
	}

	@Override
	public void addInformation(ItemStack stack, EntityPlayer player, List list, boolean advanced) {
		GlyphItemHelper.doAddInformation(this, stack, player, list, advanced);
	}
	
	@Override
	public float getDigSpeed(ItemStack stack, Block block, int meta) {
		return getStrVsBlock(stack, block);
	}
	
	@Override
	public void onUpdate(ItemStack stack, World world, Entity entity, int slot, boolean equipped) {
		GlyphItemHelper.doUpdate(this, stack, world, entity, slot, equipped);
		if (entity.ticksExisted % 8 == 0 && (entity instanceof EntityPlayer ? !((EntityPlayer)entity).isSwingInProgress : true)) {
			float boost = getBoost(stack);
			if (boost > 0) {
				setBoost(stack, equipped ? Math.max(0, boost*0.9f) : 0);
			}
		}
	}
	
	@Override
	public boolean onBlockDestroyed(ItemStack stack, World world, Block block, int x, int y, int z, EntityLivingBase ent) {
		if (GlyphItemHelper.doBlockDestroyed(this, stack, world, block, x, y, z, ent)) {
			setBoost(stack, getBoost(stack)+0.1f);
		}
		return true;
	}
	
	@Override
	public boolean onItemUse(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side, float hitX, float hitY, float hitZ) {
		EnumFacing face = EnumFacing.values()[side];
		int tX = x+face.getFrontOffsetX();
		int tY = y+face.getFrontOffsetY();
		int tZ = z+face.getFrontOffsetZ();
		Block against = world.getBlock(x, y, z);
		Block cur = world.getBlock(tX, tY, tZ);
		if (against.isAir(world, tX, tY, tZ) || against.isReplaceable(world, tX, tY, tZ)) {
			cur = against;
			tX = x;
			tY = y;
			tZ = z;
		}
		if (cur.isAir(world, tX, tY, tZ) || cur.isReplaceable(world, tX, tY, tZ)) {
			if (getMilliglyphs(stack) > 1000) {
				setMilliglyphs(stack, getMilliglyphs(stack)-1000);
				Block torch = LBlocks.technical;
				int meta = 1;
				world.setBlock(tX, tY, tZ, torch, meta, 3);
				player.playSound("mob.zombie.infect", 1, 1.75f+(world.rand.nextFloat()/4));
				return true;
			}
		}
		return super.onItemUse(stack, player, world, x, y, z, side, hitX, hitY, hitZ);
	}
	
	@Override
	public String getUnlocalizedNameInefficiently(ItemStack p_77657_1_) {
		return Strings.nullToEmpty(getUnlocalizedName(p_77657_1_));
	}
	
	@Override
	public void registerIcons(IIconRegister register) {
		super.registerIcons(register);
		glyphs = register.registerIcon("lanthanoid:eldritch_glyph_mine");
	}
	
	@Override
	public IIcon getGlyphs(ItemStack is) {
		return glyphs;
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

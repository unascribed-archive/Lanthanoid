package com.unascribed.lanthanoid.item.eldritch.tool;

import java.util.List;

import com.google.common.base.Strings;
import com.unascribed.lanthanoid.Lanthanoid;
import com.unascribed.lanthanoid.glyph.IGlyphHolderItem;
import com.unascribed.lanthanoid.init.LBlocks;
import com.unascribed.lanthanoid.item.GlyphItemHelper;
import com.unascribed.lanthanoid.util.LUtil;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemPickaxe;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;

public class ItemEldritchDrill extends ItemPickaxe implements IGlyphHolderItem {

	private IIcon glyphs;
	
	public ItemEldritchDrill(ToolMaterial mat) {
		super(mat);
		setCreativeTab(Lanthanoid.inst.creativeTabEquipment);
		setTextureName("lanthanoid:eldritch_drill");
		setUnlocalizedName("eldritch_drill");
	}
	
	@Override
	public int getMaxMilliglyphs(ItemStack stack) {
		return 500_000;
	}
	
	@Override
	public float getStrVsBlock(ItemStack stack, Block block) {
		return getMilliglyphs(stack) > 0 ? super.getStrVsBlock(stack, block)*0.75f : 0.5f;
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
	}
	
	@Override
	public boolean onBlockStartBreak(ItemStack stack, int x, int y, int z, EntityPlayer player) {
		return super.onBlockStartBreak(stack, x, y, z, player);
	}
	
	private boolean breaking = false;
	
	@Override
	public boolean onBlockDestroyed(ItemStack stack, World world, Block block, int x, int y, int z, EntityLivingBase ent) {
		if (GlyphItemHelper.doBlockDestroyed(this, stack, world, block, x, y, z, ent) && !breaking && ent instanceof EntityPlayerMP && !ent.isSneaking()) {
			try {
				breaking = true;
				LUtil.harvest((EntityPlayerMP)ent, world, x, y-1, z, stack, true, true, false);
				LUtil.harvest((EntityPlayerMP)ent, world, x, y+1, z, stack, true, true, false);
			} finally {
				breaking = false;
			}
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

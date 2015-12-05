package com.unascribed.lanthanoid.item;

import java.util.List;

import com.google.common.base.Strings;
import com.unascribed.lanthanoid.Lanthanoid;
import com.unascribed.lanthanoid.util.LUtil;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemPickaxe;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;

public class ItemEldritchDrill extends ItemPickaxe implements IGlyphHolderTool {

	private IIcon glyphs;
	
	public ItemEldritchDrill(ToolMaterial mat) {
		super(mat);
		setCreativeTab(Lanthanoid.inst.creativeTabEquipment);
		setTextureName("lanthanoid:eldritch_drill");
		setUnlocalizedName("eldritch_drill");
	}
	
	@Override
	public int getMaxMilliglyphs(ItemStack stack) {
		return 400_000;
	}
	
	@Override
	public float getStrVsBlock(ItemStack stack, Block block) {
		return getMilliglyphs(stack) > 0 ? super.getStrVsBlock(stack, block)*0.75f : 0.5f;
	}


	@Override
	public void addInformation(ItemStack stack, EntityPlayer player, List list, boolean advanced) {
		doAddInformation(stack, player, list, advanced);
	}
	
	@Override
	public float getDigSpeed(ItemStack stack, Block block, int meta) {
		return getStrVsBlock(stack, block);
	}
	
	@Override
	public void onUpdate(ItemStack stack, World world, Entity entity, int slot, boolean equipped) {
		doUpdate(stack, world, entity, slot, equipped);
	}
	
	@Override
	public boolean onBlockStartBreak(ItemStack stack, int x, int y, int z, EntityPlayer player) {
		return super.onBlockStartBreak(stack, x, y, z, player);
	}
	
	private boolean breaking = false;
	
	@Override
	public boolean onBlockDestroyed(ItemStack stack, World world, Block block, int x, int y, int z, EntityLivingBase ent) {
		if (doBlockDestroyed(stack, world, block, x, y, z, ent) && !breaking && ent instanceof EntityPlayerMP && !ent.isSneaking()) {
			try {
				breaking = true;
				LUtil.harvest((EntityPlayerMP)ent, world, x, y-1, z, true, true, false);
				LUtil.harvest((EntityPlayerMP)ent, world, x, y+1, z, true, true, false);
			} finally {
				breaking = false;
			}
		}
		return true;
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
	
	public IIcon getGlyphs() {
		return glyphs;
	}
	
}

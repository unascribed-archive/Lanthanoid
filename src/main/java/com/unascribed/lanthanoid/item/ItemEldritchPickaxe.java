package com.unascribed.lanthanoid.item;

import java.util.List;

import com.google.common.base.Strings;
import com.unascribed.lanthanoid.Lanthanoid;
import com.unascribed.lanthanoid.glyph.IGlyphHolderItem;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemPickaxe;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
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
		return 400_000;
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
		GlyphToolHelper.doAddInformation(this, stack, player, list, advanced);
	}
	
	@Override
	public float getDigSpeed(ItemStack stack, Block block, int meta) {
		return getStrVsBlock(stack, block);
	}
	
	@Override
	public void onUpdate(ItemStack stack, World world, Entity entity, int slot, boolean equipped) {
		GlyphToolHelper.doUpdate(this, stack, world, entity, slot, equipped);
		if (entity.ticksExisted % 8 == 0 && (entity instanceof EntityPlayer ? !((EntityPlayer)entity).isSwingInProgress : true)) {
			float boost = getBoost(stack);
			if (boost > 0) {
				setBoost(stack, equipped ? Math.max(0, boost*0.9f) : 0);
			}
		}
	}
	
	@Override
	public boolean onBlockDestroyed(ItemStack stack, World world, Block block, int x, int y, int z, EntityLivingBase ent) {
		if (GlyphToolHelper.doBlockDestroyed(this, stack, world, block, x, y, z, ent)) {
			setBoost(stack, getBoost(stack)+0.1f);
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

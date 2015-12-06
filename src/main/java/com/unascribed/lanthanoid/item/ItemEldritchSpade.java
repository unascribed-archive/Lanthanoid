package com.unascribed.lanthanoid.item;

import java.util.List;

import com.google.common.base.Strings;
import com.unascribed.lanthanoid.Lanthanoid;
import com.unascribed.lanthanoid.glyph.IGlyphHolderItem;
import com.unascribed.lanthanoid.util.LUtil;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemSpade;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;

public class ItemEldritchSpade extends ItemSpade implements IGlyphHolderItem {

	private IIcon glyphs;
	
	public ItemEldritchSpade(ToolMaterial mat) {
		super(mat);
		setCreativeTab(Lanthanoid.inst.creativeTabEquipment);
		setTextureName("lanthanoid:eldritch_shovel");
		setUnlocalizedName("eldritch_shovel");
	}
	
	@Override
	public int getMaxMilliglyphs(ItemStack stack) {
		return 500_000;
	}
	
	@Override
	public float getStrVsBlock(ItemStack stack, Block block) {
		return getMilliglyphs(stack) > 0 ? super.getStrVsBlock(stack, block) : super.getStrVsBlock(stack, block)/3;
	}
	
	@Override
	public float getDigSpeed(ItemStack stack, Block block, int meta) {
		return getStrVsBlock(stack, block);
	}
	
	@Override
	public void addInformation(ItemStack stack, EntityPlayer player, List list, boolean advanced) {
		GlyphToolHelper.doAddInformation(this, stack, player, list, advanced);
	}
	
	@Override
	public void onUpdate(ItemStack stack, World world, Entity entity, int slot, boolean equipped) {
		GlyphToolHelper.doUpdate(this, stack, world, entity, slot, equipped);
	}
	
	private boolean breaking = false;
	
	@Override
	public boolean onBlockDestroyed(ItemStack stack, World world, Block block, int x, int y, int z, EntityLivingBase ent) {
		if (GlyphToolHelper.doBlockDestroyed(this, stack, world, block, x, y, z, ent) && !breaking && ent instanceof EntityPlayerMP && !ent.isSneaking()) {
			try {
				breaking = true;
				int iterations = 0;
				int curY = y+1;
				while (world.getBlock(x, curY, z) != null &&
						(world.getBlock(x, curY, z).getMaterial() == Material.sand
						|| world.getBlock(x, curY, z).getMaterial() == Material.grass
						|| world.getBlock(x, curY, z).getMaterial() == Material.ground)) {
					if (iterations > 7) break;
					if (GlyphToolHelper.doBlockDestroyed(this, stack, world, block, x, curY, z, ent)) {
						LUtil.harvest((EntityPlayerMP)ent, world, x, curY, z, world.getBlock(x, curY, z).isToolEffective("shovel", world.getBlockMetadata(x, curY, z)), true, false);
					} else {
						break;
					}
					curY++;
					iterations++;
				}
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
		glyphs = register.registerIcon("lanthanoid:eldritch_glyph_dig");
	}
	
	public IIcon getGlyphs() {
		return glyphs;
	}
	
}

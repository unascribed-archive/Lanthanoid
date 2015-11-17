package com.unascribed.lanthanoid.item;

import com.unascribed.lanthanoid.init.LBlocks;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.passive.EntityChicken;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class ItemSpanner extends ItemBase {
	public ItemSpanner() {
		setTextureName("lanthanoid:spanner");
		setUnlocalizedName("rose_colored_spanner");
		setMaxDamage(256);
		setMaxStackSize(1);
	}
	@Override
	public boolean itemInteractionForEntity(ItemStack stack, EntityPlayer player, EntityLivingBase entity) {
		if (entity instanceof EntityChicken) {
			if (player.ridingEntity != null) {
				player.ridingEntity.riddenByEntity = null;
			}
			entity.riddenByEntity = player;
			player.ridingEntity = entity;
			return true;
		}
		return false;
	}
	@Override
	public boolean doesSneakBypassUse(World world, int x, int y, int z, EntityPlayer player) {
		return world.getBlock(x, y, z) == LBlocks.machine;
	}
}

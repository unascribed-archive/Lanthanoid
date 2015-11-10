package com.unascribed.lanthanoid.item;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;

public class ItemBlockMachine extends ItemBlockWithCustomName {
	public ItemBlockMachine(Block block) {
		super(block);
	}

	@Override
	public void addInformation(ItemStack stack, EntityPlayer player, List li, boolean advanced) {
		if (!stack.hasDisplayName()) {
			li.add(StatCollector.translateToLocal("ui.waypoint_hint"));
		}
		if (advanced) {
			int color = -1;
			if (stack.hasTagCompound() && stack.getTagCompound().hasKey("Color", 99)) {
				color = stack.getTagCompound().getInteger("Color");
			}
			li.add("Color: #"+Integer.toHexString(color&0x00FFFFFF).toUpperCase());
		}
	}
	
	@Override
	public boolean placeBlockAt(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side, float hitX, float hitY, float hitZ, int metadata) {
		if (!stack.hasDisplayName()) {
			if (!world.isRemote) {
				player.addChatMessage(new ChatComponentTranslation("chat.waypoint_hint"));
			}
			return false;
		}
		return super.placeBlockAt(stack, player, world, x, y, z, side, hitX, hitY, hitZ, metadata);
	}
}

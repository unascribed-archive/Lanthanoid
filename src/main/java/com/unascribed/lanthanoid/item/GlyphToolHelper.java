package com.unascribed.lanthanoid.item;

import java.util.List;

import com.unascribed.lanthanoid.glyph.IGlyphHolderItem;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;

public class GlyphToolHelper {
	public static boolean doBlockDestroyed(IGlyphHolderItem holder, ItemStack stack, World world, Block block, int x, int y, int z, EntityLivingBase ent) {
		float hardness = block.getBlockHardness(world, x, y, z);
		int cost = Math.min(holder.getMilliglyphs(stack), (int)(hardness*100f));
		holder.setMilliglyphs(stack, holder.getMilliglyphs(stack)-cost);
		int repair = Math.min(holder.getMilliglyphs(stack)/1000, 1);
		if (repair >= 1) {
			holder.setMilliglyphs(stack, holder.getMilliglyphs(stack)-(repair*1000));
		} else {
			stack.damageItem(1, ent);
		}
		if (world instanceof WorldServer) {
			((WorldServer)world).func_147487_a("enchantmenttable", x+0.5, y+0.5, z+0.5, cost/1000, 0.25, 0.25, 0.25, 0);
		}
		return cost > 0;
	}
	
	public static void doUpdate(IGlyphHolderItem holder, ItemStack stack, World world, Entity entity, int slot, boolean equipped) {
		if (entity.ticksExisted % 10 == 0) {
			int repair = Math.min(holder.getMilliglyphs(stack)/1000, stack.getMetadata());
			if (repair > 0) {
				holder.setMilliglyphs(stack, holder.getMilliglyphs(stack)-(repair*1000));
				stack.setMetadata(stack.getMetadata()-repair);
			}
		}
	}
	
	@SideOnly(Side.CLIENT)
	public static void doAddInformation(IGlyphHolderItem holder, ItemStack stack, EntityPlayer player, List list, boolean advanced) {
		list.add("\u00A79"+StatCollector.translateToLocalFormatted("ui.eldritch_ability", StatCollector.translateToLocal(stack.getUnlocalizedName()+".ability.name")));
		list.addAll(Minecraft.getMinecraft().fontRendererObj.listFormattedStringToWidth(StatCollector.translateToLocal(stack.getUnlocalizedName()+".ability.desc"), 240));
	}
}

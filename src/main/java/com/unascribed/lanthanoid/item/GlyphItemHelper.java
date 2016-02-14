package com.unascribed.lanthanoid.item;

import java.util.List;

import com.unascribed.lanthanoid.glyph.IGlyphHolderItem;
import com.unascribed.lanthanoid.init.LItems;

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

public class GlyphItemHelper {
	public static boolean doBlockDestroyed(IGlyphHolderItem holder, ItemStack stack, World world, Block block, int x, int y, int z, EntityLivingBase ent) {
		float hardness = block.getBlockHardness(world, x, y, z);
		int cost = Math.min(holder.getMilliglyphs(stack), (int)(hardness*12.5f));
		holder.setMilliglyphs(stack, holder.getMilliglyphs(stack)-cost);
		if (world instanceof WorldServer) {
			((WorldServer)world).func_147487_a("enchantmenttable", x+0.5, y+0.5, z+0.5, cost/1000, 0.25, 0.25, 0.25, 0);
		}
		return cost > 0;
	}
	
	public static void doUpdate(IGlyphHolderItem holder, ItemStack stack, World world, Entity entity, int slot, boolean equipped) {
		if (entity.ticksExisted % 10 == 0) {
			int repair = Math.min(holder.getMilliglyphs(stack)/250, stack.getMetadata());
			if (repair > 0) {
				holder.setMilliglyphs(stack, holder.getMilliglyphs(stack)-(repair*500));
				stack.setMetadata(stack.getMetadata()-repair);
			}
		}
	}
	
	@SideOnly(Side.CLIENT)
	public static void doAddInformation(IGlyphHolderItem holder, ItemStack stack, EntityPlayer player, List list, boolean advanced) {
		list.add("\u00A79"+StatCollector.translateToLocalFormatted("ui.eldritch_ability", StatCollector.translateToLocal(stack.getUnlocalizedName()+".ability.name")));
		list.addAll(Minecraft.getMinecraft().fontRendererObj.listFormattedStringToWidth(StatCollector.translateToLocal(stack.getUnlocalizedName()+".ability.desc"), 240));
		if (player.inventory.armorItemInSlot(3) != null && player.inventory.armorItemInSlot(3).getItem() == LItems.eldritch_helmet_enhanced) {
			list.add("");
			list.add(StatCollector.translateToLocalFormatted("ui.glyph_level", ((int)(((float)holder.getMilliglyphs(stack)/holder.getMaxMilliglyphs(stack))*100))+"%"));
		}
	}

	public static float getDefaultGlyphColorRed(IGlyphHolderItem holder, ItemStack is) {
		return Math.max(0.25f, holder.getMilliglyphs(is)/(float)holder.getMaxMilliglyphs(is));
	}
	
	public static float getDefaultGlyphColorGreen(IGlyphHolderItem holder, ItemStack is) {
		return 0;
	}
	
	public static float getDefaultGlyphColorBlue(IGlyphHolderItem holder, ItemStack is) {
		return 1-(holder.getMilliglyphs(is)/(float)holder.getMaxMilliglyphs(is));
	}
	
	public static float getDefaultGlyphColorAlpha(IGlyphHolderItem holder, ItemStack is) {
		return (holder.getMilliglyphs(is) == holder.getMaxMilliglyphs(is)) ? 1 : 0.5f;
	}
}

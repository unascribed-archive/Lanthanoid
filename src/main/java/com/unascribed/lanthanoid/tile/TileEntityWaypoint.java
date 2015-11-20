package com.unascribed.lanthanoid.tile;

import java.util.Map;

import com.google.common.collect.ImmutableMap;
import com.unascribed.lanthanoid.Lanthanoid;
import com.unascribed.lanthanoid.init.LItems;
import com.unascribed.lanthanoid.waypoint.Waypoint;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.relauncher.Side;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.oredict.OreDictionary;

public class TileEntityWaypoint extends TileEntity implements IBreakable, IActivatable {
	@Override
	public void breakBlock(World world, int x, int y, int z) {
		if (!world.isRemote && FMLCommonHandler.instance().getEffectiveSide() == Side.SERVER) {
			Lanthanoid.inst.waypointManager.removeWaypointLater(world, x, y, z);
		}
	}
	
	private static final Map<String, float[]> dyes = ImmutableMap.<String, float[]>builder()
			.put("dyeBlack", new float[] { 0f, 0f, 0f })
			.put("dyeRed", new float[] { 1f, 0f, 0f })
			.put("dyeGreen", new float[] { 0f, 1f, 0f })
			.put("dyeBrown", new float[] { 0.75f, 0.5f, 0f })
			.put("dyeBlue", new float[] { 0f, 0f, 1f })
			.put("dyePurple", new float[] { 1f, 0f, 0.5f })
			.put("dyeCyan", new float[] { 0f, 1f, 1f })
			.put("dyeLightGray", new float[] { 0.6f, 0.6f, 0.6f })
			.put("dyeGray", new float[] { 0.35f, 0.35f, 0.35f })
			.put("dyePink", new float[] { 1f, 0.5f, 0.65f })
			.put("dyeLime", new float[] { 0.75f, 1f, 0f })
			.put("dyeYellow", new float[] { 1f, 1f, 0f })
			.put("dyeLightBlue", new float[] { 0.5f, 0.5f, 1f })
			.put("dyeMagenta", new float[] { 1f, 0f, 1f })
			.put("dyeOrange", new float[] { 1f, 0.5f, 0f })
			.put("dyeWhite", new float[] { 1f, 1f, 1f }).build();
	
	@Override
	public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side, float subX, float subY, float subZ) {
		Waypoint w = Lanthanoid.inst.waypointManager.getWaypoint(world, x, y, z);
		if (w != null) {
			ItemStack held = player.getHeldItem();
			if (held != null) {
				if (held.getItem() == LItems.spanner) {
					if (player.isSneaking()) {
						w.nameDistance = Math.max(w.nameDistance - 5, 5);
					} else {
						w.nameDistance = Math.min(w.nameDistance + 5, 260);
					}
					Lanthanoid.inst.waypointManager.setWaypoint(world, x, y, z, w);
					return true;
				} else {
					float times = 3;
					float[] resultColor = new float[] { ((w.color >> 16) & 255) / 255f, ((w.color >> 8) & 255) / 255f, (w.color & 255) / 255f, };
					resultColor[0] *= times;
					resultColor[1] *= times;
					resultColor[2] *= times;
					int[] ids = OreDictionary.getOreIDs(held);
					boolean use = false;
					for (int id : ids) {
						String name = OreDictionary.getOreName(id);
						if (dyes.containsKey(name)) {
							float[] color = dyes.get(name);
							resultColor[0] += color[0];
							resultColor[1] += color[1];
							resultColor[2] += color[2];
							times++;
							use = true;
						}
					}
					if (use) {
						resultColor[0] /= times;
						resultColor[1] /= times;
						resultColor[2] /= times;
						int packed = 0;
						packed |= (((int) ((resultColor[0] * 255)) & 255) << 16);
						packed |= (((int) ((resultColor[1] * 255)) & 255) << 8);
						packed |= ((int) ((resultColor[2] * 255)) & 255);
						if (packed == w.color) {
							return false;
						}
						if (!player.capabilities.isCreativeMode) {
							held.stackSize--;
						}
						w.color = packed;
						Lanthanoid.inst.waypointManager.setWaypoint(world, x, y, z, w);
						return true;
					}
				}
			}
		}
		return false;
	}
}

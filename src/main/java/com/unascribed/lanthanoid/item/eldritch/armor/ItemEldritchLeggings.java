package com.unascribed.lanthanoid.item.eldritch.armor;

import java.util.Collection;
import java.util.List;

import com.google.common.collect.Lists;
import com.unascribed.lanthanoid.Lanthanoid;
import com.unascribed.lanthanoid.glyph.IGlyphHolderItem;
import com.unascribed.lanthanoid.network.SpawnGlyphParticles;
import com.unascribed.lanthanoid.tile.TileEntityEldritchDistributor;

import cpw.mods.fml.common.network.NetworkRegistry.TargetPoint;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.util.MovingObjectPosition.MovingObjectType;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;

public class ItemEldritchLeggings extends ItemEldritchArmor {

	public ItemEldritchLeggings(ArmorMaterial material) {
		super(material, 2, false);
	}
	
	@Override
	public void onArmorTick(World world, EntityPlayer player, ItemStack stack) {
		super.onArmorTick(world, player, stack);
		
		if (player.ticksExisted % 40 == 0) {
			int xCoord = (int)player.posX;
			int yCoord = (int)player.posY;
			int zCoord = (int)player.posZ;
			
			int minX = xCoord-12;
			int minZ = zCoord-12;
			int maxX = xCoord+12;
			int maxZ = zCoord+12;
			int minY = yCoord-12;
			int maxY = yCoord+12;
			
			int minCX = (xCoord/16)-1;
			int minCZ = (zCoord/16)-1;
			int maxCX = (xCoord/16)+1;
			int maxCZ = (zCoord/16)+1;
			
			
			int max = 0;
			TileEntityEldritchDistributor maxDist = null;
			
			for (int cX = minCX; cX <= maxCX; cX++) {
				for (int cZ = minCZ; cZ <= maxCZ; cZ++) {
					Chunk c = world.getChunkFromChunkCoords(cX, cZ);
					for (TileEntity te : (Collection<TileEntity>)c.chunkTileEntityMap.values()) {
						if (te.xCoord >= minX && te.xCoord <= maxX
								&& te.zCoord >= minZ && te.zCoord <= maxZ
								&& te.yCoord >= minY && te.yCoord <= maxY) {
							if (te instanceof TileEntityEldritchDistributor) {
								TileEntityEldritchDistributor dist = (TileEntityEldritchDistributor)te;
								MovingObjectPosition mop = world.rayTraceBlocks(
										Vec3.createVectorHelper(player.posX, player.posY, player.posZ),
										Vec3.createVectorHelper(te.xCoord+0.5, te.yCoord+0.5, te.zCoord+0.5));
								if (mop != null && mop.typeOfHit == MovingObjectType.BLOCK && world.getTileEntity(mop.blockX, mop.blockY, mop.blockZ) == te) {
									if (maxDist == null || max < dist.getMilliglyphs()) {
										max = dist.getMilliglyphs();
										maxDist = dist;
									}
								}
							}
						}
					}
				}
			}
			
			if (maxDist != null || getMilliglyphs(stack) > getMaxMilliglyphs(stack)/2) {
				List<ItemStack> itemsNeedingGlyphs = Lists.newArrayList();
				int freeSpace = 0;
				if (getMilliglyphs(stack) < (getMaxMilliglyphs(stack)/2)) {
					freeSpace = getMaxMilliglyphs(stack) - getMilliglyphs(stack);
					itemsNeedingGlyphs.add(stack);
				} else {
					for (ItemStack is : player.inventory.mainInventory) {
						if (is != null && is.getItem() instanceof IGlyphHolderItem) {
							IGlyphHolderItem holder = (IGlyphHolderItem)is.getItem();
							int curG = holder.getMilliglyphs(is);
							int maxG = holder.getMaxMilliglyphs(is);
							if (curG < maxG) {
								itemsNeedingGlyphs.add(is);
								freeSpace += maxG-curG;
							}
						}
					}
					for (ItemStack is : player.inventory.armorInventory) {
						if (is != null && is.getItem() instanceof IGlyphHolderItem) {
							IGlyphHolderItem holder = (IGlyphHolderItem)is.getItem();
							int curG = holder.getMilliglyphs(is);
							int maxG = holder.getMaxMilliglyphs(is);
							if (curG < maxG) {
								itemsNeedingGlyphs.add(is);
								freeSpace += maxG-curG;
							}
						}
					}
				}
				if (!itemsNeedingGlyphs.isEmpty()) {
					int toTransfer = Math.min(freeSpace/4, maxDist != null ? maxDist.getMilliglyphs() : getMilliglyphs(stack));
					if (!world.isRemote && maxDist != null) {
						double x = maxDist.xCoord+0.5;
						double y = maxDist.yCoord+0.5;
						double z = maxDist.zCoord+0.5;
						SpawnGlyphParticles.Message msg = new SpawnGlyphParticles.Message(x, y, z, player.posX, player.posY+(player.height), player.posZ, 0.25, 0.25, 0.25, toTransfer/1000);
						Lanthanoid.inst.network.sendToAllAround(msg, new TargetPoint(world.provider.dimensionId, player.posX, player.posY, player.posZ, 64));
					}
					int buffer = (int)Math.ceil(toTransfer * 0.9f);
					if (maxDist != null) {
						maxDist.setMilliglyphs(maxDist.getMilliglyphs()-toTransfer);
					} else {
						setMilliglyphs(stack, getMilliglyphs(stack)-toTransfer);
					}
					for (ItemStack is : itemsNeedingGlyphs) {
						IGlyphHolderItem holder = (IGlyphHolderItem)is.getItem();
						
						int curG = holder.getMilliglyphs(is);
						int maxG = holder.getMaxMilliglyphs(is);
						
						int toGive = Math.min(buffer, Math.min(maxG-curG, toTransfer/itemsNeedingGlyphs.size()));
						holder.setMilliglyphs(is, curG+toGive);
						buffer -= toGive;
					}
					if (buffer > 0) {
						if (maxDist != null) {
							maxDist.setMilliglyphs(maxDist.getMilliglyphs()+buffer);
						} else {
							setMilliglyphs(stack, getMilliglyphs(stack)+buffer);
						}
					} else if (buffer < 0) {
						Lanthanoid.log.warn("Accidentally summoned {} glyphs out of thin air!", buffer*-1);
					}
				}
			}
		}
	}
	
}

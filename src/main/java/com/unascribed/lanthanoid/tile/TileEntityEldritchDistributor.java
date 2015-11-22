package com.unascribed.lanthanoid.tile;

import java.util.Collection;

import com.unascribed.lanthanoid.glyph.IGlyphHolder;
import com.unascribed.lanthanoid.init.LItems;
import com.unascribed.lanthanoid.util.LVec3;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.util.MovingObjectPosition.MovingObjectType;
import net.minecraft.world.chunk.Chunk;

public class TileEntityEldritchDistributor extends TileEntityEldritchWithBooks {

	public boolean drain;
	
	@Override
	protected void doTickLogic() {
		if (!worldObj.isRemote && ticksExisted % 20 == 0) {
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
			
			IGlyphHolder max = null;
			int maxDiff = 0;
			IGlyphHolder min = null;
			int minDiff = 0;
			
			for (int cX = minCX; cX <= maxCX; cX++) {
				for (int cZ = minCZ; cZ <= maxCZ; cZ++) {
					Chunk c = worldObj.getChunkFromChunkCoords(cX, cZ);
					for (TileEntity te : (Collection<TileEntity>)c.chunkTileEntityMap.values()) {
						if (te.xCoord >= minX && te.xCoord <= maxX
								&& te.zCoord >= minZ && te.zCoord <= maxZ
								&& te.yCoord >= minY && te.yCoord <= maxY) {
							if (te instanceof IGlyphHolder && te != this) {
								IGlyphHolder holder = (IGlyphHolder) te;
								LVec3 dir = new LVec3(te.xCoord - this.xCoord, te.yCoord - this.yCoord, te.zCoord - this.zCoord);
								dir.normalize();
								double sX = xCoord+0.5+dir.xCoord;
								double sY = yCoord+0.5+dir.yCoord;
								double sZ = zCoord+0.5+dir.zCoord;
								double eX = te.xCoord+0.5;
								double eY = te.yCoord+0.5;
								double eZ = te.zCoord+0.5;
								MovingObjectPosition mop = worldObj.rayTraceBlocks(
										Vec3.createVectorHelper(sX, sY, sZ),
										Vec3.createVectorHelper(eX, eY, eZ));
								if (mop != null) {
									if (mop.typeOfHit == MovingObjectType.BLOCK && worldObj.getTileEntity(mop.blockX, mop.blockY, mop.blockZ) == holder) {
										if (drain) {
											if (holder.canReceiveGlyphs()) {
												if (min == null || holder.getMilliglyphs() < min.getMilliglyphs()) {
													min = holder;
													minDiff = Integer.MAX_VALUE;
												}
											}
										} else {
											if (holder.getMilliglyphs() > this.getMilliglyphs() && holder.canSendGlyphs()) {
												if (max == null || holder.getMilliglyphs() > max.getMilliglyphs()) {
													max = holder;
													maxDiff = holder.getMilliglyphs()-this.getMilliglyphs();
												}
											} else if (holder.getMilliglyphs() < this.getMilliglyphs() && holder.canReceiveGlyphs()) {
												if (min == null || holder.getMilliglyphs() < min.getMilliglyphs()) {
													min = holder;
													minDiff = this.getMilliglyphs()-holder.getMilliglyphs();
												}
											}
										}
									}
								}
							}
						}
					}
				}
			}
			
			if (min == null || maxDiff > minDiff || !min.transferFrom(this, drain)) {
				if (max != null) {
					this.transferFrom(max, false);
				}
			}
		}
	}

	@Override
	public boolean onBlockActivated(EntityPlayer player, int side, float subX, float subY, float subZ) {
		if (player.getHeldItem() != null && player.getHeldItem().getItem() == LItems.spanner) {
			if (!player.worldObj.isRemote) {
				drain = !drain;
				player.worldObj.addBlockEvent(xCoord, yCoord, zCoord, getBlockType(), 4, drain ? 1 : 0);
			}
			return true;
		}
		return super.onBlockActivated(player, side, subX, subY, subZ);
	}
	
	@Override
	public boolean receiveClientEvent(int event, int arg) {
		if (event == 4) {
			drain = (arg != 0);
			return true;
		}
		return super.receiveClientEvent(event, arg);
	}
	
	@Override
	public boolean canReceiveGlyphs() {
		return !drain && getMilliglyphs() < getMaxMilliglyphs();
	}

	@Override
	public boolean canSendGlyphs() {
		return true;
	}

}

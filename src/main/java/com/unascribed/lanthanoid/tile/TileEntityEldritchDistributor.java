package com.unascribed.lanthanoid.tile;

import java.util.Collection;

import com.unascribed.lanthanoid.util.LVec3;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.util.MovingObjectPosition.MovingObjectType;
import net.minecraft.world.chunk.Chunk;

public class TileEntityEldritchDistributor extends TileEntityEldritch {

	@Override
	protected void doTickLogic() {
		if (!worldObj.isRemote && ticksExisted % 20 == 0) {
			int minX = xCoord-12;
			int minZ = zCoord-12;
			int maxX = xCoord+12;
			int maxZ = zCoord+12;
			int minY = yCoord-12;
			int maxY = yCoord+12;
			
			int minCX = minX/16;
			int minCZ = minZ/16;
			int maxCX = maxX/16;
			int maxCZ = maxZ/16;
			
			TileEntityEldritch max = null;
			int maxDiff = 0;
			TileEntityEldritch min = null;
			int minDiff = 0;
			
			boolean drain = worldObj.getBlockPowerInput(xCoord, yCoord, zCoord) > 0;
			
			for (int cX = minCX; cX <= maxCX; cX++) {
				for (int cZ = minCZ; cZ <= maxCZ; cZ++) {
					Chunk c = worldObj.getChunkFromChunkCoords(cX, cZ);
					for (TileEntity te : (Collection<TileEntity>)c.chunkTileEntityMap.values()) {
						if (te.xCoord >= minX && te.xCoord <= maxX
								&& te.zCoord >= minZ && te.zCoord <= maxZ
								&& te.yCoord >= minY && te.yCoord <= maxY) {
							if (te instanceof TileEntityEldritch && te != this) {
								TileEntityEldritch tee = (TileEntityEldritch) te;
								LVec3 dir = new LVec3(tee.xCoord - this.xCoord, tee.yCoord - this.yCoord, tee.zCoord - this.zCoord);
								dir.normalize();
								double sX = xCoord+0.5+dir.xCoord;
								double sY = yCoord+0.5+dir.yCoord;
								double sZ = zCoord+0.5+dir.zCoord; 
								MovingObjectPosition mop = worldObj.rayTraceBlocks(
										Vec3.createVectorHelper(sX, sY, sZ),
										Vec3.createVectorHelper(tee.xCoord+0.5, tee.yCoord+0.5, tee.zCoord+0.5));
								if (mop == null || (mop.typeOfHit == MovingObjectType.BLOCK && worldObj.getTileEntity(mop.blockX, mop.blockY, mop.blockZ) == tee)) {
									if (drain) {
										if (tee.canReceiveGlyphs() && tee.getMilliglyphs() < tee.getMaxMilliglyphs()) {
											if (min == null || tee.getMilliglyphs() < min.getMilliglyphs()) {
												min = tee;
												minDiff = Integer.MAX_VALUE;
											}
										}
									} else {
										if (tee.getMilliglyphs() > this.getMilliglyphs() && tee.canSendGlyphs()) {
											if (max == null || tee.getMilliglyphs() > max.getMilliglyphs()) {
												max = tee;
												maxDiff = tee.getMilliglyphs()-this.getMilliglyphs();
											}
										} else if (tee.getMilliglyphs() < this.getMilliglyphs() && tee.canReceiveGlyphs()) {
											if (min == null || tee.getMilliglyphs() < min.getMilliglyphs()) {
												min = tee;
												minDiff = this.getMilliglyphs()-tee.getMilliglyphs();
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
	public boolean canReceiveGlyphs() {
		return getMilliglyphs() < getMaxMilliglyphs();
	}

	@Override
	public boolean canSendGlyphs() {
		return true;
	}

}

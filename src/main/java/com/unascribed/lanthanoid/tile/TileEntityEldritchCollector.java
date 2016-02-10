package com.unascribed.lanthanoid.tile;

import java.util.Collection;
import java.util.List;

import com.unascribed.lanthanoid.util.LVec3;

import net.minecraft.block.Block;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.util.MovingObjectPosition.MovingObjectType;
import net.minecraftforge.common.ForgeChunkManager;
import net.minecraftforge.common.ForgeChunkManager.Ticket;

public class TileEntityEldritchCollector extends TileEntityEldritchWithBooks {
	private static final String[] collectStrings = {
		"\u00A77Warming up",
		"\u00A7aCollecting",
		"\u00A7cNo player",
		"\u00A7cNo blocks",
		"\u00A7cNo bookshelves",
		"\u00A7cNo room"
	};
	private int lastCollectString = 0;
	
	@Override
	protected void doTickLogic() {
		final int collectString;
		if (!getWorld().isRemote && ticksExisted % 40 == 0) {
			if (getMilliglyphs() < getMaxMilliglyphs()) {
				Collection<Ticket> tickets = ForgeChunkManager.getPersistentChunksFor(getWorld()).get(worldObj.getChunkFromBlockCoords(xCoord, zCoord).getChunkCoordIntPair());
				boolean playerLoaded = tickets.isEmpty();
				for (Ticket t : tickets) {
					if (t.isPlayerTicket()) {
						playerLoaded = true;
						break;
					}
				}
				if (playerLoaded) {
					LVec3 end = new LVec3(worldObj.rand.nextGaussian(), worldObj.rand.nextGaussian(), worldObj.rand.nextGaussian());
					end.normalize();
					double sX = xCoord+0.5+end.xCoord;
					double sY = yCoord+0.5+end.yCoord;
					double sZ = zCoord+0.5+end.zCoord;
					end.multiply(3);
					end.addVector(xCoord+0.5, yCoord+0.5, zCoord+0.5);
					MovingObjectPosition mop = worldObj.rayTraceBlocks(Vec3.createVectorHelper(sX, sY, sZ), end.toVec3());
					if (mop != null && mop.typeOfHit == MovingObjectType.BLOCK) {
						Block hitBlock = worldObj.getBlock(mop.blockX, mop.blockY, mop.blockZ);
						float power = hitBlock.getEnchantPowerBonus(worldObj, mop.blockX, mop.blockY, mop.blockZ);
						if (power > 0) {
							setMilliglyphs(getMilliglyphs() + (int)(power*1000));
							spawnParticles(mop.blockX, mop.blockY, mop.blockZ, power);
							collectString = 1; // Collecting
						} else {
							collectString = 4; // No bookshelves
						}
					} else {
						collectString = 3; // No blocks
					}
				} else {
					collectString = 2; // Not player loaded
				}
			} else {
				collectString = 5; // No room
			}
			if (collectString != lastCollectString) {
				lastCollectString = collectString;
				addExtendedBlockEvent(255, collectString);
			}
		}
	}

	@Override
	public boolean receiveClientEvent(int event, int arg) {
		if (event == 255) {
			lastCollectString = arg;
			return true;
		}
		return super.receiveClientEvent(event, arg);
	}
	
	@Override
	protected void addDebugText(List<String> li) {
		li.add("\u00A7b\u00A7l["+Integer.toHexString(getId())+"]");
		super.addDebugText(li);
		li.add(collectStrings[lastCollectString]);
	}
	
	@Override
	public boolean canReceiveGlyphs() {
		return false;
	}

	@Override
	public boolean canSendGlyphs() {
		return true;
	}

}

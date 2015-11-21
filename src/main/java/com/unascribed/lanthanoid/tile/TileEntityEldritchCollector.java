package com.unascribed.lanthanoid.tile;

import com.unascribed.lanthanoid.util.LVec3;

import net.minecraft.block.Block;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.util.MovingObjectPosition.MovingObjectType;

public class TileEntityEldritchCollector extends TileEntityEldritchWithBooks {
	@Override
	protected void doTickLogic() {
		if (!getWorld().isRemote && getMilliglyphs() < getMaxMilliglyphs() && ticksExisted % 40 == 0) {
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
				}
			}
		}
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

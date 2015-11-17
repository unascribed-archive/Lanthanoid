package com.unascribed.lanthanoid.tile;

import com.unascribed.lanthanoid.client.SoundEldritch;
import com.unascribed.lanthanoid.util.LVec3;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Vec3;
import net.minecraft.util.MovingObjectPosition.MovingObjectType;

public class TileEntityEldritch extends TileEntity {
	public int ticksExisted;
	public int playerAnim;
	public boolean playersNearby;
	
	public int milliglyphs;
	
	@SideOnly(Side.CLIENT)
	private SoundEldritch sound;
	
	@Override
	public void updateEntity() {
		ticksExisted++;
		if (hasWorldObj()) {
			float d = 4;
			playersNearby = !worldObj.getEntitiesWithinAABB(EntityPlayer.class, getBlockType().getCollisionBoundingBoxFromPool(worldObj, xCoord, yCoord, zCoord).expand(d, d, d)).isEmpty();
			if (playersNearby) {
				if (playerAnim < 20) {
					playerAnim++;
				}
			} else {
				if (playerAnim > 0) {
					playerAnim--;
				}
			}
			if (FMLCommonHandler.instance().getSide().isClient() && FMLCommonHandler.instance().getEffectiveSide().isClient()) {
				updateSound();
			}
			if (!getWorldObj().isRemote) {
				if (getBlockMetadata() == 3) {
					// Collector
					if (ticksExisted % 10 == 0) {
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
							milliglyphs += (int)(power*1000);
							markDirty();
						}
					}
				} else if (getBlockMetadata() == 4) {
					// Distributor
				} else if (getBlockMetadata() == 5) {
					// Charger
				}
			}
		}
	}
	
	@Override
	public Packet getDescriptionPacket() {
		System.out.println("hi");
		NBTTagCompound comp = new NBTTagCompound();
		comp.setInteger("MilliGlyphs", milliglyphs);
		return new S35PacketUpdateTileEntity(xCoord, yCoord, zCoord, 0, comp);
	}
	
	@Override
	public void onDataPacket(NetworkManager net, S35PacketUpdateTileEntity pkt) {
		System.out.println("hello");
		this.milliglyphs = pkt.func_148857_g().getInteger("MilliGlyphs");
	}
	
	@Override
	public void readFromNBT(NBTTagCompound tag) {
		super.readFromNBT(tag);
		milliglyphs = tag.getInteger("MilliGlyphs");
	}
	
	@Override
	public void writeToNBT(NBTTagCompound tag) {
		super.writeToNBT(tag);
		tag.setInteger("MilliGlyphs", milliglyphs);
	}
	
	@SideOnly(Side.CLIENT)
	private void updateSound() {
		if (playerAnim > 0) {
			if (sound == null) {
				sound = new SoundEldritch(new ResourceLocation("lanthanoid", "pulsating"), this);
				Minecraft.getMinecraft().getSoundHandler().playSound(sound);
			}
		} else {
			if (sound != null) {
				Minecraft.getMinecraft().getSoundHandler().stopSound(sound);
				sound = null;
			}
		}
	}
	
	@Override
	public boolean shouldRenderInPass(int pass) {
		return pass == 1;
	}
}

package com.unascribed.lanthanoid.tile;

import com.unascribed.lanthanoid.client.SoundEldritch;
import com.unascribed.lanthanoid.effect.EntityGlyphFX;
import com.unascribed.lanthanoid.init.LBlocks;
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
	public int bookCount;
	
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
				if (milliglyphs > getMaxMilliglyphs()) {
					milliglyphs = getMaxMilliglyphs();
					syncMilliglyphs();
					markDirty();
				}
				if (getBlockMetadata() == 3) {
					// Collector
					if (milliglyphs < getMaxMilliglyphs() && ticksExisted % 10 == 0) {
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
								milliglyphs += (int)(power*1000);
								
								spawnParticles(mop.blockX, mop.blockY, mop.blockZ, power);
								syncMilliglyphs();
								markDirty();
							}
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
	
	public int getMaxMilliglyphs() {
		return 100000+(bookCount*20000);
	}

	private void spawnParticles(int blockX, int blockY, int blockZ, float power) {
		byte dX = (byte)(blockX-xCoord);
		byte dY = (byte)(blockY-yCoord);
		byte dZ = (byte)(blockZ-zCoord);
		
		int packed = 0;
		
		packed |= (((int)(power*4))&0xFF)<<24;
		packed |= ((dX&0xFF) << 16);
		packed |= ((dY&0xFF) << 8);
		packed |= (dZ&0xFF);
		
		worldObj.addBlockEvent(xCoord, yCoord, zCoord, LBlocks.machine, 2, packed);
	}

	public void syncMilliglyphs() {
		worldObj.addBlockEvent(xCoord, yCoord, zCoord, LBlocks.machine, 1, milliglyphs);
	}
	
	public void syncBooks() {
		worldObj.addBlockEvent(xCoord, yCoord, zCoord, LBlocks.machine, 3, bookCount);
	}
	
	@Override
	public boolean receiveClientEvent(int event, int arg) {
		if (event == 1) {
			milliglyphs = arg;
			return true;
		} else if (event == 2) {
			int pow = ((arg>>>24)&0xFF);
			int x = (byte)((arg>>>16)&0xFF);
			int y = (byte)((arg>>>8)&0xFF);
			int z = (byte)(arg&0xFF);
			if (FMLCommonHandler.instance().getSide().isClient() && FMLCommonHandler.instance().getEffectiveSide().isClient()) {
				spawnParticle(x, y, z, pow);
			}
			return true;
		} else if (event == 3) {
			bookCount = arg;
			return true;
		}
		return super.receiveClientEvent(event, arg);
	}
	
	@SideOnly(Side.CLIENT)
	private void spawnParticle(int x, int y, int z, int pow) {
		double spread = 0.25;
		for (int i = 0; i < pow*10; i++) {
			EntityGlyphFX fx = new EntityGlyphFX(worldObj, xCoord+0.5, yCoord+2, zCoord+0.5, x+(worldObj.rand.nextGaussian()*spread), y+(worldObj.rand.nextGaussian()*spread)-2, z+(worldObj.rand.nextGaussian()*spread));
			Minecraft.getMinecraft().effectRenderer.addEffect(fx);
		}
	}

	@Override
	public Packet getDescriptionPacket() {
		NBTTagCompound comp = new NBTTagCompound();
		comp.setInteger("MilliGlyphs", milliglyphs);
		comp.setInteger("Books", bookCount);
		return new S35PacketUpdateTileEntity(xCoord, yCoord, zCoord, 255, comp);
	}
	
	@Override
	public void onDataPacket(NetworkManager net, S35PacketUpdateTileEntity pkt) {
		this.milliglyphs = pkt.func_148857_g().getInteger("MilliGlyphs");
		this.bookCount = pkt.func_148857_g().getInteger("Books");
	}
	
	@Override
	public void readFromNBT(NBTTagCompound tag) {
		super.readFromNBT(tag);
		milliglyphs = tag.getInteger("MilliGlyphs");
		bookCount = tag.getInteger("Books");
	}
	
	@Override
	public void writeToNBT(NBTTagCompound tag) {
		super.writeToNBT(tag);
		tag.setInteger("MilliGlyphs", milliglyphs);
		tag.setInteger("Books", bookCount);
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

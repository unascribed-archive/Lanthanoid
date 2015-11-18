package com.unascribed.lanthanoid.tile;

import java.util.Collection;
import java.util.List;

import com.unascribed.lanthanoid.client.SoundEldritch;
import com.unascribed.lanthanoid.effect.EntityGlyphFX;
import com.unascribed.lanthanoid.init.LBlocks;
import com.unascribed.lanthanoid.util.LVec3;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityFallingBlock;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S12PacketEntityVelocity;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Vec3;
import net.minecraft.world.WorldServer;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.util.MovingObjectPosition.MovingObjectType;

public class TileEntityEldritch extends TileEntity {
	public static final int LAUNCH_COST = 10000;
	
	public int ticksExisted;
	public int playerAnim;
	public boolean playersNearby;
	
	public int milliglyphs;
	public int bookCount;
	
	public int bounceTicks = 40;
	public int bounceAnimTicks = 40;
	
	@SideOnly(Side.CLIENT)
	private SoundEldritch sound;
	
	@Override
	public void updateEntity() {
		ticksExisted++;
		if (hasWorldObj()) {
			double d = 2.5;
			AxisAlignedBB aabb = getBlockType().getCollisionBoundingBoxFromPool(worldObj, xCoord, yCoord, zCoord);
			playersNearby = !worldObj.getEntitiesWithinAABB(EntityPlayer.class, aabb.expand(d, d, d)).isEmpty();
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
			}
			if (getBlockMetadata() == 3) {
				// Collector
				if (!getWorldObj().isRemote && milliglyphs < getMaxMilliglyphs() && ticksExisted % 40 == 0) {
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
												if (tee.getBlockMetadata() != 3 &&
													tee.milliglyphs < tee.getMaxMilliglyphs()) {
													if (min == null || tee.milliglyphs < min.milliglyphs) {
														min = tee;
														minDiff = Integer.MAX_VALUE;
													}
												}
											} else {
												if (tee.milliglyphs > this.milliglyphs &&
														(tee.getBlockMetadata() == 3 || tee.getBlockMetadata() == 4)
														&& tee.milliglyphs < tee.getMaxMilliglyphs()) {
													if (max == null || tee.milliglyphs > max.milliglyphs) {
														max = tee;
														maxDiff = tee.milliglyphs-this.milliglyphs;
													}
												} else if (tee.milliglyphs < this.milliglyphs &&
														tee.getBlockMetadata() != 3 &&
														tee.getBlockMetadata() != 4) {
													if (min == null || tee.milliglyphs < min.milliglyphs) {
														min = tee;
														minDiff = this.milliglyphs-tee.milliglyphs;
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
			} else if (getBlockMetadata() == 5) {
				// Charger
			} else if (getBlockMetadata() == 6) {
				// Faith Plate
				bounceAnimTicks++;
				if (!worldObj.isRemote && milliglyphs >= LAUNCH_COST) {
					bounceTicks++;
					if (bounceTicks > 20) {
						boolean didSomething = false;
						Block b = worldObj.getBlock(xCoord, yCoord+1, zCoord);
						int meta = worldObj.getBlockMetadata(xCoord, yCoord+1, zCoord);
						if (b != null) {
							if (!b.isAir(worldObj, xCoord, yCoord+1, zCoord)) {
								EntityFallingBlock efb = new EntityFallingBlock(worldObj, xCoord+0.5, yCoord+1.5, zCoord+0.5, b, meta);
								TileEntity te = worldObj.getTileEntity(xCoord, yCoord+1, zCoord);
								if (te != null) {
									NBTTagCompound nbt = new NBTTagCompound();
									te.writeToNBT(nbt);
									efb.field_145810_d = nbt;
								}
								efb.motionY = 0.5;
								efb.field_145812_b = 2;
								worldObj.setBlockToAir(xCoord, yCoord+1, zCoord);
								if (!worldObj.isRemote) {
									worldObj.spawnEntityInWorld(efb);
								}
								didSomething = true;
							}
						}
						AxisAlignedBB box = aabb.offset(0, 1, 0);
						box.maxY = box.minY+0.01;
						for (Entity e : (List<Entity>)worldObj.getEntitiesWithinAABB(Entity.class, box)) {
							e.motionY = 1;
							e.fallDistance = 0;
							MinecraftServer.getServer().getConfigurationManager().sendToAllNear(e.posX, e.posY, e.posZ, 256, e.worldObj.provider.dimensionId, new S12PacketEntityVelocity(e));
							didSomething = true;
						}
						if (didSomething) {
							milliglyphs -= LAUNCH_COST;
							syncMilliglyphs();
							markDirty();
							worldObj.playSoundEffect(xCoord+0.5, yCoord+0.5, zCoord+0.5, "lanthanoid:launch", 1, 0.5f);
							bounceTicks = 0;
							worldObj.addBlockEvent(xCoord, yCoord, zCoord, LBlocks.machine, 4, 0);
						}
					}
				}
			}
		}
	}
	
	private boolean transferFrom(TileEntityEldritch te, boolean force) {
		int d;
		if (force) {
			d = Math.max(1000, te.milliglyphs/8);
		} else {
			d = (te.milliglyphs-this.milliglyphs)/8;
		} 
		int amt = Math.min(this.getMaxMilliglyphs()-this.milliglyphs, d);
		if (amt > te.milliglyphs) {
			amt = te.milliglyphs;
		}
		if (amt > 0) {
			te.milliglyphs -= amt;
			te.syncMilliglyphs();
			te.markDirty();
			this.milliglyphs += amt;
			syncMilliglyphs();
			spawnParticles(te.xCoord, te.yCoord, te.zCoord, amt/1000f);
			markDirty();
			return true;
		}
		return false;
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
		} else if (event == 4) {
			bounceAnimTicks = 0;
			return true;
		}
		return super.receiveClientEvent(event, arg);
	}
	
	@SideOnly(Side.CLIENT)
	private void spawnParticle(int x, int y, int z, int pow) {
		double spread = 0.25;
		for (int i = 0; i < (pow == 0 ? 1 : pow*5); i++) {
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
		bounceTicks = tag.getInteger("BounceTicks");
	}
	
	@Override
	public void writeToNBT(NBTTagCompound tag) {
		super.writeToNBT(tag);
		tag.setInteger("MilliGlyphs", milliglyphs);
		tag.setInteger("Books", bookCount);
		tag.setInteger("BounceTicks", bounceTicks);
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

package com.unascribed.lanthanoid.tile;

import com.unascribed.lanthanoid.client.SoundEldritch;
import com.unascribed.lanthanoid.effect.EntityGlyphFX;
import com.unascribed.lanthanoid.glyph.IGlyphHolder;
import com.unascribed.lanthanoid.init.LBlocks;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.WorldServer;

public abstract class TileEntityEldritch extends TileEntity implements IGlyphHolder, IBreakable {

	public int ticksExisted;
	public int playerAnim;
	public boolean playersNearby;
	
	private int milliglyphs;
	
	@SideOnly(Side.CLIENT)
	private SoundEldritch sound;
	
	protected abstract void doTickLogic();
	
	@Override
	public void updateEntity() {
		if (hasWorldObj()) {
			if (ticksExisted == 0) {
				ticksExisted = worldObj.rand.nextInt(65536);
			}
			ticksExisted++;
			if (worldObj.isRemote) {
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
			}
			if (FMLCommonHandler.instance().getSide().isClient() && FMLCommonHandler.instance().getEffectiveSide().isClient()) {
				updateSound();
			}
			if (!getWorld().isRemote) {
				if (getMilliglyphs() > getMaxMilliglyphs()) {
					setMilliglyphs(getMaxMilliglyphs());
				}
			}
			doTickLogic();
		}
	}
	
	@Override
	public boolean transferFrom(IGlyphHolder holder, boolean force) {
		int d;
		if (force) {
			d = Math.max(1000, holder.getMilliglyphs()/8);
		} else {
			d = (holder.getMilliglyphs()-this.getMilliglyphs())/8;
			if (d < 1000) {
				d = (holder.getMilliglyphs()-this.getMilliglyphs())/2;
			}
		} 
		int amt = Math.min(this.getMaxMilliglyphs()-this.getMilliglyphs(), d);
		if (amt > holder.getMilliglyphs()) {
			amt = holder.getMilliglyphs();
		}
		if (amt > 0) {
			holder.setMilliglyphs(holder.getMilliglyphs() - amt);
			this.setMilliglyphs(this.getMilliglyphs() + amt);
			if (holder instanceof TileEntity) {
				TileEntity te = (TileEntity)holder;
				spawnParticles(te.xCoord, te.yCoord, te.zCoord, amt/1000f);
			}
			return true;
		}
		return false;
	}

	@Override
	public void breakBlock() {
		if (getMilliglyphs() > 15000) {
			worldObj.playSoundEffect(xCoord+0.5, yCoord+0.5, zCoord+0.5, "lanthanoid:waste", 1f, 0.5f);
			if (worldObj instanceof WorldServer) {
				((WorldServer)worldObj).func_147487_a("enchantmenttable", xCoord+0.5, yCoord+0.5, zCoord+0.5, getMilliglyphs()/250, 0.25, 0.25, 0.25, 0);
			}
		}
	}
	
	@Override
	public int getMaxMilliglyphs() {
		return 100000;
	}

	protected void spawnParticles(int blockX, int blockY, int blockZ, float power) {
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

	@Override
	public boolean receiveClientEvent(int event, int arg) {
		if (event == 1) {
			setMilliglyphs(arg);
			return true;
		} else if (event == 2) {
			int pow = ((arg>>>24)&0xFF);
			int x = (byte)((arg>>>16)&0xFF) + xCoord;
			int y = (byte)((arg>>>8)&0xFF) + yCoord;
			int z = (byte)(arg&0xFF) + zCoord;
			if (FMLCommonHandler.instance().getSide().isClient() && FMLCommonHandler.instance().getEffectiveSide().isClient()) {
				spawnTransferParticlesClient(x, y, z, pow);
			}
			return true;
		}
		return super.receiveClientEvent(event, arg);
	}
	
	@SideOnly(Side.CLIENT)
	private void spawnTransferParticlesClient(int x, int y, int z, int pow) {
		spawnParticlesClient(x+0.5f, y+0.5f, z+0.5f, xCoord+0.5f, yCoord+0.5f, zCoord+0.5f, 0.25, pow);
	}
	
	@SideOnly(Side.CLIENT)
	protected void spawnParticlesClient(float sX, float sY, float sZ, float eX, float eY, float eZ, double spread, int pow) {
		for (int i = 0; i < (pow*1)*(2-Minecraft.getMinecraft().gameSettings.particleSetting); i++) {
			EntityGlyphFX fx = new EntityGlyphFX(worldObj, eX, eY+1.5, eZ, (sX-eX)+(worldObj.rand.nextGaussian()*spread), (sY-eY)+(worldObj.rand.nextGaussian()*spread)-2, (sZ-eZ)+(worldObj.rand.nextGaussian()*spread));
			Minecraft.getMinecraft().effectRenderer.addEffect(fx);
		}
	}

	@Override
	public Packet getDescriptionPacket() {
		NBTTagCompound comp = new NBTTagCompound();
		buildDescriptionPacket(comp);
		return new S35PacketUpdateTileEntity(xCoord, yCoord, zCoord, 255, comp);
	}
	
	protected void buildDescriptionPacket(NBTTagCompound nbt) {
		nbt.setInteger("MilliGlyphs", getMilliglyphs());
	}
	
	protected void processDescriptionPacket(NBTTagCompound nbt) {
		setMilliglyphs(nbt.getInteger("MilliGlyphs"));
	}
	
	@Override
	public final void onDataPacket(NetworkManager net, S35PacketUpdateTileEntity pkt) {
		processDescriptionPacket(pkt.getNbtCompound());
	}
	
	@Override
	public void readFromNBT(NBTTagCompound tag) {
		super.readFromNBT(tag);
		setMilliglyphs(tag.getInteger("MilliGlyphs"));
	}
	
	@Override
	public void writeToNBT(NBTTagCompound tag) {
		super.writeToNBT(tag);
		tag.setInteger("MilliGlyphs", getMilliglyphs());
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
		return pass == 0;
	}

	@Override
	public int getMilliglyphs() {
		return milliglyphs;
	}

	@Override
	public void setMilliglyphs(int milliglyphs) {
		this.milliglyphs = milliglyphs;
		if (hasWorldObj() && !worldObj.isRemote) {
			worldObj.addBlockEvent(xCoord, yCoord, zCoord, LBlocks.machine, 1, getMilliglyphs());
		}
		markDirty();
	}

}

package com.unascribed.lanthanoid.tile;

import java.util.List;

import com.unascribed.lanthanoid.init.LBlocks;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityFallingBlock;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.play.server.S12PacketEntityVelocity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;

public class TileEntityEldritchFaithPlate extends TileEntityEldritch implements IFallable, IActivatable {
	
	public static final int LAUNCH_COST = 10000;
	
	public int bounceTicks = 40;
	public int bounceAnimTicks = 40;
	
	@Override
	protected void doTickLogic() {
		bounceAnimTicks++;
		if (!worldObj.isRemote && getMilliglyphs() >= LAUNCH_COST) {
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
				AxisAlignedBB box = getBlockType().getCollisionBoundingBoxFromPool(worldObj, xCoord, yCoord, zCoord).offset(0, 1, 0);
				box.maxY = box.minY+0.01;
				box = box.contract(0.1, 0, 0.1);
				for (Entity e : (List<Entity>)worldObj.getEntitiesWithinAABB(Entity.class, box)) {
					e.motionY = 1;
					e.fallDistance = 0;
					MinecraftServer.getServer().getConfigurationManager().sendToAllNear(e.posX, e.posY, e.posZ, 256, e.worldObj.provider.dimensionId, new S12PacketEntityVelocity(e));
					didSomething = true;
				}
				if (didSomething) {
					setMilliglyphs(getMilliglyphs() - LAUNCH_COST);
					worldObj.playSoundEffect(xCoord+0.5, yCoord+0.5, zCoord+0.5, "lanthanoid:launch", 1, 0.5f);
					bounceTicks = 0;
					worldObj.addBlockEvent(xCoord, yCoord, zCoord, LBlocks.machine, 4, 0);
				}
			}
		}
	}
	
	@Override
	public void onFallenUpon(World world, int x, int y, int z, Entity entity, float fallDistance) {
		if (bounceTicks > 20) {
			entity.fallDistance = 0;
		}
	}
	
	@Override
	public boolean canReceiveGlyphs() {
		return true;
	}

	@Override
	public boolean canSendGlyphs() {
		return false;
	}

	@Override
	public void readFromNBT(NBTTagCompound tag) {
		super.readFromNBT(tag);
		bounceTicks = tag.getInteger("BounceTicks");
	}
	
	@Override
	public void writeToNBT(NBTTagCompound tag) {
		super.writeToNBT(tag);
		tag.setInteger("BounceTicks", bounceTicks);
	}
	
	@Override
	public boolean receiveClientEvent(int event, int arg) {
		if (event == 4) {
			bounceAnimTicks = 0;
			return true;
		}
		return super.receiveClientEvent(event, arg);
	}
	
	@Override
	public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side, float subX, float subY, float subZ) {
		if (player.getHeldItem() != null && !(player.getHeldItem().getItem() instanceof ItemBlock)) {
			if (!world.isRemote) {
				ItemStack stack = player.getHeldItem();
				EntityItem ent = new EntityItem(world, x + 0.5, y + 1.2, z + 0.5, stack);
				ent.motionY = -0.5;
				world.spawnEntityInWorld(ent);
				player.inventory.setInventorySlotContents(player.inventory.currentItem, null);
			}
			return true;
		}
		return false;
	}

}

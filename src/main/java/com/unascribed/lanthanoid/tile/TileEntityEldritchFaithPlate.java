package com.unascribed.lanthanoid.tile;

import java.util.List;

import com.google.common.collect.Lists;
import com.unascribed.lanthanoid.init.LBlocks;

import net.malisis.core.util.ItemUtils;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityFallingBlock;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.play.server.S12PacketEntityVelocity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants.NBT;

public class TileEntityEldritchFaithPlate extends TileEntityEldritch implements IFallable, IActivatable, IInventory {
	
	public static final int LAUNCH_COST = 2500;
	
	public int bounceTicks = 40;
	public int bounceAnimTicks = 40;
	public int animHeight = 0;
	
	private ItemStack stack;
	
	@Override
	protected void doTickLogic() {
		bounceAnimTicks++;
		if (!worldObj.isRemote && getMilliglyphs() >= LAUNCH_COST) {
			bounceTicks++;
			if (bounceTicks > 20) {
				List<Entity> affected = Lists.newArrayList();
				if (stack != null && canLaunch()) {
					EntityItem ent = new EntityItem(worldObj, xCoord + 0.5, yCoord + 1.2, zCoord + 0.5, stack);
					ent.delayBeforeCanPickup = 40;
					worldObj.spawnEntityInWorld(ent);
					affected.add(ent);
					stack = null;
					markDirty();
				}
				Block b = worldObj.getBlock(xCoord, yCoord+1, zCoord);
				int meta = worldObj.getBlockMetadata(xCoord, yCoord+1, zCoord);
				if (!(worldObj.getTileEntity(xCoord, yCoord+1, zCoord) instanceof TileEntityEldritchFaithPlate) && b != null) {
					if (!b.isAir(worldObj, xCoord, yCoord+1, zCoord)) {
						TileEntity te = worldObj.getTileEntity(xCoord, yCoord+1, zCoord);
						if (te == null) {
							EntityFallingBlock efb = new EntityFallingBlock(worldObj, xCoord+0.5, yCoord+1.5, zCoord+0.5, b, meta);
							efb.fallTime = 2;
							worldObj.setBlockToAir(xCoord, yCoord+1, zCoord);
							if (!worldObj.isRemote) {
								worldObj.spawnEntityInWorld(efb);
							}
							affected.add(efb);
						}
					}
				}
				AxisAlignedBB box = getBlockType().getCollisionBoundingBoxFromPool(worldObj, xCoord, yCoord, zCoord).offset(0, 1, 0);
				box.maxY = box.minY+0.01;
				box = box.contract(0.1, 0, 0.1);
				for (Entity e : (List<Entity>)worldObj.getEntitiesWithinAABB(Entity.class, box)) {
					affected.add(e);
				}
				if (!affected.isEmpty()) {
					List<TileEntityEldritchFaithPlate> plates = Lists.newArrayList(this);
					float velocity = (getAmountStacked(plates)/2f)+0.5f;
					for (Entity e : affected) {
						e.fallDistance = 0;
						if (e instanceof EntityFallingBlock) {
							e.motionY = Math.sqrt(velocity);
						} else {
							e.motionY = velocity;
						}
						MinecraftServer.getServer().getConfigurationManager().sendToAllNear(e.posX, e.posY, e.posZ, 256, e.worldObj.provider.dimensionId, new S12PacketEntityVelocity(e));
					}
					for (TileEntityEldritchFaithPlate fp : plates) {
						fp.setMilliglyphs(fp.getMilliglyphs()-LAUNCH_COST);
						fp.bounceTicks = 0;
					}
					worldObj.addBlockEvent(xCoord, yCoord, zCoord, LBlocks.machine, 4, plates.size());
					worldObj.playSoundEffect(xCoord+0.5, yCoord+0.5, zCoord+0.5, "lanthanoid:launch", 1, 0.5f);
				}
			}
		}
	}
	
	@Override
	protected void addDebugText(List<String> li) {
		li.add("\u00A77\u00A7l["+Integer.toHexString(getId())+"]");
		super.addDebugText(li);
		li.add(getAmountStacked()+"x");
	}
	
	public int getAmountStacked() {
		return getAmountStacked(null);
	}
	
	public int getAmountStacked(List<TileEntityEldritchFaithPlate> list) {
		int y = yCoord-1;
		int amt = 1;
		for (int i = 0; i < 6; i++) {
			TileEntity te = worldObj.getTileEntity(xCoord, y, zCoord);
			if (te instanceof TileEntityEldritchFaithPlate) {
				TileEntityEldritchFaithPlate tefp = (TileEntityEldritchFaithPlate)te;
				if (tefp.bounceTicks > 20 && tefp.getMilliglyphs() >= LAUNCH_COST) {
					amt++;
					if (list != null) {
						list.add(tefp);
					}
				}
				y--;
			} else {
				break;
			}
		}
		return amt;
	}

	@Override
	public void onFallenUpon(World world, int x, int y, int z, Entity entity, float fallDistance) {
		if (bounceTicks > 20) {
			entity.fallDistance = 0;
		}
	}
	
	@Override
	public void breakBlock() {
		super.breakBlock();
		if (stack != null) {
			float f = worldObj.rand.nextFloat() * 0.8F + 0.1F;
			float f1 = worldObj.rand.nextFloat() * 0.8F + 0.1F;
			float f2 = worldObj.rand.nextFloat() * 0.8F + 0.1F;
			EntityItem ent = new EntityItem(worldObj, xCoord + f, yCoord + f1, zCoord + f2, stack);
			float f3 = 0.05F;
			ent.motionX = worldObj.rand.nextGaussian() * f3;
			ent.motionY = worldObj.rand.nextGaussian() * f3 + 0.2F;
			ent.motionZ = worldObj.rand.nextGaussian() * f3;
			worldObj.spawnEntityInWorld(ent);
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
		if (tag.hasKey("PendingStack", NBT.TAG_COMPOUND)) {
			stack = ItemStack.loadItemStackFromNBT(tag.getCompoundTag("PendingStack"));
		} else {
			stack = null;
		}
	}
	
	@Override
	public void writeToNBT(NBTTagCompound tag) {
		super.writeToNBT(tag);
		tag.setInteger("BounceTicks", bounceTicks);
		if (stack != null) {
			NBTTagCompound stackTag = new NBTTagCompound();
			stack.writeToNBT(stackTag);
			tag.setTag("PendingStack", stackTag);
		}
	}
	
	@Override
	public boolean receiveClientEvent(int event, int arg) {
		if (event == 4) {
			bounceAnimTicks = 0;
			animHeight = arg;
			return true;
		}
		return super.receiveClientEvent(event, arg);
	}
	
	@Override
	public boolean onBlockActivated(EntityPlayer player, int side, float subX, float subY, float subZ) {
		if (!worldObj.isRemote && canLaunch()) {
			if (stack == null) {
				stack = player.getHeldItem();
				player.setCurrentItemOrArmor(0, null);
				markDirty();
			} else if (ItemUtils.areItemStacksStackable(stack, player.getHeldItem())) {
				int s = Math.min(player.getHeldItem().stackSize, 64-stack.stackSize);
				stack.stackSize += s;
				player.getHeldItem().stackSize -= s;
				if (player.getHeldItem().stackSize <= 0) {
					player.setCurrentItemOrArmor(0, null);
				}
				markDirty();
			} else {
				ItemStack swap = player.getHeldItem();
				player.setCurrentItemOrArmor(0, stack);
				stack = swap;
				markDirty();
			}
		}
		return true;
	}

	
	private boolean canLaunch() {
		return worldObj.getBlock(xCoord, yCoord+1, zCoord).isAir(worldObj, xCoord, yCoord+1, zCoord);
	}

	@Override
	public int getSizeInventory() {
		return 1;
	}

	@Override
	public ItemStack getStackInSlot(int p_70301_1_) {
		return p_70301_1_ == 0 ? stack : null;
	}

	@Override
	public ItemStack decrStackSize(int index, int count) {
		ItemStack itemstack;

		if (stack.stackSize <= count) {
			itemstack = stack;
			stack = null;
			markDirty();
			return itemstack;
		} else {
			itemstack = stack.splitStack(count);

			if (stack.stackSize == 0) {
				stack = null;
			}

			markDirty();
			return itemstack;
		}
	}

	@Override
	public ItemStack getStackInSlotOnClosing(int p_70304_1_) {
		ItemStack s = stack;
		stack = null;
		return s;
	}

	@Override
	public void setInventorySlotContents(int p_70299_1_, ItemStack p_70299_2_) {
		stack = p_70299_2_;
	}

	@Override
	public String getInventoryName() {
		return "inventory.eldritch_faith_plate";
	}

	@Override
	public boolean isCustomInventoryName() {
		return false;
	}

	@Override
	public int getInventoryStackLimit() {
		return 64;
	}

	@Override
	public boolean isUseableByPlayer(EntityPlayer p_70300_1_) {
		return false;
	}

	@Override
	public void openChest() {
	}

	@Override
	public void closeChest() {
	}

	@Override
	public boolean isItemValidForSlot(int p_94041_1_, ItemStack p_94041_2_) {
		return true;
	}

}

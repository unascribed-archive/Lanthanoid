package com.unascribed.lanthanoid.tile;

import com.unascribed.lanthanoid.init.LBlocks;

import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

public abstract class TileEntityEldritchWithBooks extends TileEntityEldritch implements IActivatable, IBreakable {

	private int bookCount;
	
	@Override
	public int getMaxMilliglyphs() {
		return super.getMaxMilliglyphs()+(getBookCount()*20000);
	}
	
	public int getBookCount() {
		return bookCount;
	}

	public void setBookCount(int bookCount) {
		this.bookCount = bookCount;
		if (!worldObj.isRemote) {
			worldObj.addBlockEvent(xCoord, yCoord, zCoord, LBlocks.machine, 3, getBookCount());
		}
		markDirty();
	}
	
	@Override
	public boolean receiveClientEvent(int event, int arg) {
		if (event == 3) {
			setBookCount(arg);
			return true;
		}
		return super.receiveClientEvent(event, arg);
	}
	
	@Override
	public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side, float subX, float subY, float subZ) {
		if (player.isSneaking()) {
			if (getBookCount() > 0) {
				if (!world.isRemote) {
					setBookCount(getBookCount() - 1);
					if (world.getGameRules().getGameRuleBooleanValue("doTileDrops")) {
						player.entityDropItem(new ItemStack(Items.book), 0.5f);
					}
				}
				return true;
			}
		} else if (player.getHeldItem() != null && player.getHeldItem().getItem() == Items.book) {
			if (getBookCount() < 5) {
				if (!world.isRemote) {
					if (!player.capabilities.isCreativeMode) {
						player.getHeldItem().stackSize--;
					}
					setBookCount(getBookCount() + 1);
				}
				return true;
			}
		}
		return false;
	}
	
	@Override
	public void breakBlock(World world, int x, int y, int z) {
		float f = world.rand.nextFloat() * 0.8F + 0.1F;
		float f1 = world.rand.nextFloat() * 0.8F + 0.1F;
		for (int i = 0; i < getBookCount(); i++) {
			float f2 = world.rand.nextFloat() * 0.8F + 0.1F;
			EntityItem ent = new EntityItem(world, x + f, y + f1, z + f2, new ItemStack(Items.book));
			float f3 = 0.05F;
			ent.motionX = world.rand.nextGaussian() * f3;
			ent.motionY = world.rand.nextGaussian() * f3 + 0.2F;
			ent.motionZ = world.rand.nextGaussian() * f3;
			world.spawnEntityInWorld(ent);
		}
	}
	
	@Override
	public void buildDescriptionPacket(NBTTagCompound nbt) {
		super.buildDescriptionPacket(nbt);
		nbt.setInteger("Books", getBookCount());
	}
	
	@Override
	protected void processDescriptionPacket(NBTTagCompound nbt) {
		super.processDescriptionPacket(nbt);
		setBookCount(nbt.getInteger("Books"));
	}
	
	@Override
	public void writeToNBT(NBTTagCompound tag) {
		super.writeToNBT(tag);
		tag.setInteger("Books", getBookCount());
	}
	
	@Override
	public void readFromNBT(NBTTagCompound tag) {
		super.readFromNBT(tag);
		setBookCount(tag.getInteger("Books"));
	}

}

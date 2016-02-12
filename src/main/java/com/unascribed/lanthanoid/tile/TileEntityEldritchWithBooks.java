package com.unascribed.lanthanoid.tile;

import com.unascribed.lanthanoid.init.LBlocks;
import com.unascribed.lanthanoid.init.LItems;

import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

public abstract class TileEntityEldritchWithBooks extends TileEntityEldritch implements IActivatable {

	private int bookCount;
	
	@Override
	public void updateEntity() {
		int x = xCoord;
		int y = yCoord+1;
		int z = zCoord;
		if (hasWorldObj() && !worldObj.isRemote && worldObj.getBlock(x, y, z) != null && worldObj.getBlock(x, y, z).isOpaqueCube()) {
			dropBooks(false);
			setBookCount(0);
		}
		super.updateEntity();
	}
	
	@Override
	public int getMaxMilliglyphs() {
		return super.getMaxMilliglyphs()+(getBookCount()*20000);
	}
	
	public int getBookCount() {
		return bookCount;
	}

	public void setBookCount(int bookCount) {
		this.bookCount = bookCount;
		if (hasWorldObj() && !worldObj.isRemote) {
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
	public boolean onBlockActivated(EntityPlayer player, int side, float subX, float subY, float subZ) {
		if (player.isSneaking()) {
			if (getBookCount() > 0) {
				if (!worldObj.isRemote) {
					setBookCount(getBookCount() - 1);
					if (worldObj.getGameRules().getGameRuleBooleanValue("doTileDrops")) {
						int amt = 0;
						int newMax = getMaxMilliglyphs();
						if (getMilliglyphs() > newMax) {
							amt = getMilliglyphs()-newMax;
						}
						System.out.println(amt);
						ItemStack stack;
						if (amt <= 0) {
							stack = new ItemStack(Items.book);
						} else {
							stack = new ItemStack(LItems.charged_book);
							LItems.charged_book.setMilliglyphs(stack, amt);
						}
						player.entityDropItem(stack, 0.5f);
					}
				}
				return true;
			}
		} else if (player.getHeldItem() != null && (player.getHeldItem().getItem() == Items.book || player.getHeldItem().getItem() == LItems.charged_book)) {
			int x = xCoord;
			int y = yCoord+1;
			int z = zCoord;
			if (getBookCount() < 5 && (worldObj.getBlock(x, y, z) == null || !worldObj.getBlock(x, y, z).isOpaqueCube())) {
				if (!worldObj.isRemote) {
					if (!player.capabilities.isCreativeMode) {
						player.getHeldItem().stackSize--;
					}
					setBookCount(getBookCount() + 1);
					if (player.getHeldItem().getItem() == LItems.charged_book) {
						setMilliglyphs(getMilliglyphs()+LItems.charged_book.getMilliglyphs(player.getHeldItem()));
					}
				}
				return true;
			}
		}
		return false;
	}
	
	@Override
	public void breakBlock() {
		super.breakBlock();
		dropBooks(true);
	}
	
	protected void dropBooks(boolean center) {
		float f = worldObj.rand.nextFloat() * 0.8F + 0.1F;
		float f1 = worldObj.rand.nextFloat() * 0.8F + 0.1F;
		if (!center) {
			f1 += 0.6f;
		}
		for (int i = 0; i < getBookCount(); i++) {
			float f2 = worldObj.rand.nextFloat() * 0.8F + 0.1F;
			EntityItem ent = new EntityItem(worldObj, xCoord + f, yCoord + f1, zCoord + f2, new ItemStack(Items.book));
			float f3 = 0.05F;
			ent.motionX = worldObj.rand.nextGaussian() * f3;
			ent.motionY = worldObj.rand.nextGaussian() * f3 + 0.2F;
			ent.motionZ = worldObj.rand.nextGaussian() * f3;
			worldObj.spawnEntityInWorld(ent);
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

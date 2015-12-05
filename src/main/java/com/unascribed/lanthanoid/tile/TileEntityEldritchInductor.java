package com.unascribed.lanthanoid.tile;

import com.unascribed.lanthanoid.glyph.IGlyphHolderItem;
import com.unascribed.lanthanoid.init.LItems;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryBasic;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.AxisAlignedBB;
import net.minecraftforge.common.util.Constants.NBT;

public class TileEntityEldritchInductor extends TileEntityEldritch implements IInventory, IActivatable, IBounded {

	private InventoryBasic inv = new InventoryBasic("eldritch_inductor", false, 4) {
		@Override
		public int getInventoryStackLimit() {
			return 1;
		}
	};
	private AxisAlignedBB aabb = AxisAlignedBB.getBoundingBox(0, 0, 0, 1, 31/32D, 1);
	
	
	@Override
	protected void doTickLogic() {
		if (ticksExisted % 20 == 0) {
			int minI = -1;
			ItemStack minStack = null;
			IGlyphHolderItem min = null;
			for (int i = 0; i < getSizeInventory(); i++) {
				ItemStack stack = getStackInSlot(i);
				if (stack != null) {
					if (stack.getItem() == Items.book) {
						stack = new ItemStack(LItems.charged_book);
						setInventorySlotContents(i, stack);
					}
					if (stack.getItem() instanceof IGlyphHolderItem) {
						IGlyphHolderItem holder = (IGlyphHolderItem) stack.getItem();
						if (holder.getMilliglyphs(stack) < holder.getMaxMilliglyphs(stack)) {
							if (min == null || holder.getMilliglyphs(stack) < min.getMilliglyphs(minStack)) {
								minI = i;
								min = holder;
								minStack = stack;
							}
						}
					}
				}
			}
			if (min != null) {
				int transfer = Math.min(getMilliglyphs()/8, (min.getMaxMilliglyphs(minStack)-min.getMilliglyphs(minStack))/8);
				int room = (min.getMaxMilliglyphs(minStack)-min.getMilliglyphs(minStack));
				if (transfer > room) {
					transfer = room;
				}
				if (transfer > 0) {
					min.setMilliglyphs(minStack, min.getMilliglyphs(minStack)+transfer);
					if (!worldObj.isRemote) {
						setMilliglyphs(getMilliglyphs()-transfer);
						worldObj.addBlockEvent(xCoord, yCoord, zCoord, getBlockType(), 3, (((transfer/250)&0xFFFF)<<16) | (minI&0xFFFF));
					}
				}
			}
		}
	}
	
	@Override
	public boolean receiveClientEvent(int event, int arg) {
		if (event == 3) {
			if (FMLCommonHandler.instance().getSide().isClient() && FMLCommonHandler.instance().getEffectiveSide().isClient()) {
				int i = arg & 0xFFFF;
				int pow = (arg >> 16) & 0xFFFF;
				spawnChargeParticlesClient(i, pow);
			}
			return true;
		}
		return super.receiveClientEvent(event, arg);
	}
	
	@SideOnly(Side.CLIENT)
	protected void spawnChargeParticlesClient(int i, int pow) {
		float iX = (i % 2 == 0 ? 0.75f : 0.25f);
		float iZ = (i > 1 ? 0.25f : 0.75f);
		spawnParticlesClient(xCoord+0.5f, yCoord+0.95f, zCoord+0.5f, xCoord+iX, yCoord+0.75f, zCoord+iZ, 0.25, pow);
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
	public void breakBlock() {
		super.breakBlock();
		float f = worldObj.rand.nextFloat() * 0.8F + 0.1F;
		float f1 = worldObj.rand.nextFloat() * 0.8F + 0.1F;
		for (int i = 0; i < getSizeInventory(); i++) {
			if (getStackInSlot(i) == null) continue;
			float f2 = worldObj.rand.nextFloat() * 0.8F + 0.1F;
			EntityItem ent = new EntityItem(worldObj, xCoord + f, yCoord + f1, zCoord + f2, getStackInSlotOnClosing(i));
			float f3 = 0.05F;
			ent.motionX = worldObj.rand.nextGaussian() * f3;
			ent.motionY = worldObj.rand.nextGaussian() * f3 + 0.2F;
			ent.motionZ = worldObj.rand.nextGaussian() * f3;
			worldObj.spawnEntityInWorld(ent);
		}
	}

	@Override
	public boolean onBlockActivated(EntityPlayer player, int side, float subX, float subY, float subZ) {
		if (subY >= aabb.maxY) {
			boolean right = (subX < 0.5f);
			boolean bottom = (subZ < 0.5f);
			int idx = 0;
			if (right) {
				idx += 1;
			}
			if (bottom) {
				idx += 2;
			}
			if (!worldObj.isRemote) {
				ItemStack held = player.getHeldItem();
				if (getStackInSlot(idx) == null) {
					if (held != null && (held.getItem() instanceof IGlyphHolderItem || held.getItem() == Items.book)) {
						setInventorySlotContents(idx, held.splitStack(1));
						if (held.stackSize <= 0) {
							player.setCurrentItemOrArmor(0, null);
						}
						return true;
					}
				} else {
					EntityItem ei = new EntityItem(worldObj, xCoord+(right ? 0.25 : 0.75), yCoord+1.25, zCoord+(bottom ? 0.25 : 0.75), getStackInSlotOnClosing(idx));
					ei.delayBeforeCanPickup = 10;
					ei.motionX = 0;
					ei.motionY = 0;
					ei.motionZ = 0;
					worldObj.spawnEntityInWorld(ei);
					return true;
				}
			}
		}
		return false;
	}
	
	@Override
	protected void buildDescriptionPacket(NBTTagCompound nbt) {
		super.buildDescriptionPacket(nbt);
		writeInventory(nbt);
	}

	private void writeInventory(NBTTagCompound nbt) {
		NBTTagList slots = new NBTTagList();
		for (int i = 0; i < getSizeInventory(); i++) {
			if (getStackInSlot(i) != null) {
				NBTTagCompound tag = new NBTTagCompound();
				tag.setByte("Slot", (byte)i);
				getStackInSlot(i).writeToNBT(tag);
				slots.appendTag(tag);
			}
		}
		nbt.setTag("Contents", slots);
	}
	
	@Override
	protected void processDescriptionPacket(NBTTagCompound nbt) {
		super.processDescriptionPacket(nbt);
		readInventory(nbt);
	}

	private void readInventory(NBTTagCompound nbt) {
		for (int i = 0; i < getSizeInventory(); i++) {
			setInventorySlotContents(i, null);
		}
		NBTTagList li = nbt.getTagList("Contents", NBT.TAG_COMPOUND);
		for (int i = 0; i < li.tagCount(); i++) {
			NBTTagCompound comp = li.getCompoundTagAt(i);
			setInventorySlotContents(comp.getInteger("Slot"), ItemStack.loadItemStackFromNBT(comp));
		}
	}
	
	@Override
	public void readFromNBT(NBTTagCompound tag) {
		super.readFromNBT(tag);
		readInventory(tag);
	}
	
	@Override
	public void writeToNBT(NBTTagCompound tag) {
		super.writeToNBT(tag);
		writeInventory(tag);
	}
	
	@Override
	public AxisAlignedBB getBoundingBox() {
		return aabb;
	}
	

	@Override
	public int getSizeInventory() {
		return inv.getSizeInventory();
	}

	@Override
	public ItemStack getStackInSlot(int slotIn) {
		return inv.getStackInSlot(slotIn);
	}

	@Override
	public ItemStack decrStackSize(int index, int count) {
		return inv.decrStackSize(index, count);
	}

	@Override
	public ItemStack getStackInSlotOnClosing(int index) {
		ItemStack stack = getStackInSlot(index);
		if (stack != null) {
			setInventorySlotContents(index, null);
			return stack;
		} else {
			return null;
		}
	}

	@Override
	public void setInventorySlotContents(int index, ItemStack stack) {
		inv.setInventorySlotContents(index, stack);
		markDirty();
		if (hasWorldObj()) {
			worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
		}
	}

	@Override
	public String getInventoryName() {
		return inv.getInventoryName();
	}

	@Override
	public boolean isCustomInventoryName() {
		return inv.isCustomInventoryName();
	}

	@Override
	public int getInventoryStackLimit() {
		return inv.getInventoryStackLimit();
	}

	@Override
	public boolean isUseableByPlayer(EntityPlayer player) {
		return inv.isUseableByPlayer(player);
	}

	@Override
	public void openChest() {
		inv.openChest();
	}

	@Override
	public void closeChest() {
		inv.closeChest();
	}

	@Override
	public boolean isItemValidForSlot(int index, ItemStack stack) {
		return inv.isItemValidForSlot(index, stack);
	}

}

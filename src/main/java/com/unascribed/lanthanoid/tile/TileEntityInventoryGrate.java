package com.unascribed.lanthanoid.tile;

import java.util.List;

import com.google.common.collect.Lists;
import net.minecraft.command.IEntitySelector;
import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.EntityHorse;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

public class TileEntityInventoryGrate extends TileEntity implements IInventory {
	public class Slot {
		public IInventory owner;
		public int index;
		public Slot(IInventory owner, int index) {
			this.owner = owner;
			this.index = index;
		}
	}
	public interface InventoryAction<T> {
		T perform(IInventory inventory, int slot);
	}
	public interface InventoryBiAction<T, U> {
		T perform(IInventory inventory, int slot, U arg);
	}
	
	public interface InventoryVoidAction {
		void perform(IInventory inventory, int slot);
	}
	public interface InventoryBiVoidAction<U> {
		void perform(IInventory inventory, int slot, U arg);
	}
	
	private List<Entity> standingEntities = Lists.newArrayList();
	
	@Override
	public void updateEntity() {
		if (hasWorldObj()) {
			AxisAlignedBB aabb = getBlockType().getCollisionBoundingBoxFromPool(getWorld(), xCoord, yCoord, zCoord);
			aabb.offset(0, Math.abs(aabb.maxY-aabb.minY), 0);
			standingEntities.clear();
			selectEntitiesWithinAABBReusingList(worldObj, Entity.class, aabb, standingEntities, null);
		} else {
			standingEntities.clear();
		}
	}
	
	private static <T extends Entity> void selectEntitiesWithinAABBReusingList(World w, Class<T> clazz, AxisAlignedBB bb, List<T> li, IEntitySelector selector) {
		int minX = MathHelper.floor_double((bb.minX - World.MAX_ENTITY_RADIUS) / 16.0D);
		int maxX = MathHelper.floor_double((bb.maxX + World.MAX_ENTITY_RADIUS) / 16.0D);
		int minZ = MathHelper.floor_double((bb.minZ - World.MAX_ENTITY_RADIUS) / 16.0D);
		int maxZ = MathHelper.floor_double((bb.maxZ + World.MAX_ENTITY_RADIUS) / 16.0D);

		for (int x = minX; x <= maxX; ++x) {
			for (int z = minZ; z <= maxZ; ++z) {
				if (w.getChunkProvider().chunkExists(x, z)) {
					w.getChunkFromChunkCoords(x, z).getEntitiesOfTypeWithinAAAB(clazz, bb, li, selector);
				}
			}
		}
	}
	
	private Slot search(int slot) {
		if (standingEntities.isEmpty()) return null;
		IInventory inv = null;
		int i = 0;
		for (Entity e : standingEntities) {
			inv = unwrapInventory(e);
			if (inv == null) continue;
			int size = inv.getSizeInventory();
			if (slot < i+size) {
				slot -= i;
				break;
			}
			i += size;
		}
		if (inv == null) return null;
		return new Slot(inv, slot);
	}
	
	private IInventory unwrapInventory(Entity e) {
		if (e instanceof EntityHorse) {
			return ((EntityHorse)e).horseChest;
		} else if (e instanceof EntityPlayer) {
			return ((EntityPlayer)e).inventory;
		} else if (e instanceof IInventory) {
			return ((IInventory)e);
		}
		return null;
	}
	
	private <T> T performOnOwningInventory(int slot, InventoryAction<T> action) {
		Slot s = search(slot);
		if (s.owner == null) return null;
		return action.perform(s.owner, s.index);
	}
	
	private <T, U> T performOnOwningInventory(int slot, U arg, InventoryBiAction<T, U> action) {
		Slot s = search(slot);
		if (s.owner == null) return null;
		return action.perform(s.owner, s.index, arg);
	}
	
	/*private void performOnOwningInventory(int slot, InventoryVoidAction action) {
		Slot s = search(slot);
		if (s.owner != null) {
			action.perform(s.owner, s.index);
		}
	}*/
	
	private <U> void performOnOwningInventory(int slot, U arg, InventoryBiVoidAction<U> action) {
		Slot s = search(slot);
		if (s.owner != null) {
			action.perform(s.owner, s.index, arg);
		}
	}
	
	@Override
	public int getSizeInventory() {
		int size = 0;
		for (Entity e : standingEntities) {
			IInventory inv = unwrapInventory(e);
			if (inv != null) size += inv.getSizeInventory();
		}
		return size;
	}

	@Override
	public ItemStack getStackInSlot(int slot) {
		return performOnOwningInventory(slot, IInventory::getStackInSlot);
	}

	@Override
	public ItemStack decrStackSize(int slot, int count) {
		return performOnOwningInventory(slot, count, IInventory::decrStackSize);
	}

	@Override
	public ItemStack getStackInSlotOnClosing(int slot) {
		return performOnOwningInventory(slot, IInventory::getStackInSlotOnClosing);
	}

	@Override
	public void setInventorySlotContents(int slot, ItemStack stack) {
		performOnOwningInventory(slot, stack, IInventory::setInventorySlotContents);
	}

	@Override
	public String getInventoryName() {
		return "Inventory Grate";
	}

	@Override
	public boolean isCustomInventoryName() {
		return false;
	}

	@Override
	public int getInventoryStackLimit() {
		if (standingEntities.isEmpty()) return 64;
		int max = 0;
		for (Entity e : standingEntities) {
			IInventory inv = unwrapInventory(e);
			if (inv == null) continue;
			max = Math.max(max, inv.getInventoryStackLimit());
		}
		return max;
	}

	@Override
	public boolean isUseableByPlayer(EntityPlayer player) {
		return false;
	}

	@Override
	public void openChest() {
		
	}

	@Override
	public void closeChest() {
		
	}

	@Override
	public boolean isItemValidForSlot(int index, ItemStack stack) {
		return performOnOwningInventory(index, stack, IInventory::isItemValidForSlot);
	}

}

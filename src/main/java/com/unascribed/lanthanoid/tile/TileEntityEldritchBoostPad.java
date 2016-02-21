package com.unascribed.lanthanoid.tile;

import java.util.List;

import com.unascribed.lanthanoid.init.LBlocks;
import com.unascribed.lanthanoid.item.eldritch.armor.ItemEldritchBoots;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.IIcon;
import net.minecraft.util.MathHelper;

public class TileEntityEldritchBoostPad extends TileEntityEldritch implements IIconProvider, IPlaceable {

	private EnumFacing facing = null;
	private boolean textureAnimating = false;
	
	@Override
	protected void doTickLogic() {
		if (facing == null || getMilliglyphs() < 100) return;
		AxisAlignedBB aabb = getBlockType().getCollisionBoundingBoxFromPool(worldObj, xCoord, yCoord, zCoord).expand(0, 0.5, 0);
		for (Entity e : (List<Entity>)worldObj.getEntitiesWithinAABB(Entity.class, aabb)) {
			if (getMilliglyphs() < 100) break;
			double m = 0.05;
			e.addVelocity(facing.getFrontOffsetX()*m, facing.getFrontOffsetY()*m, facing.getFrontOffsetZ()*m);
			for (int i = 0; i < 4; i++) {
				worldObj.spawnParticle("enchantmenttable", e.posX+worldObj.rand.nextGaussian()/4, yCoord+1.5, e.posZ+worldObj.rand.nextGaussian()/4, facing.getFrontOffsetX()*-1, -0.5, facing.getFrontOffsetZ()*-1);
			}
			setMilliglyphs(Math.max(0, getMilliglyphs()-10));
			if (e instanceof EntityPlayer) {
				EntityPlayer ep = (EntityPlayer)e;
				if (ep.isSprinting()) {
					ItemStack boots = ep.inventory.armorItemInSlot(0);
					int dir = MathHelper.floor_double(ep.rotationYaw * 4.0F / 360.0F + 0.5D) & 3;
					boolean correctFacing = false;
					switch (dir) {
						case 0:
							correctFacing = (facing == EnumFacing.SOUTH);
							break;
						case 1:
							correctFacing = (facing == EnumFacing.WEST);
							break;
						case 2:
							correctFacing = (facing == EnumFacing.NORTH);
							break;
						case 3:
							correctFacing = (facing == EnumFacing.EAST);
							break;
					}
					if (boots != null && boots.getItem() instanceof ItemEldritchBoots) {
						setMilliglyphs(Math.max(0, getMilliglyphs()-50));
						ItemEldritchBoots ieb = (ItemEldritchBoots)boots.getItem();
						if (correctFacing) {
							ieb.setSprintingTicks(boots, ieb.getSprintingTicks(boots)+20);
						} else {
							ieb.setSprintingTicks(boots, Math.max(0, ieb.getSprintingTicks(boots)-50));
						}
					}
				}
			}
		}
	}
	
	@Override
	public void setMilliglyphs(int milliglyphs) {
		super.setMilliglyphs(milliglyphs);
		if ((milliglyphs < 100) != textureAnimating && hasWorldObj() && worldObj.isRemote) {
			worldObj.markBlockForRenderUpdate(xCoord, yCoord, zCoord);
		}
	}
	
	@Override
	protected void addDebugText(List<String> li) {
		li.add("\u00A73\u00A7l["+Integer.toHexString(getId())+"]");
		super.addDebugText(li);
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
	protected void buildDescriptionPacket(NBTTagCompound nbt) {
		super.buildDescriptionPacket(nbt);
		if (facing != null) {
			nbt.setInteger("Facing", facing.ordinal());
		}
	}
	
	@Override
	protected void processDescriptionPacket(NBTTagCompound nbt) {
		super.processDescriptionPacket(nbt);
		if (nbt.hasKey("Facing", 99)) {
			facing = EnumFacing.values()[nbt.getInteger("Facing")%EnumFacing.values().length];
		} else {
			facing = null;
		}
		worldObj.markBlockForRenderUpdate(xCoord, yCoord, zCoord);
	}
	
	@Override
	public void writeToNBT(NBTTagCompound tag) {
		super.writeToNBT(tag);
		if (facing != null) {
			tag.setInteger("Facing", facing.ordinal());
		}
	}
	
	@Override
	public void readFromNBT(NBTTagCompound tag) {
		super.readFromNBT(tag);
		if (tag.hasKey("Facing", 99)) {
			facing = EnumFacing.values()[tag.getInteger("Facing")%EnumFacing.values().length];
		} else {
			facing = null;
		}
	}

	@Override
	public IIcon getIcon(int side) {
		if (side == 1 || side == 0) {
			if (facing == null) return null;
			if (getMilliglyphs() < 100) {
				textureAnimating = false;
				switch (facing) {
					case NORTH:
						return LBlocks.machine.boost_pad_noanim;
					case EAST:
						return LBlocks.machine.boost_pad_90_noanim;
					case SOUTH:
						return LBlocks.machine.boost_pad_180_noanim;
					case WEST:
						return LBlocks.machine.boost_pad_270_noanim;
					default:
						break;
				}
			} else {
				textureAnimating = true;
				switch (facing) {
					case NORTH:
						return LBlocks.machine.boost_pad;
					case EAST:
						return LBlocks.machine.boost_pad_90;
					case SOUTH:
						return LBlocks.machine.boost_pad_180;
					case WEST:
						return LBlocks.machine.boost_pad_270;
					default:
						break;
				}
			}
		}
		return null;
	}

	@Override
	public void onBlockPlacedBy(EntityLivingBase placer, ItemStack stack) {
		int dir = MathHelper.floor_double(placer.rotationYaw * 4.0F / 360.0F + 0.5D) & 3;
		switch (dir) {
			case 0:
				facing = EnumFacing.SOUTH;
				break;
			case 1:
				facing = EnumFacing.WEST;
				break;
			case 2:
				facing = EnumFacing.NORTH;
				break;
			case 3:
				facing = EnumFacing.EAST;
				break;
		}
		markDirty();
		worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
	}

}

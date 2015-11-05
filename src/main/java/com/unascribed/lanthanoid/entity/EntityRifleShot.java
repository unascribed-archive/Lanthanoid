package com.unascribed.lanthanoid.entity;

import com.unascribed.lanthanoid.item.rifle.PrimaryMode;

import cpw.mods.fml.common.FMLCommonHandler;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.event.world.BlockEvent;

public class EntityRifleShot extends Entity {

	private static final int MODE_INDEX = 12;
	
	protected EntityLivingBase shooter;
	
	public EntityRifleShot(World world) {
		super(world);
	}
	
    public EntityRifleShot(World world, EntityLivingBase shooter, float speed) {
        super(world);
        this.shooter = shooter;
        setSize(0.25F, 0.25F);
        setLocationAndAngles(shooter.posX, shooter.posY + (double)shooter.getEyeHeight(), shooter.posZ, shooter.rotationYaw, shooter.rotationPitch);
        Vec3 look = shooter.getLookVec();
        setVelocity(look.xCoord*speed, look.yCoord*speed, look.zCoord*speed);
    }

	@Override
	protected void entityInit() {
		dataWatcher.addObjectByDataType(MODE_INDEX, 0/*byte*/);
	}

	@Override
	public void onUpdate() {
		lastTickPosX = posX;
		lastTickPosY = posY;
		lastTickPosZ = posZ;
		setPosition(posX + motionX, posY + motionY, posZ + motionZ);
		MovingObjectPosition mop = worldObj.rayTraceBlocks(Vec3.createVectorHelper(lastTickPosX, lastTickPosY, lastTickPosZ),
				Vec3.createVectorHelper(posX, posY, posZ));
		if (mop != null && mop.typeOfHit != MovingObjectPosition.MovingObjectType.MISS) {
			onImpact(mop);
		}
		
		if (ticksExisted > 40) {
			setDead();
		}
		if (FMLCommonHandler.instance().getSide().isClient() && FMLCommonHandler.instance().getEffectiveSide().isClient()) {
			//spawnParticles();
		}
	}
	
	
	@Override
	public void readEntityFromNBT(NBTTagCompound tag) {
		dataWatcher.updateObject(MODE_INDEX, tag.getByte("Mode"));
	}

	@Override
	public void writeEntityToNBT(NBTTagCompound tag) {
		tag.setByte("Mode", dataWatcher.getWatchableObjectByte(MODE_INDEX));
	}

	protected void onImpact(MovingObjectPosition mop) {
		System.out.println(mop);
		setDead(); // TODO
	}
	
	public PrimaryMode getMode() {
		PrimaryMode[] vals = PrimaryMode.values();
		int idx = dataWatcher.getWatchableObjectByte(MODE_INDEX)%vals.length;
		return vals[idx];
	}
	
	public void setMode(PrimaryMode mode) {
		dataWatcher.updateObject(MODE_INDEX, (byte)mode.ordinal());
	}

	@SuppressWarnings("unused")
	private boolean harvest(EntityPlayerMP player, int x, int y, int z) {
		BlockEvent.BreakEvent event = ForgeHooks.onBlockBreakEvent(worldObj, player.theItemInWorldManager.getGameType(), player, x, y, z);
		if (event.isCanceled()) {
			return false;
		} else {
			Block block = worldObj.getBlock(x, y, z);
			int meta = worldObj.getBlockMetadata(x, y, z);
			if (block.getBlockHardness(worldObj, x, y, z) < 0)
				return false;
			worldObj.playAuxSFX(2001, x, y, z, Block.getIdFromBlock(block) + (worldObj.getBlockMetadata(x, y, z) << 12));
			block.onBlockHarvested(worldObj, x, y, z, meta, player);
			boolean success = block.removedByPlayer(worldObj, player, x, y, z, true);

			if (success) {
				block.onBlockDestroyedByPlayer(worldObj, x, y, z, meta);
				block.harvestBlock(worldObj, player, x, y, z, meta);
				block.dropXpOnBlockBreak(worldObj, x, y, z, event.getExpToDrop() != 0 ? event.getExpToDrop() : block.getExpDrop(worldObj, meta, 0));
			}
			return success;
		}
	}

}

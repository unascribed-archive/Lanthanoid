package com.unascribed.lanthanoid.item.eldritch.armor;

import java.util.List;
import com.unascribed.lanthanoid.Lanthanoid;
import com.unascribed.lanthanoid.init.LAchievements;
import com.unascribed.lanthanoid.network.BootNoise;
import com.unascribed.lanthanoid.network.BootZap;
import cpw.mods.fml.common.network.NetworkRegistry.TargetPoint;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EntityDamageSource;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.MovingObjectPosition.MovingObjectType;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;

public class ItemEldritchBoots extends ItemEldritchArmor {

	public static final DamageSource wall = new DamageSource("wall");
	public static final float SPINUP_SPEED = 800f;
	
	public ItemEldritchBoots(ArmorMaterial material) {
		super(material, 3, false);
	}
	
	@Override
	public void onArmorTick(World world, EntityPlayer player, ItemStack stack) {
		super.onArmorTick(world, player, stack);
		
		int sprintTicks = getSprintingTicks(stack);
		if (sprintTicks < 0) {
			player.setSprinting(false);
			float speed = (float)Math.log(1+((sprintTicks*-1)/SPINUP_SPEED));
			if (!world.isRemote) {
				Lanthanoid.inst.network.sendToAllAround(new BootZap.Message(player.posX-(player.width/2), player.boundingBox.minY, player.posZ-(player.width/2), -0.5f), new TargetPoint(
						world.provider.dimensionId,
						player.posX, player.boundingBox.minY, player.posZ,
						64));
				stack.damageItem(1, player);
			}
			player.moveFlying(0, 1, speed*50);
		}
		if (!world.isRemote) {
			if (sprintTicks < 0) {
				setSprintingTicks(stack, sprintTicks+1);
			} else if (player.isSprinting() && player.onGround && getMilliglyphs(stack) > 0) {
				setSprintingTicks(stack, getSprintingTicks(stack)+1);
			} else {
				if (sprintTicks < 0) {
					setSprintingTicks(stack, sprintTicks + 1);
				} else if (sprintTicks > 0) {
					float speed = (float)Math.log(1+(sprintTicks/SPINUP_SPEED));
					if (getMilliglyphs(stack) > 0) {
						MovingObjectPosition mop = collisionRayTrace(player, 0.5);
						if (mop != null && mop.typeOfHit == MovingObjectType.BLOCK) {
							if ((speed*40) > 1) {
								player.attackEntityFrom(wall, speed*40);
							}
						}
					}
					setSprintingTicks(stack, (int) -(speed*40));
				}
			}
		}
		
		if (sprintTicks == 1 && !world.isRemote) {
			Lanthanoid.inst.network.sendToDimension(new BootNoise.Message(player.getEntityId()), world.provider.dimensionId);
		}
		if (sprintTicks > 0 && (player.onGround || player.capabilities.isFlying) && !player.isInsideOfMaterial(Material.water)) {
			float speed = (float)Math.log(1+(sprintTicks/SPINUP_SPEED));
			int glyphCost = (int)(speed * 500);
			if (getMilliglyphs(stack) < glyphCost) {
				setMilliglyphs(stack, 0);
			} else {
				setMilliglyphs(stack, getMilliglyphs(stack)-glyphCost);
				if (world instanceof WorldServer) {
					Vec3 right = player.getLookVec();
					right.rotateAroundY(90);
					((WorldServer) world).func_147487_a("enchantmenttable", player.posX-(right.xCoord*0.2), player.boundingBox.minY+0.2, player.posZ-(right.xCoord*0.2), Math.min(10, glyphCost/10), 0.1, 0, 0.1, 0.2);
					((WorldServer) world).func_147487_a("enchantmenttable", player.posX+(right.xCoord*0.2), player.boundingBox.minY+0.2, player.posZ+(right.xCoord*0.2), Math.min(10, glyphCost/10), 0.1, 0, 0.1, 0.2);
					if (glyphCost > 100) {
						((WorldServer) world).func_147487_a("enchantmenttable", player.posX, player.posY, player.posZ, 20, player.width/2, player.height/2, player.width/2, 1);
					}
				}
				float damage = (speed*40);
				if (damage > 6) {
					for (Entity ent : (List<Entity>)world.getEntitiesWithinAABB(Entity.class, player.boundingBox.expand(1, 1, 1))) {
						if (ent instanceof EntityLivingBase) {
							EntityLivingBase elb = (EntityLivingBase)ent;
							if (elb == player) continue;
							elb.attackEntityFrom(new EntityDamageSource("collision", player), damage);
						}
					}
				}
				player.moveFlying(0, 1, speed);
				if (speed >= 1.1f) {
					player.triggerAchievement(LAchievements.goFast);
				}
				if (!world.isRemote && world.rand.nextInt((int) Math.max(2, 50-(speed*50))) == 0) {
					if (speed > 0.5f) {
						Vec3 look = player.getLookVec();
						Vec3 right = Vec3.createVectorHelper(look.xCoord, look.yCoord, look.zCoord);
						right.rotateAroundY(90);
						for (int i = 0; i < 4; i++) {
							Lanthanoid.inst.network.sendToAllAround(new BootZap.Message(player.posX-(player.width/2)+(look.xCoord*(speed*4))+(right.xCoord*player.worldObj.rand.nextGaussian()), player.boundingBox.minY+(player.worldObj.rand.nextFloat()*player.height), player.posZ-(player.width/2)+(look.zCoord*(speed*4))+(right.zCoord*player.worldObj.rand.nextGaussian()), Math.min(1, speed)), new TargetPoint(
									world.provider.dimensionId,
									player.posX, player.boundingBox.minY, player.posZ,
									64));
						}
					}
					Lanthanoid.inst.network.sendToAllAround(new BootZap.Message(player.posX-(player.width/2), player.boundingBox.minY, player.posZ-(player.width/2), Math.min(1, speed)), new TargetPoint(
							world.provider.dimensionId,
							player.posX, player.boundingBox.minY, player.posZ,
							64));
					stack.damageItem(1, player);
				}
			}
		}
	}
	
	public int getSprintingTicks(ItemStack stack) {
		return stack.hasTagCompound() ? stack.getTagCompound().getInteger("sprintTicks") : 0;
	}
	
	public void setSprintingTicks(ItemStack stack, int ticks) {
		if (!stack.hasTagCompound()) {
			stack.setTagCompound(new NBTTagCompound());
		}
		stack.getTagCompound().setInteger("sprintTicks", ticks);
	}
	
	public static MovingObjectPosition collisionRayTrace(EntityPlayer entity, double distance) {
		MovingObjectPosition a = _rayTrace(entity, distance, 0);
		if (a == null || a.typeOfHit != MovingObjectPosition.MovingObjectType.BLOCK) {
			MovingObjectPosition b = _rayTrace(entity, distance, -1);
			return b;
		}
		return a;
	}

	private static MovingObjectPosition _rayTrace(EntityPlayer entity, double distance, double offset) {
		Vec3 vec3 = Vec3.createVectorHelper(entity.posX, entity.posY + entity.getEyeHeight() + offset, entity.posZ);
		Vec3 vec31 = entity.getLookVec();
		vec31.yCoord = 0;
		Vec3 vec32 = vec3.addVector(vec31.xCoord * distance, vec31.yCoord * distance, vec31.zCoord * distance);
		return entity.worldObj.rayTraceBlocks(vec3, vec32, false, true, false);
	}

}

package com.unascribed.lanthanoid.item.eldritch.armor;

import java.util.List;
import com.unascribed.lanthanoid.Lanthanoid;
import com.unascribed.lanthanoid.network.BootNoise;
import com.unascribed.lanthanoid.network.BootZap;
import com.unascribed.lanthanoid.util.LUtil;

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

	private static final DamageSource wall = new DamageSource("wall");
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
		}
		if (!world.isRemote) {
			if (player.isSprinting() && getMilliglyphs(stack) > 0) {
				setSprintingTicks(stack, getSprintingTicks(stack)+1);
			} else {
				if (sprintTicks < 0) {
					setSprintingTicks(stack, sprintTicks + 1);
				} else if (sprintTicks > 0) {
					if (getMilliglyphs(stack) <= 0) {
						setSprintingTicks(stack, -4);
					} else {
						MovingObjectPosition mop = LUtil.rayTrace(player, 0.5, false, true, true);
						float speed = (float)Math.log(1+(sprintTicks/SPINUP_SPEED));
						if (mop != null && mop.typeOfHit == MovingObjectType.BLOCK) {
							if ((speed*40) > 1) {
								player.attackEntityFrom(wall, speed*40);
							}
						} else {
							Vec3 look = player.getLookVec();
							player.addVelocity(look.xCoord*speed, (look.yCoord+0.2)*speed, look.zCoord*speed);
						}
					}
					setSprintingTicks(stack, 0);
				}
			}
		}
		
		if (sprintTicks == 1 && !world.isRemote) {
			Lanthanoid.inst.network.sendToDimension(new BootNoise.Message(player.getEntityId()), world.provider.dimensionId);
		}
		if (sprintTicks > 0 && (player.onGround || player.capabilities.isFlying) && !player.isInsideOfMaterial(Material.water)) {
			float speed = (float)Math.log(1+(sprintTicks/SPINUP_SPEED));
			int glyphCost = (int)(speed * 1000);
			if (getMilliglyphs(stack) < glyphCost) {
				setMilliglyphs(stack, 0);
			} else {
				setMilliglyphs(stack, getMilliglyphs(stack)-glyphCost);
				if (world instanceof WorldServer) {
					((WorldServer) world).func_147487_a("enchantmenttable", player.posX, player.posY, player.posZ, glyphCost/100, player.width, player.height, player.width, 0);
				}
				float damage = (speed*40);
				if (damage > 1) {
					for (Entity ent : (List<Entity>)world.getEntitiesWithinAABB(Entity.class, player.boundingBox.expand(1, 1, 1))) {
						if (ent instanceof EntityLivingBase) {
							EntityLivingBase elb = (EntityLivingBase)ent;
							if (elb == player) continue;
							elb.attackEntityFrom(new EntityDamageSource("collision", player), damage);
						}
					}
				}
				player.moveFlying(0, 1, speed);
				if (!world.isRemote && world.rand.nextInt((int) Math.max(2, 50-(speed*50))) == 0) {
					Lanthanoid.inst.network.sendToAllAround(new BootZap.Message(player.posX, player.boundingBox.minY, player.posZ, Math.min(1, speed)), new TargetPoint(
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

}

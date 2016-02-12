package com.unascribed.lanthanoid.item.eldritch.armor;

import com.unascribed.lanthanoid.init.LItems;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingAttackEvent;

public class ItemEldritchHelmet extends ItemEldritchArmor {

	public ItemEldritchHelmet(ArmorMaterial material, boolean enhanced) {
		super(material, 0, enhanced);
		MinecraftForge.EVENT_BUS.register(this);
	}
	
	@SubscribeEvent
	public void onAttacked(LivingAttackEvent e) {
		if (e.entityLiving instanceof EntityPlayer) {
			EntityPlayer player = (EntityPlayer)e.entityLiving;
			ItemStack helm = player.inventory.armorItemInSlot(3);
			if (helm != null && helm.getItem() == this) {
				float damage = e.ammount;
				damage = player.applyArmorCalculations(e.source, damage);
				damage = player.applyPotionDamageCalculations(e.source, damage);
				if (damage >= player.getHealth() && getMilliglyphs(helm) >= 125_000) {
					helm.damageItem(265, player);
					int milliglyphs = getMilliglyphs(helm);
					setMilliglyphs(helm, 0);
					player.addPotionEffect(new PotionEffect(Potion.absorption.id, 30*20, milliglyphs/125_000));
					if (player.worldObj instanceof WorldServer) {
						((WorldServer)player.worldObj).func_147487_a("enchantmenttable", player.posX, player.boundingBox.maxY, player.posZ, milliglyphs/10000, 0.2, 0.2, 0.2, 0);
					}
				}
			}
		}
	}
	
	@Override
	public void onArmorTick(World world, EntityPlayer player, ItemStack stack) {
		super.onArmorTick(world, player, stack);
		if (player.ticksExisted % 60 == 0 && player.getHealth() < player.getMaxHealth()) {
			int cost = Math.min((int)((player.getMaxHealth()-player.getHealth())*4000), Math.min(getMilliglyphs(stack), 4000));
			if (cost > 0) {
				float heal = cost/4000f;
				player.heal(heal);
				setMilliglyphs(stack, getMilliglyphs(stack)-cost);
				if (world instanceof WorldServer) {
					((WorldServer)world).func_147487_a("enchantmenttable", player.posX, player.boundingBox.maxY, player.posZ, cost/400, 0.2, 0.2, 0.2, 0);
				}
			}
		}
		ItemStack boots = player.inventory.armorItemInSlot(0);
		if (boots != null && boots.getItem() == LItems.eldritch_boots) {
			int sprintTicks = LItems.eldritch_boots.getSprintingTicks(boots);
			if (sprintTicks > 40 && sprintTicks % 40 == 0 && player.getFoodStats().needFood()) {
				for (int i = 0; i < player.inventory.getSizeInventory(); i++) {
					ItemStack is = player.inventory.getStackInSlot(i);
					if (is == null) continue;
					if (is.getItem() instanceof ItemFood) {
						if (player.worldObj.isRemote) {
							for (int i1 = 0; i1 < 5; ++i1) {
								Vec3 vec3 = Vec3.createVectorHelper((player.worldObj.rand.nextFloat() - 0.5D) * 0.1D, Math.random() * 0.1D + 0.1D, 0.0D);
								vec3.rotateAroundX(-player.rotationPitch * (float) Math.PI / 180.0F);
								vec3.rotateAroundY(-player.rotationYaw * (float) Math.PI / 180.0F);
								Vec3 vec31 = Vec3.createVectorHelper((player.worldObj.rand.nextFloat() - 0.5D) * 0.3D, (-player.worldObj.rand.nextFloat()) * 0.6D - 0.3D, 0.6D);
								vec31.rotateAroundX(-player.rotationPitch * (float) Math.PI / 180.0F);
								vec31.rotateAroundY(-player.rotationYaw * (float) Math.PI / 180.0F);
								vec31 = vec31.addVector(player.posX, player.posY + player.getEyeHeight(), player.posZ);
								player.worldObj.spawnParticle("iconcrack_" + Item.getIdFromItem(is.getItem()), vec31.xCoord, vec31.yCoord, vec31.zCoord, vec3.xCoord, vec3.yCoord + 0.05D, vec3.zCoord);
							}
						}
						is = is.getItem().onItemUseFinish(is, player.worldObj, player);
						if (is == null || is.stackSize <= 0) {
							player.inventory.setInventorySlotContents(i, null);
						} else {
							player.inventory.setInventorySlotContents(i, is);
						}
						break;
					}
				}
			}
		}
	}
	
	@Override
	public void onUpdate(ItemStack stack, World world, Entity entity, int slot, boolean equipped) {
		super.onUpdate(stack, world, entity, slot, equipped);
		
	}

}

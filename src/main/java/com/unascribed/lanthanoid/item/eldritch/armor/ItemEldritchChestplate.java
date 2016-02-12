package com.unascribed.lanthanoid.item.eldritch.armor;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingHurtEvent;

public class ItemEldritchChestplate extends ItemEldritchArmor {

	public ItemEldritchChestplate(ArmorMaterial material) {
		super(material, 1, false);
		MinecraftForge.EVENT_BUS.register(this);
	}
	
	@SubscribeEvent
	public void onHurt(LivingHurtEvent e) {
		if (e.entityLiving instanceof EntityPlayer) {
			EntityPlayer player = (EntityPlayer)e.entityLiving;
			ItemStack plate = player.inventory.armorItemInSlot(2);
			if (plate != null && plate.getItem() == this) {
				float min = e.ammount*0.2083333333333333f;
				float d = e.ammount-min;
				int cost = Math.min(getMilliglyphs(plate),(int)(d*1000));
				if (player.worldObj instanceof WorldServer) {
					((WorldServer)player.worldObj).func_147487_a("enchantmenttable", player.posX, player.posY, player.posZ, cost/1000, player.width/2, player.height/2, player.width/2, 0);
				}
				setMilliglyphs(plate, getMilliglyphs(plate)-cost);
				e.ammount -= (cost/1000f);
			}
		}
	}
	
	@Override
	public void onArmorTick(World world, EntityPlayer player, ItemStack stack) {
		super.onArmorTick(world, player, stack);
		
	}
	
	@Override
	public void onUpdate(ItemStack stack, World world, Entity entity, int slot, boolean equipped) {
		super.onUpdate(stack, world, entity, slot, equipped);
		
	}

}

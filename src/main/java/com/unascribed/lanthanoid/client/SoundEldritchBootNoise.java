package com.unascribed.lanthanoid.client;

import com.unascribed.lanthanoid.init.LItems;
import com.unascribed.lanthanoid.item.eldritch.armor.ItemEldritchBoots;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

@SideOnly(Side.CLIENT)
public class SoundEldritchBootNoise extends MovingSoundEntity {

	private EntityLivingBase owner;
	private int ticksExisted;
	
	public SoundEldritchBootNoise(ResourceLocation loc, EntityLivingBase owner) {
		super(loc, owner, 1);
		this.owner = owner;
		this.xPosF = (float)owner.posX;
		this.yPosF = (float)owner.posY;
		this.zPosF = (float)owner.posZ;
		this.pitch = 1;
		this.volume = 0.05f;
		this.repeat = true;
	}

	@Override
	public void update() {
		super.update();
		ItemStack boots = owner.getEquipmentInSlot(1);
		if (boots == null || boots.getItem() != LItems.eldritch_boots || !owner.isSprinting()) {
			stop();
			return;
		}
		
		int sprintTicks = ticksExisted++;
		
		float speed = (float)Math.log(1+(sprintTicks/ItemEldritchBoots.SPINUP_SPEED));
		this.volume = Math.max(0.05f, Math.min(1, speed));
		this.pitch = 0.5f+speed;
	}
	
}

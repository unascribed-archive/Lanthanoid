package com.unascribed.lanthanoid.compat;

import java.util.List;

import com.unascribed.backlytra.Backlytra;
import com.unascribed.backlytra.ElytraSound;
import com.unascribed.backlytra.FieldImitations;
import com.unascribed.backlytra.MethodImitations;
import com.unascribed.lanthanoid.LanthanoidProperties;
import com.unascribed.lanthanoid.effect.EntityGlyphFX;
import com.unascribed.lanthanoid.init.LItems;
import com.unascribed.lanthanoid.item.eldritch.armor.ItemEldritchElytra;
import com.unascribed.lanthanoid.network.SetFlyingState.State;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemArmor.ArmorMaterial;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Vec3;
import net.minecraftforge.common.util.EnumHelper;

public class BacklytraCompat {

	public static void init() {
		ArmorMaterial eldritchElytra = EnumHelper.addArmorMaterial("ELDRITCH_ELYTRA", Backlytra.durability*3, new int[]{0, 2, 0, 0}, 12);
		
		LItems.eldritch_elytra = new ItemEldritchElytra(eldritchElytra);
		GameRegistry.registerItem(LItems.eldritch_elytra, "eldritch_elytra");
	}
	
	public static void tick(EntityPlayer p) {
		ItemStack itemstack = p.getEquipmentInSlot(3);
		int soundMode = FieldImitations.get(p, "lanthanoidElytraSoundMode", 0);
		if (MethodImitations.isElytraFlying(p) && itemstack != null && itemstack.getItem() instanceof ItemEldritchElytra) {
			ItemEldritchElytra iee = (ItemEldritchElytra)itemstack.getItem();
			int desiredMode;
			if (iee.getMilliglyphs(itemstack) > 300) {
				Vec3 look = p.getLookVec();
				LanthanoidProperties props = (LanthanoidProperties) p.getExtendedProperties("lanthanoid");
				if (p.isSneaking()) {
					p.motionX *= 0.99;
					if (p.motionY > 0) {
						p.motionY *= 0.99;
					}
					p.motionZ *= 0.99;
					desiredMode = 1;
					iee.setMilliglyphs(itemstack, iee.getMilliglyphs(itemstack)-300);
				} else if (props.flyingState == State.ELYTRA_BOOST) {
					p.motionX += look.xCoord*0.025;
					p.motionY += look.yCoord*0.025;
					p.motionZ += look.yCoord*0.025;
					emit(p, 0);
					desiredMode = 1;
					iee.setMilliglyphs(itemstack, iee.getMilliglyphs(itemstack)-300);
				} else {
					desiredMode = 2;
				}
			} else {
				desiredMode = 2;
			}
			if (p.worldObj.isRemote && soundMode != desiredMode) {
				startSound(p, desiredMode == 1);
			}
			soundMode = desiredMode;
			float speed = MathHelper.sqrt_float((float) (p.motionX * p.motionX + p.motionY * p.motionY + p.motionZ * p.motionZ));
			float dmg = speed*3;
			if (dmg > 0.75f) {
				for (Entity e : (List<Entity>)p.worldObj.getEntitiesWithinAABBExcludingEntity(p, p.boundingBox.expand(2, 2, 2))) {
					if (e instanceof EntityLivingBase) {
						((EntityLivingBase)e).attackEntityFrom(DamageSource.causePlayerDamage(p), dmg);
					}
				}
			}
		} else {
			soundMode = 0;
		}
		FieldImitations.set(p, "lanthanoidElytraSoundMode", soundMode);
	}
	
	private static void emit(EntityPlayer player, double height) {
		if (player.worldObj.isRemote && FMLCommonHandler.instance().getSide() == Side.CLIENT) {
			Vec3 right = player.getLookVec();
			right.rotateAroundY(90);
			right.yCoord = 0;
			_emit(player, right, 0.2, height);
			_emit(player, right, -0.2, height);
		}
	}

	@SideOnly(Side.CLIENT)
	private static void _emit(EntityPlayer player, Vec3 mod, double d, double height) {
		double sX = player.posX;
		double sY = player.boundingBox.minY;
		double sZ = player.posZ;
		sX += (mod.xCoord*d);
		sY += (mod.yCoord*d);
		sZ += (mod.zCoord*d);
		for (int i = 0; i < 4; i++) {
			double eX = sX+(player.worldObj.rand.nextGaussian()*(player.width/2));
			double eY = sY-height;
			double eZ = sZ+(player.worldObj.rand.nextGaussian()*(player.width/2));
			EntityGlyphFX fx = new EntityGlyphFX(player.worldObj, eX, eY, eZ, sX-eX, sY-eY, sZ-eZ);
			Minecraft.getMinecraft().effectRenderer.addEffect(fx);
		}
	}
	
	@SideOnly(Side.CLIENT)
	private static ElytraSound sound;
	
	@SideOnly(Side.CLIENT)
	private static void startSound(EntityPlayer p, boolean jet) {
		if (!(p instanceof EntityPlayerSP)) return;
		if (sound != null) {
			Minecraft.getMinecraft().getSoundHandler().stopSound(sound);
			sound = null;
		}
		if (jet) {
			Minecraft.getMinecraft().getSoundHandler().playSound(sound = new ElytraSound((EntityPlayerSP)p, new ResourceLocation("lanthanoid", "eldritch_elytra")));
			sound.setForcedVolume(1f);
		} else {
			Minecraft.getMinecraft().getSoundHandler().playSound(sound = new ElytraSound((EntityPlayerSP)p, new ResourceLocation("backlytra", "item.elytra.flying")));
		}
	}

}
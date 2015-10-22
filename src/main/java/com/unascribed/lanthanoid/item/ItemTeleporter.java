package com.unascribed.lanthanoid.item;

import java.util.List;

import com.unascribed.lanthanoid.LAchievements;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EntityDamageSource;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;

public class ItemTeleporter extends ItemMulti {
	public static String[] flavors = {
			"Yttrium",
			"Praseodymium",
			"Holmium",
			"Ytterbium",
			"Neodymium",
			"Erbium",
			"Gadolinium",
			"Dysprosium"
	};
	public ItemTeleporter() {
		super(prefix(flavors));
	}
	private static String[] prefix(String[] arr) {
		String[] rtrn = new String[arr.length];
		for (int i = 0; i < arr.length; i++) {
			rtrn[i] = "teleporter"+arr[i];
		}
		return rtrn;
	}
	@Override
	public int getItemStackLimit(ItemStack stack) {
		return 1;
	}
	@Override
	public boolean showDurabilityBar(ItemStack stack) {
		return getCompound(stack).getInteger("teleportCooldown") > 0;
	}
	@Override
	public double getDurabilityForDisplay(ItemStack stack) {
		return getCompound(stack).getInteger("teleportCooldown")/(40D+(stack.getItemDamage()*10));
	}
	@Override
	public boolean getShareTag() {
		return true;
	}
	@Override
	public void onUpdate(ItemStack p_77663_1_, World p_77663_2_, Entity p_77663_3_, int p_77663_4_, boolean p_77663_5_) {
		int cooldown = getCompound(p_77663_1_).getInteger("teleportCooldown");
		if (cooldown > 0) {
			cooldown--;
			getCompound(p_77663_1_).setInteger("teleportCooldown", cooldown);
		} else if (cooldown == 0) {
			p_77663_3_.playSound("lanthanoid:teleport_ready", 1.0f, 1.0f);
			getCompound(p_77663_1_).setInteger("teleportCooldown", -1);
		}
		if (!p_77663_2_.isRemote && p_77663_3_ instanceof EntityPlayer) {
			EntityPlayer p = ((EntityPlayer)p_77663_3_);
			p.triggerAchievement(LAchievements.craftTeleporter);
		}
	}
	@Override
	public void addInformation(ItemStack stack, EntityPlayer player, List li, boolean advanced) {
		int mod = stack.getItemDamage()*2;
		int coolMod = stack.getItemDamage()*10;
		li.add("\u00A77Teleport Distance: "+(6+mod)+" blocks");
		li.add("\u00A77Cooldown: "+((40+coolMod)/20)+(coolMod%20 == 0 ? "" : ".5")+" seconds");
	}
	@Override
	public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player) {
		if (getCompound(stack).getInteger("teleportCooldown") > 0) return stack;
		if (world instanceof WorldServer) {
			((WorldServer)world).func_147487_a("reddust", player.posX, player.posY, player.posZ, 200, player.width, player.height, player.width, 10f);
		}
		if (!world.isRemote) {
			player.playSound("lanthanoid:teleport_in", 1.0f, 1.0f);
		}
		if (player.ridingEntity != null) {
			Entity riding = player.ridingEntity;
			riding.riddenByEntity = null;
			player.ridingEntity = null;
			if (riding instanceof EntityLivingBase) {
				player.triggerAchievement(LAchievements.telefragMount);
				player.worldObj.playSoundAtEntity(player, "lanthanoid:telefrag", 1.0f, 2.0f);
				riding.attackEntityFrom(new EntityDamageSource("telefrag", player), 500000);
			}
		}
		Vec3 pos = Vec3.createVectorHelper(player.posX, player.posY+player.getEyeHeight(), player.posZ);
		Vec3 look = player.getLookVec();
		int mod = stack.getItemDamage()*2;
		double dist = 6+mod;
		Vec3 target = pos.addVector(look.xCoord*dist, look.yCoord*dist, look.zCoord*dist);
		if (world instanceof WorldServer) {
			double x = pos.xCoord;
			double y = pos.yCoord;
			double z = pos.zCoord;
			double iter = 4;
			for (int i = 0; i < dist*iter; i++) {
				x += look.xCoord/iter;
				y += look.yCoord/iter;
				z += look.zCoord/iter;
				((WorldServer)world).func_147487_a("happyVillager", x, y, z, 1, 0, 0, 0, 0);
			}
		}
		int x = (int)target.xCoord;
		int y = (int)target.yCoord;
		int z = (int)target.zCoord;
		for (int i = 0; i < 3; i++) {
			player.setPosition(x+(0.5*Math.signum(x)), y, z+(0.5*Math.signum(z)));
			if (!player.isEntityInsideOpaqueBlock()) {
				break;
			} else {
				y++;
			}
		}
		player.setPosition(x+(0.5*Math.signum(x)), y, z+(0.5*Math.signum(z)));
		if (!world.isRemote) {
			boolean first = true;
			for (EntityLivingBase elb : (List<EntityLivingBase>)world.getEntitiesWithinAABB(EntityLivingBase.class, player.boundingBox)) {
				if (elb == player) continue;
				elb.attackEntityFrom(new EntityDamageSource("telefrag", player), 500000);
				if (first) {
					player.worldObj.playSoundAtEntity(player, "lanthanoid:telefrag", 1.0f, 2.0f);
					player.triggerAchievement(LAchievements.telefrag);
					if (stack.getItemDamage() == 7) {
						player.triggerAchievement(LAchievements.telesnipe);
					}
					first = false;
				}
			}
			if (player.isEntityInsideOpaqueBlock()) {
				player.attackEntityFrom(new DamageSource("self_telefrag"), 500000);
				player.triggerAchievement(LAchievements.selfTelefrag);
			}
		}
		if (world.isRemote) {
			player.playSound("lanthanoid:teleport_in", 1.0f, 1.0f);
		}
		player.playSound("lanthanoid:teleport_out", 1.0f, 1.0f);
		if (world instanceof WorldServer) {
			((WorldServer)world).func_147487_a("reddust", player.posX, player.posY, player.posZ, 200, player.width, player.height, player.width, 10f);
		}
		getCompound(stack).setInteger("teleportCooldown", (40+(stack.getItemDamage()*10)));
		return stack;
	}
	private NBTTagCompound getCompound(ItemStack stack) {
		if (!stack.hasTagCompound()) {
			stack.setTagCompound(new NBTTagCompound());
		}
		return stack.getTagCompound();
	}
}

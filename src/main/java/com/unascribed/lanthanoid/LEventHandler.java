package com.unascribed.lanthanoid;

import java.util.List;

import com.unascribed.lanthanoid.client.LClientEventHandler;
import com.unascribed.lanthanoid.init.LAchievements;
import com.unascribed.lanthanoid.init.LItems;
import com.unascribed.lanthanoid.item.eldritch.armor.ItemEldritchArmor;
import com.unascribed.lanthanoid.item.rifle.Variant;
import com.unascribed.lanthanoid.network.BeamParticle;
import com.unascribed.lanthanoid.network.SetFlyingState;
import com.unascribed.lanthanoid.network.SetFlyingState.State;
import com.unascribed.lanthanoid.network.SetScopeFactor;
import com.unascribed.lanthanoid.waypoint.Waypoint;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent;
import cpw.mods.fml.common.gameevent.TickEvent.Phase;
import cpw.mods.fml.common.gameevent.TickEvent.PlayerTickEvent;
import cpw.mods.fml.common.gameevent.TickEvent.ServerTickEvent;
import cpw.mods.fml.common.network.NetworkRegistry.TargetPoint;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityFallingBlock;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.Vec3;
import net.minecraftforge.event.entity.EntityEvent;
import net.minecraftforge.event.entity.player.PlayerDestroyItemEvent;

public class LEventHandler {
	
	@SubscribeEvent
	public void onConstruct(EntityEvent.EntityConstructing e) {
		e.entity.registerExtendedProperties("lanthanoid", new LanthanoidProperties());
	}
	
	private int waypointTicks = 0;
	
	@SubscribeEvent
	public void onDestroy(PlayerDestroyItemEvent e) {
		if (e.original.getItem() == LItems.erbium_wrecking_ball) {
			e.entityPlayer.inventory.addItemStackToInventory(LItems.stick.getStackForName("stickHolmium"));
		}
	}
	
	@SubscribeEvent
	public void onServerTick(ServerTickEvent e) {
		if (e.phase == Phase.START) {
			if (waypointTicks++ % 5 == 0) {
				Lanthanoid.inst.waypointManager.sendUpdates();
			}
		}
	}
	
	@SubscribeEvent
	public void onPlayerLoggedIn(PlayerLoggedInEvent e) {
		if (!(e.player instanceof EntityPlayerMP)) {
			return;
		}
		Lanthanoid.inst.waypointManager.sendAll((EntityPlayerMP)e.player, false);
	}
	
	@SubscribeEvent
	public void onPlayerTick(PlayerTickEvent e) {
		LanthanoidProperties props = (LanthanoidProperties)e.player.getExtendedProperties("lanthanoid");
		for (Waypoint w : Lanthanoid.inst.waypointManager.allWaypoints(e.player.worldObj)) {
			double distSq = e.player.getDistanceSq(w.x+0.5, w.y+0.5, w.z+0.5);
			if (distSq < w.nameDistance*w.nameDistance) {
				if (!w.owner.equals(e.player.getGameProfile().getId())) {
					e.player.triggerAchievement(LAchievements.usedWaypoint);
					if (FMLCommonHandler.instance().getEffectiveSide().isServer()) {
						for (EntityPlayer p : (List<EntityPlayer>)MinecraftServer.getServer().getConfigurationManager().playerEntityList) {
							if (p.getGameProfile().getId().equals(w.owner)) {
								p.triggerAchievement(LAchievements.useWaypoint);
							}
						}
					}
				}
			}
		}
		if (props.scopeFactor > 1) {
			ItemStack held = e.player.getHeldItem();
			if (held == null || held.getItem() != LItems.rifle || LItems.rifle.getVariant(held) != Variant.ZOOM) {
				props.scopeFactor = 1;
				if (!e.player.worldObj.isRemote && e.player instanceof EntityPlayerMP) {
					Lanthanoid.inst.network.sendTo(new SetScopeFactor.Message(props.scopeFactor), (EntityPlayerMP)e.player);
				}
			}
		} else if (props.scopeFactor == 0) {
			ItemStack held = e.player.getHeldItem();
			if (held == null || held.getItem() != LItems.rifle || LItems.rifle.getVariant(held) != Variant.NONE) {
				props.scopeFactor = 1;
				if (!e.player.worldObj.isRemote && e.player instanceof EntityPlayerMP) {
					Lanthanoid.inst.network.sendTo(new SetScopeFactor.Message(props.scopeFactor), (EntityPlayerMP)e.player);
				}
			}
		}
		if (ItemEldritchArmor.hasSetBonus(e.player)) {
			SetFlyingState.State flyingState;
			if (e.player.worldObj.isRemote) {
				flyingState = LClientEventHandler.inst.lastFlyingState;
			} else {
				flyingState = props.flyingState;
			}
			int totalGlyphs = 0;
			for (ItemStack is : e.player.inventory.armorInventory) {
				totalGlyphs += ((ItemEldritchArmor)is.getItem()).getMilliglyphs(is);
			}
			if (totalGlyphs >= 1000) {
				int cost = 0;
				if (flyingState == State.FLYING) {
					cost = 250;
					if (e.player instanceof EntityPlayerMP) {
						((EntityPlayerMP)e.player).playerNetServerHandler.floatingTickCount = 0;
					}
					e.player.motionY += 0.05;
					if (e.player.motionY > 0.4) {
						e.player.motionY = 0.4;
					}
					if (e.player.motionY >= 0) {
						e.player.fallDistance = 0;
					}
					if (e.player.moveForward > 0) {
						e.player.moveFlying(0, 1, 0.0075f);
					}
					for (int i = 0; i < 5; i++) {
						e.player.worldObj.spawnParticle("enchantmenttable", e.player.posX+(e.player.worldObj.rand.nextGaussian()*(e.player.width/2)), e.player.boundingBox.minY, e.player.posZ+(e.player.worldObj.rand.nextGaussian()*(e.player.width/2)), 0, -0.5, 0);
					}
				} else if (flyingState == State.HOVER) {
					cost = 100;
					if (e.player instanceof EntityPlayerMP) {
						((EntityPlayerMP)e.player).playerNetServerHandler.floatingTickCount = 0;
					}
					if (e.player.motionY > 0) {
						e.player.motionY -= 0.05;
					} else if (e.player.motionY < 0) {
						e.player.motionY += 0.05;
					}
					if (Math.abs(e.player.motionY) < 0.1) {
						e.player.motionY = 0;
						e.player.fallDistance = 0;
					}
					if (e.player.moveForward > 0) {
						e.player.moveFlying(0, 1, 0.005f);
					}
					for (int i = 0; i < 8; i++) {
						e.player.worldObj.spawnParticle("enchantmenttable", e.player.posX+(e.player.worldObj.rand.nextGaussian()*(e.player.width/2)), e.player.boundingBox.minY, e.player.posZ+(e.player.worldObj.rand.nextGaussian()*(e.player.width/2)), 0, 0, 0);
					}
				}
				int loops = 0;
				while (cost > 0) {
					for (ItemStack is : e.player.inventory.armorInventory) {
						int mg = ((ItemEldritchArmor)is.getItem()).getMilliglyphs(is);
						int c = Math.min((int)Math.ceil(cost/4f), mg);
						cost -= c;
						((ItemEldritchArmor)is.getItem()).setMilliglyphs(is, mg-c);
					}
					loops++;
					if (loops > 20) {
						Lanthanoid.log.warn("Tried to decrement cost too many times!");
						break;
					}
				}
				if (!e.player.onGround && e.player.isSneaking()) {
					if (e.player.motionY < -0.2) {
						e.player.motionY += 0.05;
						if (e.player.motionY >= -0.2) {
							e.player.fallDistance = 0;
						}
						for (int i = 0; i < 2; i++) {
							e.player.worldObj.spawnParticle("enchantmenttable", e.player.posX+(e.player.worldObj.rand.nextGaussian()*(e.player.width/2)), e.player.boundingBox.minY, e.player.posZ+(e.player.worldObj.rand.nextGaussian()*(e.player.width/2)), 0, 0.5, 0);
						}
					}
				}
			}
		}
		if (props.grabbedEntity != null) {
			if (props.grabbedEntity.isDead) {
				props.grabbedEntity = null;
			}/* else if (held == null || held.getItem() != LItems.rifle || LItems.rifle.getMode(held) != ItemRifle.PrimaryMode.TRACTOR) {
				props.grabbedEntity = null;
			}*/ else {
				Entity ent = props.grabbedEntity;
				if (ent instanceof EntityFallingBlock) {
					((EntityFallingBlock)ent).fallTime = 2;
				}
				ent.fallDistance = 0;
				Vec3 entPos = Vec3.createVectorHelper(ent.posX, ent.posY, ent.posZ);
				Vec3 look = e.player.getLookVec();
				Vec3 target = Vec3.createVectorHelper(e.player.posX+(look.xCoord*2), e.player.posY+(look.yCoord*2), e.player.posZ+(look.zCoord*2));
				Vec3 dir = entPos.subtract(target);
				ent.setVelocity(dir.xCoord, dir.yCoord+0.2, dir.zCoord);
				if (e.player.worldObj.isRemote && e.player instanceof EntityPlayerMP) {
					EntityPlayerMP player = (EntityPlayerMP)e.player;
					Vec3 start = Vec3.createVectorHelper(player.posX, player.boundingBox.maxY-0.2f, player.posZ);
					if (((LanthanoidProperties)player.getExtendedProperties("lanthanoid")).scopeFactor > 1) {
						start.yCoord -= 0.25;
					} else {
						Vec3 right = player.getLookVec();
						right.rotateAroundY(-90f);
						float rightAdj = 0.25f;
						start = start.addVector(right.xCoord*rightAdj, right.yCoord*rightAdj, right.zCoord*rightAdj);
					}
					Lanthanoid.inst.network.sendToAllAround(new BeamParticle.Message(false, false, start.xCoord, start.yCoord, start.zCoord,
							ent.posX, ent.posY, ent.posZ, -1/*ItemRifle.PrimaryMode.TRACTOR.color*/),
							new TargetPoint(
								player.worldObj.provider.dimensionId,
								start.xCoord,
								start.yCoord,
								start.zCoord,
								150
							));
				}
			}
		}
	}
	
}

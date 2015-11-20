package com.unascribed.lanthanoid;

import java.util.List;
import com.unascribed.lanthanoid.client.LClientEventHandler;
import com.unascribed.lanthanoid.init.LAchievements;
import com.unascribed.lanthanoid.init.LItems;
import com.unascribed.lanthanoid.item.rifle.Variant;
import com.unascribed.lanthanoid.network.BeamParticleMessage;
import com.unascribed.lanthanoid.network.SetScopeFactorMessage;
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
		Waypoint nearest = null;
		double minDist = Double.MAX_VALUE;
		for (Waypoint w : Lanthanoid.inst.waypointManager.allWaypoints(e.player.worldObj)) {
			double distSq = e.player.getDistanceSq(w.x+0.5, w.y+0.5, w.z+0.5);
			if (distSq < w.nameDistance*w.nameDistance) {
				if (FMLCommonHandler.instance().getEffectiveSide().isClient()) {
					if (distSq < minDist) {
						nearest = w;
						minDist = distSq;
					}
				}
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
		if (FMLCommonHandler.instance().getEffectiveSide().isClient()) {
			LClientEventHandler.inst.onNearWaypoint(nearest);
		}
		if (props.scopeFactor > 1) {
			ItemStack held = e.player.getHeldItem();
			if (held == null || held.getItem() != LItems.rifle || LItems.rifle.getVariant(held) != Variant.ZOOM) {
				props.scopeFactor = 1;
				if (!e.player.worldObj.isRemote && e.player instanceof EntityPlayerMP) {
					Lanthanoid.inst.network.sendTo(new SetScopeFactorMessage(props.scopeFactor), (EntityPlayerMP)e.player);
				}
			}
		} else if (props.scopeFactor == 0) {
			ItemStack held = e.player.getHeldItem();
			if (held == null || held.getItem() != LItems.rifle || LItems.rifle.getVariant(held) != Variant.NONE) {
				props.scopeFactor = 1;
				if (!e.player.worldObj.isRemote && e.player instanceof EntityPlayerMP) {
					Lanthanoid.inst.network.sendTo(new SetScopeFactorMessage(props.scopeFactor), (EntityPlayerMP)e.player);
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
					((EntityFallingBlock)ent).field_145812_b = 2;
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
					Lanthanoid.inst.network.sendToAllAround(new BeamParticleMessage(false, false, start.xCoord, start.yCoord, start.zCoord,
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

package com.unascribed.lanthanoid.network;

import java.util.List;
import java.util.Map;

import com.unascribed.lanthanoid.Lanthanoid;
import com.unascribed.lanthanoid.Vec3i;
import com.unascribed.lanthanoid.waypoint.Waypoint;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ModifyWaypointListHandler implements IMessageHandler<ModifyWaypointListMessage, IMessage> {
	@Override
	@SideOnly(Side.CLIENT)
	public IMessage onMessage(ModifyWaypointListMessage message, MessageContext ctx) {
		if (message.mode == ModifyWaypointListMessage.Mode.PATCH) {
			for (Map.Entry<Integer, List<Vec3i>> en : message.remove.entrySet()) {
				for (Vec3i v : en.getValue()) {
					Lanthanoid.inst.waypointManager.removeWaypoint(en.getKey(), v.x, v.y, v.z);
				}
			}
			
			for (Map.Entry<Integer, List<Waypoint>> en : message.add.entrySet()) {
				for (Waypoint w : en.getValue()) {
					Lanthanoid.inst.waypointManager.setWaypoint(en.getKey(), w.x, w.y, w.z, w);
				}
			}
		} else if (message.mode == ModifyWaypointListMessage.Mode.RESET) {
			Lanthanoid.inst.waypointManager.clear();
		}
		return null;
	}
}

package com.unascribed.lanthanoid.network;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.unascribed.lanthanoid.Lanthanoid;
import com.unascribed.lanthanoid.Vec3i;
import com.unascribed.lanthanoid.waypoint.Waypoint;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import io.netty.buffer.ByteBuf;

public final class ModifyWaypointList {

	public enum Mode {
		PATCH,
		RESET,
		PUT
	}
	
	public static class Handler implements IMessageHandler<Message, IMessage> {
		@Override
		@SideOnly(Side.CLIENT)
		public IMessage onMessage(Message message, MessageContext ctx) {
			if (message.mode == Mode.PUT) {
				Lanthanoid.inst.waypointManager.clear();
			}
			if (message.mode == Mode.PATCH || message.mode == Mode.PUT) {
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
			} else if (message.mode == Mode.RESET) {
				Lanthanoid.inst.waypointManager.clear();
			}
			return null;
		}
	}
	
	public static class Message implements IMessage {

		public Mode mode = Mode.PATCH;
		public Map<Integer, List<Waypoint>> add = Collections.emptyMap();
		public Map<Integer, List<Vec3i>> remove = Collections.emptyMap();
		
		@Override
		public void fromBytes(ByteBuf buf) {
			int modeI = buf.readUnsignedByte();
			Mode[] vals = Mode.values();
			mode = vals[modeI%vals.length];
			
			remove = Collections.emptyMap();
			add = Collections.emptyMap();
			
			if (mode == Mode.PATCH || mode == Mode.PUT) {
				int addSize = buf.readUnsignedShort();
				if (addSize > 0) {
					Map<Integer, List<Waypoint>> map = Maps.newHashMap();
					List<Integer> dims = Lists.newArrayList();
					for (int i = 0; i < addSize; i++) {
						dims.add((int)buf.readByte());
					}
					for (int dim : dims) {
						List<Waypoint> li = Lists.newArrayList();
						int size = buf.readUnsignedShort();
						for (int i = 0; i < size; i++) {
							Waypoint way = new Waypoint();
							way.fromBytes(buf);
							li.add(way);
						}
						map.put(dim, li);
					}
					add = map;
				}
				if (mode == Mode.PATCH) {
					int removeSize = buf.readUnsignedShort();
					if (removeSize > 0) {
						Map<Integer, List<Vec3i>> map = Maps.newHashMap();
						List<Integer> dims = Lists.newArrayList();
						for (int i = 0; i < removeSize; i++) {
							dims.add((int)buf.readByte());
						}
						for (int dim : dims) {
							List<Vec3i> li = Lists.newArrayList();
							int size = buf.readUnsignedShort();
							for (int i = 0; i < size; i++) {
								li.add(new Vec3i(buf.readInt(), buf.readUnsignedByte(), buf.readInt()));
							}
							map.put(dim, li);
						}
						remove = map;
					}
				}
			}
		}

		@Override
		public void toBytes(ByteBuf buf) {
			buf.writeByte(mode.ordinal());
			
			buf.writeShort(add.size());
			for (Integer dim : add.keySet()) {
				buf.writeByte(dim);
			}
			for (List<Waypoint> w : add.values()) {
				buf.writeShort(w.size());
				for (Waypoint wa : w) {
					wa.toBytes(buf);
				}
			}
			
			buf.writeShort(remove.size());
			for (Integer dim : remove.keySet()) {
				buf.writeByte(dim);
			}
			for (List<Vec3i> v : remove.values()) {
				buf.writeShort(v.size());
				for (Vec3i ve : v) {
					buf.writeInt(ve.x);
					buf.writeByte(ve.y);
					buf.writeInt(ve.z);
				}
			}
		}

	}

	
	private ModifyWaypointList() {}
}

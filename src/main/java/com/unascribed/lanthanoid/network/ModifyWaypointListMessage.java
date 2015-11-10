package com.unascribed.lanthanoid.network;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.unascribed.lanthanoid.Vec3i;
import com.unascribed.lanthanoid.waypoint.Waypoint;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import io.netty.buffer.ByteBuf;

public class ModifyWaypointListMessage implements IMessage {

	public enum Mode {
		PATCH,
		RESET
	}
	
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
		
		if (mode == Mode.PATCH) {
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

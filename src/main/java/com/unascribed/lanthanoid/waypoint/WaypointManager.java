package com.unascribed.lanthanoid.waypoint;

import java.util.AbstractMap;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Nonnull;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.unascribed.lanthanoid.Lanthanoid;
import com.unascribed.lanthanoid.Vec3i;
import com.unascribed.lanthanoid.network.ModifyWaypointListMessage;
import com.unascribed.lanthanoid.network.ModifyWaypointListMessage.Mode;
import com.unascribed.lanthanoid.util.Functional;
import com.unascribed.lanthanoid.waypoint.Waypoint.Type;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.relauncher.Side;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants.NBT;

public class WaypointManager {
	private Map<Integer, Map<Vec3i, Waypoint>> waypoints = Maps.newConcurrentMap();
	
	private Map<Integer, List<Waypoint>> additions = Maps.newConcurrentMap();
	private Map<Integer, List<Vec3i>> removals = Maps.newConcurrentMap();
	
	private List<Map.Entry<Integer, Vec3i>> pendingRemovals = Lists.newArrayList();
	
	private ThreadLocal<Vec3i> goatLocal = Functional.newThreadLocal(Vec3i::new);
	
	private WaypointSavedData wsd;
	
	public void sendUpdates() {
		for (Map.Entry<Integer, Vec3i> en : pendingRemovals) {
			removeWaypoint(en.getKey(), en.getValue().x, en.getValue().y, en.getValue().z);
		}
		pendingRemovals.clear();
		if (FMLCommonHandler.instance().getSide() == Side.CLIENT) {
			removals.clear();
			additions.clear();
			return;
		}
		if (additions.isEmpty() && removals.isEmpty()) return;
		ModifyWaypointListMessage mwlm = new ModifyWaypointListMessage();
		mwlm.add = Maps.newHashMap(additions);
		mwlm.remove = Maps.newHashMap(removals);
		Lanthanoid.inst.network.sendToAll(mwlm);
		additions.clear();
		removals.clear();
	}
	
	
	
	public void sendAll(EntityPlayerMP player, boolean patch) {
		ModifyWaypointListMessage msg = new ModifyWaypointListMessage();
		msg.remove = Maps.newHashMap();
		msg.add = Maps.newHashMap();
		msg.mode = patch ? Mode.PATCH : Mode.PUT;
		for (Map.Entry<Integer, Map<Vec3i, Waypoint>> dimEn : waypoints.entrySet()) {
			List<Waypoint> li = Lists.newArrayList();
			for (Waypoint w : dimEn.getValue().values()) {
				if (w.type == Type.PERSONAL && !player.getGameProfile().getId().equals(w.owner)) continue;
				li.add(w);
			}
			msg.add.put(dimEn.getKey(), li);
		}
		Lanthanoid.inst.network.sendTo(msg, player);
	}
	
	
	
	public void writeToNBT(NBTTagCompound nbt) {
		for (Map.Entry<Integer, Map<Vec3i, Waypoint>> dimEn : waypoints.entrySet()) {
			NBTTagList li = new NBTTagList();
			for (Waypoint w : dimEn.getValue().values()) {
				NBTTagCompound nw = new NBTTagCompound();
				w.writeToNBT(nw);
				li.appendTag(nw);
			}
			nbt.setTag("DIM"+dimEn.getKey(), li);
		}
	}
	
	
	
	public void readFromNBT(NBTTagCompound nbt) {
		clear();
		for (String key : (Set<String>)nbt.func_150296_c()) {
			if (key.startsWith("DIM")) {
				int dimId = Integer.parseInt(key.substring(3));
				NBTTagList li = nbt.getTagList(key, NBT.TAG_COMPOUND);
				for (int i = 0; i < li.tagCount(); i++) {
					Waypoint w = new Waypoint();
					w.setId();
					w.readFromNBT(li.getCompoundTagAt(i));
					setWaypoint(dimId, w.x, w.y, w.z, w);
				}
			}
		}
	}
	
	
	
	public void clear() {
		if (FMLCommonHandler.instance().getSide().isServer()) {
			ModifyWaypointListMessage mwlm = new ModifyWaypointListMessage();
			mwlm.mode = ModifyWaypointListMessage.Mode.RESET;
			Lanthanoid.inst.network.sendToAll(mwlm);
		}
		additions.clear();
		removals.clear();
		pendingRemovals.clear();
		waypoints.clear();
		if (wsd != null) {
			wsd.markDirty();
		}
	}
	
	
	
	public Collection<Waypoint> allWaypoints(World world) {
		return allWaypoints(world.provider.dimensionId);
	}
	
	public Collection<Waypoint> allWaypoints(int dimId) {
		return getDimMap(waypoints, dimId).values();
	}
	
	
	
	public Waypoint getWaypoint(World world, int x, int y, int z) {
		return getWaypoint(world.provider.dimensionId, x, y, z);
	}
	
	public Waypoint getWaypoint(int dimId, int x, int y, int z) {
		return getDimMap(waypoints, dimId).get(goat(x, y, z));
	}
	
	
	
	public boolean hasWaypoint(World world, int x, int y, int z) {
		return hasWaypoint(world.provider.dimensionId, x, y, z);
	}
	
	public boolean hasWaypoint(int dimId, int x, int y, int z) {
		return getDimMap(waypoints, dimId).containsKey(goat(x, y, z));
	}
	
	
	
	public void setWaypoint(World world, int x, int y, int z, @Nonnull Waypoint waypoint) {
		setWaypoint(world.provider.dimensionId, x, y, z, waypoint);
	}
	
	public void setWaypoint(int dimId, int x, int y, int z, @Nonnull Waypoint waypoint) {
		Preconditions.checkNotNull(waypoint);
		
		getDimList(additions, dimId).add(waypoint);
		Waypoint old = getWaypoint(dimId, x, y, z);
		if (old != null && old != waypoint) {
			getDimList(removals, dimId).add(new Vec3i(x, y, z));
		}
		waypoint.x = x;
		waypoint.y = y;
		waypoint.z = z;
		getDimMap(waypoints, dimId).put(goat(x, y, z), waypoint);
		if (wsd != null) {
			wsd.markDirty();
		}
	}
	
	
	
	public void removeWaypointLater(World world, int x, int y, int z) {
		removeWaypointLater(world.provider.dimensionId, x, y, z);
	}
	
	public void removeWaypointLater(int dim, int x, int y, int z) {
		Vec3i vec = new Vec3i(x, y, z);
		getDimList(removals, dim).add(vec);
		pendingRemovals.add(new AbstractMap.SimpleEntry<>(dim, vec));
	}
	
	
	
	public void removeWaypoint(World world, int x, int y, int z) {
		removeWaypoint(world.provider.dimensionId, x, y, z);
	}
	
	public void removeWaypoint(int dim, int x, int y, int z) {
		Map<Vec3i, Waypoint> dimMap = getDimMap(waypoints, dim);
		Vec3i goat = goat(x, y, z);
		dimMap.remove(goat);
		getDimList(removals, dim).add(goat.clone());
		if (wsd != null) {
			wsd.markDirty();
		}
	}
	
	
	private <T> List<T> getDimList(Map<Integer, List<T>> src, int dimId) {
		if (!src.containsKey(dimId)) {
			src.put(dimId, Lists.newArrayList());
		}
		return src.get(dimId);
	}
	
	private <K, V> Map<K, V> getDimMap(Map<Integer, Map<K, V>> src, int dimId) {
		if (!src.containsKey(dimId)) {
			src.put(dimId, Maps.newHashMap());
		}
		return src.get(dimId);
	}


	private Vec3i goat(int x, int y, int z) {
		Vec3i goat = goatLocal.get();
		goat.x = x;
		goat.y = y;
		goat.z = z;
		return goat;
	}



	public void setSaveManager(WaypointSavedData wsd) {
		this.wsd = wsd;
	}


}

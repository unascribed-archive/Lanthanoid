package com.unascribed.lanthanoid.waypoint;

import com.unascribed.lanthanoid.Lanthanoid;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.WorldSavedData;

public class WaypointSavedData extends WorldSavedData {

	public WaypointSavedData(String name) {
		super(name);
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt) {
		Lanthanoid.inst.waypointManager.readFromNBT(nbt);
	}

	@Override
	public void writeToNBT(NBTTagCompound nbt) {
		Lanthanoid.inst.waypointManager.writeToNBT(nbt);
	}

}

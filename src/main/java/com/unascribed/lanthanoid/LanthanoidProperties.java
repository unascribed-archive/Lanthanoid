package com.unascribed.lanthanoid;

import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraftforge.common.IExtendedEntityProperties;

public class LanthanoidProperties implements IExtendedEntityProperties {
	
	public int scopeFactor = 1;
	public Entity grabbedEntity;

	@Override
	public void saveNBTData(NBTTagCompound compound) {

	}

	@Override
	public void loadNBTData(NBTTagCompound compound) {

	}

	@Override
	public void init(Entity entity, World world) {

	}

}

package com.unascribed.lanthanoid;

import java.util.Map;

import com.google.common.collect.Maps;

import net.minecraft.client.audio.MovingSound;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;

public class MovingSoundEntity extends MovingSound {
	private static final Map<Entity, MovingSoundEntity> sounds = Maps.newHashMap();
	
	private Entity entity;
	public MovingSoundEntity(ResourceLocation loc, Entity entity) {
		super(loc);
		this.entity = entity;
		if (sounds.containsKey(entity)) {
			sounds.get(entity).stop();
		}
		sounds.put(entity, this);
	}

	@Override
	public void update() {
		if (donePlaying) {
			sounds.remove(this);
		}
		if (entity.isDead) stop();
		this.xPosF = (float)entity.posX;
		this.yPosF = (float)entity.posY;
		this.zPosF = (float)entity.posZ;
	}
	
	public void stop() {
		this.donePlaying = true;
		sounds.remove(this);
	}
	
	public static MovingSoundEntity get(Entity ent) {
		MovingSoundEntity s = sounds.get(ent);
		if (s.donePlaying) {
			sounds.remove(s);
		}
		return s;
	}

}

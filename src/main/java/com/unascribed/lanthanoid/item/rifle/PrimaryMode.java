package com.unascribed.lanthanoid.item.rifle;

import java.util.Collections;
import java.util.List;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;

import net.minecraft.block.Block;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public final class PrimaryMode extends Mode<PrimaryMode> {
	private static final List<PrimaryMode> values = Lists.newArrayList();
	
	public static final PrimaryMode EXPLODE = new PrimaryMode(0, "EXPLODE", Items.gunpowder, 0x747474).setNoBuffer();
	public static final PrimaryMode DAMAGE = new PrimaryMode(1, "DAMAGE", "dustYtterbium", 0xFFEC00).setDoesDamage();
	public static final PrimaryMode HEALING = new PrimaryMode(2, "HEALING", "dustErbium", 0x2C61FF).setDoesHeal();
	public static final PrimaryMode MINE = new PrimaryMode(3, "MINE", "dustHolmium", 0xFFF4D6);
	public static final PrimaryMode GROW = new PrimaryMode(4, "GROW", "dustCerium", 0xFF004C).setDoesPoof();
	public static final PrimaryMode SHRINK = new PrimaryMode(5, "SHRINK", "dustDysprosium", 0xE400FF).setDoesPoof();
	//public static final PrimaryMode KNOCKBACK = new PrimaryMode(6, "KNOCKBACK", "dustYttrium", 0xA9F8FF);
	//public static final PrimaryMode REPLICATE = new PrimaryMode(7, "REPLICATE", "dustActinolite", 0x83FFCF).setDoesPoof();
	//public static final PrimaryMode WORMHOLE = new PrimaryMode(8, "WORMHOLE", "dustDiaspore", 0x8762FF);
	public static final PrimaryMode LIGHT = new PrimaryMode(6, "LIGHT", "dustThulite", 0xFF7768);
	
	public static final ImmutableSet<Integer> usedOreIDs;
	
	public static PrimaryMode[] values() {
		return values.toArray(new PrimaryMode[values.size()]);
	}
	
	@Override
	protected void addValue() {
		values.add(this);
	}
	
	static {
		ImmutableSet.Builder<Integer> builder = ImmutableSet.builder();
		for (PrimaryMode pm : values()) {
			if (pm.type instanceof Integer) {
				builder.add((Integer)pm.type);
			}
		}
		usedOreIDs = builder.build();
		Collections.sort(values, (a, b) -> Integer.compare(a.ordinal(), b.ordinal()));
	}
	
	
	private PrimaryMode(int ordinal, String name, Block type, int color) {
		super(ordinal, name, type, color);
	}
	private PrimaryMode(int ordinal, String name, Item type, int color) {
		super(ordinal, name, type, color);
	}
	private PrimaryMode(int ordinal, String name, ItemStack type, int color) {
		super(ordinal, name, type, color);
	}
	private PrimaryMode(int ordinal, String name, String type, int color) {
		super(ordinal, name, type, color);
	}
	
	private boolean doesDamage, doesHeal, doesPoof;
	
	private PrimaryMode setDoesDamage() {
		this.doesDamage = true;
		return this;
	}
	private PrimaryMode setDoesHeal() {
		this.doesHeal = true;
		return this;
	}
	private PrimaryMode setDoesPoof() {
		this.doesPoof = true;
		return this;
	}
	
	public boolean doesDamage() {
		return doesDamage;
	}
	public boolean doesHeal() {
		return doesHeal;
	}
	public boolean doesPoof() {
		return doesPoof;
	}
}
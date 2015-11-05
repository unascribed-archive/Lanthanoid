package com.unascribed.lanthanoid.item.rifle;

import java.util.Collections;
import java.util.List;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;

import net.minecraft.block.Block;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public final class SecondaryMode extends Mode<SecondaryMode> {
	private static final List<SecondaryMode> values = Lists.newArrayList();
	
	public static final SecondaryMode NONE = new SecondaryMode(0, "NONE", 0xFFFFFF);
	public static final SecondaryMode BLAZE = new SecondaryMode(1, "BLAZE", Items.blaze_powder, 0xFFAA00);
	public static final SecondaryMode CHAIN = new SecondaryMode(2, "CHAIN", "dustNeodymium", 0x8D8DFF);
	public static final SecondaryMode BOUNCE = new SecondaryMode(3, "BOUNCE", "dustPraseodymium", 0x98FF8F);
	
	
	public static final ImmutableSet<Integer> usedOreIDs;
	
	public static SecondaryMode[] values() {
		return values.toArray(new SecondaryMode[values.size()]);
	}
	
	@Override
	protected void addValue() {
		values.add(this);
	}
	
	static {
		ImmutableSet.Builder<Integer> builder = ImmutableSet.builder();
		for (SecondaryMode pm : values()) {
			if (pm.type instanceof Integer) {
				builder.add((Integer)pm.type);
			}
		}
		usedOreIDs = builder.build();
		Collections.sort(values, (a, b) -> Integer.compare(a.ordinal(), b.ordinal()));
	}
	
	private SecondaryMode(int ordinal, String name, int color) {
		super(ordinal, name, color);
	}
	private SecondaryMode(int ordinal, String name, Block type, int color) {
		super(ordinal, name, type, color);
	}
	private SecondaryMode(int ordinal, String name, Item type, int color) {
		super(ordinal, name, type, color);
	}
	private SecondaryMode(int ordinal, String name, ItemStack type, int color) {
		super(ordinal, name, type, color);
	}
	private SecondaryMode(int ordinal, String name, String type, int color) {
		super(ordinal, name, type, color);
	}
	
}
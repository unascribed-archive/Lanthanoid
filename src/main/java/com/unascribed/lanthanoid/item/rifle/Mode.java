package com.unascribed.lanthanoid.item.rifle;


import org.apache.commons.lang3.ArrayUtils;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;

public abstract class Mode<T extends Mode> {
	private final int ordinal;
	private final String name;
	public final Object type;
	public final String translationKey;
	public final int color;
	
	protected Mode(int ordinal, String name, int color) {
		this.ordinal = ordinal;
		this.name = name;
		this.translationKey = name.toLowerCase();
		this.type = null;
		this.color = color;
		addValue();
	}
	protected Mode(int ordinal, String name, String type, int color) {
		this.ordinal = ordinal;
		this.name = name;
		this.translationKey = name.toLowerCase();
		this.type = OreDictionary.getOreID(type);
		this.color = color;
		addValue();
	}
	protected Mode(int ordinal, String name, ItemStack type, int color) {
		this.ordinal = ordinal;
		this.name = name;
		this.translationKey = name.toLowerCase();
		this.type = type;
		this.color = color;
		addValue();
	}
	protected Mode(int ordinal, String name, Item type, int color) {
		this(ordinal, name, new ItemStack(type, 1, 0), color);
	}
	protected Mode(int ordinal, String name, Block type, int color) {
		this(ordinal, name, new ItemStack(type, 1, 0), color);
	}
	
	public String name() {
		return name;
	}
	public int ordinal() {
		return ordinal;
	}
	
	private boolean doesBuffer = true;
	
	protected T setNoBuffer() {
		doesBuffer = false;
		return (T) this;
	}
	
	public boolean doesBuffer() {
		return doesBuffer;
	}
	
	protected abstract void addValue();
	public boolean stackMatches(ItemStack stack) {
		if (type == null) {
			return false;
		} else if (type instanceof Integer) {
			return ArrayUtils.contains(OreDictionary.getOreIDs(stack), (Integer)type);
		} else if (type instanceof ItemStack) {
			return stack.isItemEqual((ItemStack)type);
		} else {
			throw new IllegalStateException();
		}
	}
}

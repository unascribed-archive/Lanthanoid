package com.unascribed.lanthanoid.util;

import java.util.Map;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Maps;
import gnu.trove.map.TObjectIntMap;
import gnu.trove.map.hash.TObjectIntHashMap;
import net.minecraft.util.StatCollector;

public class MultiHelper {
	protected String mode;
	protected ImmutableList<String> names;
	protected TObjectIntMap<String> reverseNames;
	// precalculate the processed strings for speed
	protected Map<String, String> firstWords = Maps.newHashMap();
	protected Map<String, String> materials = Maps.newHashMap();
	
	public MultiHelper(String mode, String... names) {
		this.mode = mode;
		this.names = ImmutableList.copyOf(names);
		reverseNames = new TObjectIntHashMap<>(names.length);
		for (int i = 0; i < names.length; i++) {
			reverseNames.put(names[i], i);
		}
		for (String s : names) {
			firstWords.put(s, firstWord(s));
			materials.put(s, allExceptFirstWord(s).toLowerCase());
		}
	}
	
	public ImmutableList<String> getNames() {
		return names;
	}
	
	private String allExceptFirstWord(String s) {
		StringBuilder sb = new StringBuilder();
		boolean ignore = true;
		for (int i = 0; i < s.length(); i++) {
			int c = s.codePointAt(i);
			if (Character.isUpperCase(c)) {
				ignore = false;
			}
			if (!ignore) {
				sb.appendCodePoint(c);
			}
		}
		return sb.toString();
	}
	
	private String firstWord(String s) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < s.length(); i++) {
			int c = s.codePointAt(i);
			if (Character.isUpperCase(c)) {
				break;
			}
			sb.appendCodePoint(c);
		}
		return sb.toString();
	}

	public boolean hasName(String name) {
		return reverseNames.containsKey(name);
	}
	
	public int getMetaForName(String name) {
		return reverseNames.get(name);
	}
	
	public String getNameForMeta(int meta) {
		return meta < 0 || meta >= names.size() ? null : names.get(meta);
	}
	
	public String getDisplayNameForMeta(int meta) {
		String name = getNameForMeta(meta);
		if (name == null) {
			return StatCollector.translateToLocal(mode+".error");
		}
		return StatCollector.translateToLocalFormatted(getUnlocalizedNameForMeta(meta)+".template", StatCollector.translateToLocal("material."+materials.get(name)));
	}
	
	public String getUnlocalizedNameForMeta(int meta) {
		String name = getNameForMeta(meta);
		if (name == null) {
			return StatCollector.translateToLocal(mode+".error");
		}
		return mode+"."+firstWords.get(name);
	}
	
}

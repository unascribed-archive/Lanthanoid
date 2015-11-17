package com.unascribed.lanthanoid.init;

import java.util.List;
import java.util.Map;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.unascribed.lanthanoid.Lanthanoid;
import com.unascribed.lanthanoid.client.TextureCompositorImpl.BlockBackdrop;
import com.unascribed.lanthanoid.client.TextureCompositorImpl.BlockType;
import com.unascribed.lanthanoid.client.TextureCompositorImpl.ItemType;
import com.unascribed.lanthanoid.util.TextureCompositor;

public class LMaterials {

	public static Map<String, Integer> colors = Maps.newHashMap();
	
	public static List<String> metalsPlusVanilla = Lists.newArrayList("Gold", "Iron");
	public static List<String> metals = Lists.newArrayList();
	public static List<String> gems = Lists.newArrayList();
	public static List<String> others = Lists.newArrayList();
	public static List<String> gemsAndMetal = Lists.newArrayList();
	public static List<String> gemsAndMetalPlusVanilla = Lists.newArrayList("Gold", "Iron", "Diamond", "Emerald");
	public static List<String> hasOre = Lists.newArrayList("Coal", "Lapis", "Redstone", "Quartz", "Gold", "Iron", "Diamond", "Emerald");
	
	public static void init() {
		initMetals();
		initGems();
		initNewMaterials();
		
		others.add("Gypsum");
		
		
		int goldColor = 0xFDD753;
		int ironColor = 0xEEEEEE;
		int diamondColor = 0x00FFFF;
		int emeraldColor = 0x00FF00;
		
		colors.put("Gold", goldColor);
		colors.put("Iron", ironColor);
		colors.put("Diamond", diamondColor);
		colors.put("Emerald", emeraldColor);
		
		TextureCompositor compositor = Lanthanoid.inst.compositor;
		
		if (compositor != null) {
			compositor.addItem("nuggetIron", ironColor, ItemType.NUGGET);

			compositor.addItem("stickIron", ironColor, ItemType.STICK);
			compositor.addItem("stickGold", goldColor, ItemType.STICK);
			
			compositor.addItem("railIron", ironColor, ItemType.RAIL);
			compositor.addItem("railGold", goldColor, ItemType.RAIL);
			
			compositor.addItem("dustIron", ironColor, ItemType.DUST);
			compositor.addItem("dustGold", goldColor, ItemType.DUST);
			compositor.addItem("dustDiamond", diamondColor, ItemType.DUST);
			compositor.addItem("dustEmerald", emeraldColor, ItemType.DUST);
			
			
			
			compositor.addBlock("oreGypsum", 0xCBCBCB, BlockType.CRYSTAL);
			
			compositor.addBlock("platingIron", ironColor, BlockType.PLATING);
			compositor.addBlock("platingGold", goldColor, BlockType.PLATING);
			
			compositor.addBlock("weakplatingIron", ironColor, BlockType.WEAK_PLATING);
			compositor.addBlock("weakplatingGold", goldColor, BlockType.WEAK_PLATING);
		}
		
		metalsPlusVanilla.addAll(metals);
		gemsAndMetalPlusVanilla.addAll(gemsAndMetal);
	}

	private static void initNewMaterials() {
		// ************************************************************************************************** //
		// ** NEW ORES, GEM OR METAL, MUST GO HERE, AFTER ALL EXISTING DEFINITIONS, OR METADATA WILL BREAK ** //
		// ************************************************************************************************** //
		addIngots("YttriumBariumCopperOxide", 0x111111);
	}

	private static void initGems() {
		// Gems
		addAll("Actinolite", 0x40AD83, BlockType.GEM_ORE, BlockBackdrop.STONE, ItemType.SQUARE_GEM);
		addAll("Diaspore", 0x674BC3, BlockType.GEM_ORE, BlockBackdrop.STONE, ItemType.ROUND_GEM);
		
		addAll("Thulite", 0xCA5E52, BlockType.GEM_ORE, BlockBackdrop.STONE, ItemType.HEX_GEM);
		
		addAll("Rosasite", 0x00A6C3, BlockType.LUMPY_ORE, BlockBackdrop.NONE, ItemType.ORB);
		
		addAll("Raspite", 0xC67226, BlockType.GEM_ORE, BlockBackdrop.STONE, ItemType.TRIANGLE_GEM);
	}

	private static void initMetals() {
		// Basic metals
		addAll("Copper", 0x944A09, BlockType.METAL_ORE, BlockBackdrop.STONE, ItemType.INGOT);
		addAll("Yttrium", 0x496B6E, BlockType.METAL_ORE, BlockBackdrop.STONE, ItemType.INGOT);
		addAll("Barium", 0x39190A, BlockType.METAL_ORE, BlockBackdrop.STONE, ItemType.INGOT);
		
		// Hell lanthanides
		addAll("Ytterbium", 0x423D00, BlockType.METAL_ORE, BlockBackdrop.NETHERRACK, ItemType.INGOT);
		addAll("Neodymium", 0x363662, BlockType.METAL_ORE, BlockBackdrop.NETHERRACK, ItemType.INGOT);
		addAll("Praseodymium", 0x2B4929, BlockType.METAL_ORE, BlockBackdrop.NETHERRACK, ItemType.INGOT);
		
		// Rarer lanthanides
		addAll("Holmium", 0xA8A18D, BlockType.METAL_ORE, BlockBackdrop.STONE, ItemType.INGOT);
		
		// End lanthanides
		addAll("Erbium", 0x1A3996, BlockType.TRACE_ORE, BlockBackdrop.END_STONE, ItemType.INGOT);
		addAll("Gadolinium", 0x157952, BlockType.TRACE_ORE, BlockBackdrop.END_STONE, ItemType.INGOT);
		
		// Shipwreck lanthanides
		addAll("Dysprosium", 0x860096, BlockType.TRACE_ORE, BlockBackdrop.STONE, ItemType.INGOT);
		addAll("Cerium", 0xD0003E, BlockType.TRACE_ORE, BlockBackdrop.STONE, ItemType.INGOT);
		addAll("Lutetium", 0xBEE22F, BlockType.TRACE_ORE, BlockBackdrop.STONE, ItemType.INGOT);
	}

	private static void addIngots(String name, int color) {
		TextureCompositor compositor = Lanthanoid.inst.compositor;
		colors.put(name, color);
		if (compositor != null) {
			compositor.addItem("ingot"+name, color, ItemType.INGOT);
			compositor.addItem("stick"+name, color, ItemType.STICK);
			compositor.addItem("nugget"+name, color, ItemType.NUGGET);
			compositor.addBlock("block"+name, color, BlockType.METAL_BLOCK);
			compositor.addBlock("plating"+name, color, BlockType.PLATING);
			compositor.addBlock("weakplating"+name, color, BlockType.WEAK_PLATING);
			compositor.addItem("dust"+name, color, ItemType.DUST);
		}
		metals.add(name);
		gemsAndMetal.add(name);
	}
	
	private static void addAll(String name, int color, BlockType blockType, BlockBackdrop backdrop, ItemType itemType) {
		TextureCompositor compositor = Lanthanoid.inst.compositor;
		colors.put(name, color);
		if (compositor != null) {
			compositor.addBlock("ore"+name, color, blockType, backdrop);
			if (itemType == ItemType.INGOT) {
				compositor.addItem("ingot"+name, color, itemType);
				compositor.addItem("stick"+name, color, ItemType.STICK);
				compositor.addItem("nugget"+name, color, ItemType.NUGGET);
			} else {
				compositor.addItem("gem"+name, color, itemType);
			}
		}
		if (blockType == BlockType.METAL_ORE || blockType == BlockType.TRACE_ORE) {
			if (compositor != null) {
				compositor.addBlock("block"+name, color, BlockType.METAL_BLOCK);
				compositor.addBlock("plating"+name, color, BlockType.PLATING);
				compositor.addBlock("weakplating"+name, color, BlockType.WEAK_PLATING);
			}
			metals.add(name);
			gemsAndMetal.add(name);
		} else if (blockType == BlockType.GEM_ORE || blockType == BlockType.GEM_SQUARE_ORE || blockType == BlockType.LUMPY_ORE) {
			if (compositor != null) {
				compositor.addBlock("block"+name, color, blockType == BlockType.LUMPY_ORE ? BlockType.CRYSTAL : BlockType.GEM_BLOCK);
			}
			gems.add(name);
			gemsAndMetal.add(name);
		} else {
			if (compositor != null) {
				compositor.addBlock("block"+name, color, blockType);
			}
			others.add(name);
		}
		if (compositor != null) {
			compositor.addItem("dust"+name, color, ItemType.DUST);
		}
		hasOre.add(name);
	}

}

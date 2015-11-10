package com.unascribed.lanthanoid.init;

import com.unascribed.lanthanoid.item.ItemTeleporter;
import com.unascribed.lanthanoid.item.rifle.Variant;

import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.ShapedOreRecipe;
import net.minecraftforge.oredict.ShapelessOreRecipe;

public class LRecipes {

	public static void init() {
		initStorage();
		initPlating();
		
		initRifle();
		initTeleporter();
		
		initMachines();
		
		initStopgap();
		
		GameRegistry.addRecipe(new ShapedOreRecipe(LBlocks.misc.getStackForName("lampThulite"), 
				"tt",
				"tt",
				't', "dustThulite"));
		
		GameRegistry.addRecipe(new ShapedOreRecipe(LItems.wrecking_ball,
				", ,",
				"d,B",
				"h  ",
				
				',', "nuggetDysprosium",
				'd', "stickDysprosium",
				'h', "stickHolmium",
				'B', "blockDysprosium"));
	}
	
	private static void initMachines() {
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(LBlocks.machine, 1, 0), 
				"hph",
				"hgh",
				"hhh",
				'h', "ingotHolmium",
				'g', "gemRaspite",
				'p', Items.ender_pearl));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(LBlocks.machine, 1, 1), 
				"hph",
				"hgh",
				"hhh",
				'h', "ingotHolmium",
				'g', "gemDiamond",
				'p', Items.ender_pearl));
	}

	private static void initStopgap() {
		GameRegistry.addRecipe(new ShapedOreRecipe(LItems.ingot.getStackForName("ingotDysprosium", 2), 
				"dbd",
				"bEb",
				"dbd",
				'E', "lanthanoidPrivate-blockEndMetal",
				'b', "ingotBarium",
				'd', "dyePurple"));
		GameRegistry.addRecipe(new ShapedOreRecipe(LItems.ingot.getStackForName("ingotCerium", 2), 
				"dbd",
				"bGb",
				"dbd",
				'G', "lanthanoidPrivate-blockEndMetal",
				'b', "ingotBarium",
				'd', "dyeRed"));
		GameRegistry.addRecipe(new ShapedOreRecipe(LItems.ingot.getStackForName("ingotLutetium", 2), 
				"dbd",
				"bGb",
				"dbd",
				'G', "lanthanoidPrivate-blockEndMetal",
				'b', "ingotBarium",
				'd', "dyeYellow"));
		GameRegistry.addRecipe(new ShapedOreRecipe(LBlocks.energized_lutetium, 
				"rrr",
				"rlr",
				"rrr",
				'r', "dustRedstone",
				'l', "ingotLutetium"));
	}

	public static void initTeleporter() {
		int i = 0;
		for (String s : ItemTeleporter.flavors) {
			GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(LItems.teleporter, 1, i++), 
					" o ",
					"lel",
					"lLl",
					'o', "nugget"+s,
					'l', "ingot"+s,
					'L', "lanthanoidPrivate-blockEndMetal",
					'e', Items.ender_pearl));
		}
	}

	public static void initStorage() {
		for (String s : LMaterials.metals) {
			GameRegistry.addSmelting(new ItemStack(LBlocks.ore_metal, 1, LBlocks.ore_metal.getMetaForName("ore"+s)),
					LItems.ingot.getStackForName("ingot"+s), 1.0f);
			GameRegistry.addRecipe(new ShapelessOreRecipe(LItems.nugget.getStackForName("nugget"+s, 9),
					"ingot"+s));
			GameRegistry.addRecipe(new ShapelessOreRecipe(LItems.ingot.getStackForName("ingot"+s),
					"nugget"+s, "nugget"+s, "nugget"+s,
					"nugget"+s, "nugget"+s, "nugget"+s,
					"nugget"+s, "nugget"+s, "nugget"+s));
			
			GameRegistry.addRecipe(new ShapelessOreRecipe(new ItemStack(LBlocks.storage, 1, LBlocks.storage.getMetaForName("block"+s)),
					"ingot"+s, "ingot"+s, "ingot"+s,
					"ingot"+s, "ingot"+s, "ingot"+s,
					"ingot"+s, "ingot"+s, "ingot"+s));
			GameRegistry.addRecipe(new ShapelessOreRecipe(LItems.ingot.getStackForName("ingot"+s, 9),
					"block"+s));
		}
		for (String s : LMaterials.gems) {	
			GameRegistry.addRecipe(new ShapelessOreRecipe(new ItemStack(LBlocks.storage, 1, LBlocks.storage.getMetaForName("block"+s)),
					"gem"+s, "gem"+s, "gem"+s,
					"gem"+s, "gem"+s, "gem"+s,
					"gem"+s, "gem"+s, "gem"+s));
			GameRegistry.addRecipe(new ShapelessOreRecipe(LItems.gem.getStackForName("gem"+s, 9),
					"block"+s));
		}
		GameRegistry.addRecipe(new ShapelessOreRecipe(LItems.nugget.getStackForName("nuggetIron", 9), "ingotIron"));
		GameRegistry.addRecipe(new ShapelessOreRecipe(Items.iron_ingot, 
				"nuggetIron", "nuggetIron", "nuggetIron",
				"nuggetIron", "nuggetIron", "nuggetIron",
				"nuggetIron", "nuggetIron", "nuggetIron"));
	}

	public static void initPlating() {
		for (String s : LMaterials.metalsPlusVanilla) {
			GameRegistry.addRecipe(new ShapedOreRecipe(LBlocks.weak_plating.getStackForName("plating"+s, 8),
					"iii",
					"iIi",
					"iii",
					'i', "nugget"+s,
					'I', "cobblestone"));
			GameRegistry.addRecipe(new ShapedOreRecipe(LBlocks.plating.getStackForName("plating"+s, 8),
					"iii",
					"iIi",
					"iii",
					'i', "nugget"+s,
					'I', Blocks.obsidian));
			if (s.equals("Gold")) {
				GameRegistry.addSmelting(LBlocks.plating.getStackForName("plating"+s, 1), new ItemStack(Items.gold_nugget), 0);
				GameRegistry.addSmelting(LBlocks.weak_plating.getStackForName("weakplating"+s, 1), new ItemStack(Items.gold_nugget), 0);
			} else {
				GameRegistry.addSmelting(LBlocks.plating.getStackForName("plating"+s, 1), LItems.nugget.getStackForName("nugget"+s), 0);
				GameRegistry.addSmelting(LBlocks.weak_plating.getStackForName("weakplating"+s, 1), LItems.nugget.getStackForName("nugget"+s), 0);
			}
			GameRegistry.addRecipe(new ShapedOreRecipe(LItems.stick.getStackForName("stick"+s, 4),
					"i",
					"i",
					'i', "ingot"+s));
		}
	}

	public static void initRifle() {
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(LItems.rifle, 1, Variant.NONE.ordinal()),
				"fz ",
				"zdo",
				" sb",
				'f', "gemActinolite",
				'b', "blockHolmium",
				'o', "gemDiaspore",
				's', "stickHolmium",
				'z', "ingotHolmium",
				'd', "nuggetDysprosium"));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(LItems.rifle, 1, Variant.ZOOM.ordinal()),
				"r ",
				"gq",
				
				'g', LItems.rifle,
				'r', "gemRaspite",
				'q', "gemQuartz"));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(LItems.rifle, 1, Variant.OVERCLOCK.ordinal()),
				"d ",
				"gb",
				
				'g', LItems.rifle,
				'd', "ingotDysprosium",
				'b', "ingotGold"));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(LItems.rifle, 1, Variant.SUPERCLOCKED.ordinal()),
				"d ",
				"gb",
				
				'g', new ItemStack(LItems.rifle, 1, Variant.OVERCLOCK.ordinal()),
				'd', "blockDysprosium",
				'b', "blockGold"));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(LItems.rifle, 1, Variant.EFFICIENCY.ordinal()),
				"c ",
				"gd",
				
				'g', LItems.rifle,
				'c', "ingotCerium",
				'd', "gemDiamond"));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(LItems.rifle, 1, Variant.SUPEREFFICIENCY.ordinal()),
				"c ",
				"gd",
				
				'g', new ItemStack(LItems.rifle, 1, Variant.EFFICIENCY.ordinal()),
				'c', "blockCerium",
				'd', "blockDiamond"));
	}
	
}

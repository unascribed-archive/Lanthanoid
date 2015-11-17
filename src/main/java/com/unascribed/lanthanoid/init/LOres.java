package com.unascribed.lanthanoid.init;

import net.minecraftforge.oredict.OreDictionary;

public class LOres {

	public static void register() {
		LBlocks.ore_metal.registerOres();
		LBlocks.ore_gem.registerOres();
		LBlocks.ore_other.registerOres();
		LBlocks.storage.registerOres();
		LItems.ingot.registerOres();
		LItems.stick.registerOres();
		LItems.nugget.registerOres();
		LItems.dust.registerOres();
		LItems.gem.registerOres();
		
		for (String gem : LMaterials.gems) {
			OreDictionary.registerOre("ingot"+gem, LItems.gem.getStackForName("gem"+gem));
		}
		
		OreDictionary.registerOre("lanthanoidPrivate-blockEndMetal", LBlocks.storage.getStackForName("blockErbium"));
		OreDictionary.registerOre("lanthanoidPrivate-blockEndMetal", LBlocks.storage.getStackForName("blockGadolinium"));
		
		OreDictionary.registerOre("energizedLutetium", LBlocks.energized_lutetium);
	}

}

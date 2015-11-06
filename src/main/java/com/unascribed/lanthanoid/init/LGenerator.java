package com.unascribed.lanthanoid.init;

import com.unascribed.lanthanoid.gen.GeneratorGroup;
import com.unascribed.lanthanoid.gen.OreGenerator;

import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.init.Blocks;

public class LGenerator {

	public static void initOres() {
		GeneratorGroup group = new GeneratorGroup();
		
		group.add(OreGenerator.create("Copper")
				.block(LBlocks.ore_metal, LBlocks.ore_metal.getMetaForName("oreCopper"))
				.frequency(12)
				.range(48, 64)
				.size(5));
		group.add(OreGenerator.create("Yttrium")
				.block(LBlocks.ore_metal, LBlocks.ore_metal.getMetaForName("oreYttrium"))
				.frequency(10)
				.range(8, 48)
				.size(5));
		group.add(OreGenerator.create("Ytterbium")
				.block(LBlocks.ore_metal, LBlocks.ore_metal.getMetaForName("oreYtterbium"))
				.target(Blocks.netherrack)
				.frequency(10)
				.range(42, 84)
				.dimension(OreGenerator.NETHER)
				.size(5));
		group.add(OreGenerator.create("Praseodymium")
				.block(LBlocks.ore_metal, LBlocks.ore_metal.getMetaForName("orePraseodymium"))
				.target(Blocks.netherrack)
				.frequency(8)
				.range(8, 41)
				.dimension(OreGenerator.NETHER)
				.size(5));
		group.add(OreGenerator.create("Neodymium")
				.block(LBlocks.ore_metal, LBlocks.ore_metal.getMetaForName("oreNeodymium"))
				.target(Blocks.netherrack)
				.dimension(OreGenerator.NETHER)
				.frequency(10)
				.range(85, 128)
				.size(5));
		group.add(OreGenerator.create("Holmium")
				.block(LBlocks.ore_metal, LBlocks.ore_metal.getMetaForName("oreHolmium"))
				.frequency(8)
				.range(80, 120)
				.size(6));
		group.add(OreGenerator.create("Barium")
				.block(LBlocks.ore_metal, LBlocks.ore_metal.getMetaForName("oreBarium"))
				.frequency(12)
				.range(24, 52)
				.size(4));
		group.add(OreGenerator.create("Erbium")
				.block(LBlocks.ore_metal, LBlocks.ore_metal.getMetaForName("oreErbium"))
				.target(Blocks.end_stone)
				.dimension(OreGenerator.THE_END)
				.frequency(12)
				.range(8, 128)
				.size(6));
		group.add(OreGenerator.create("Gadolinium")
				.block(LBlocks.ore_metal, LBlocks.ore_metal.getMetaForName("oreGadolinium"))
				.target(Blocks.end_stone)
				.dimension(OreGenerator.THE_END)
				.frequency(12)
				.range(8, 128)
				.size(6));
		
		group.add(OreGenerator.create("Actinolite")
				.block(LBlocks.ore_gem, LBlocks.ore_gem.getMetaForName("oreActinolite"))
				.frequency(12)
				.range(8, 64)
				.size(4));
		group.add(OreGenerator.create("Diaspore")
				.block(LBlocks.ore_gem, LBlocks.ore_gem.getMetaForName("oreDiaspore"))
				.frequency(4)
				.range(8, 24)
				.size(6));
		group.add(OreGenerator.create("Thulite")
				.block(LBlocks.ore_gem, LBlocks.ore_gem.getMetaForName("oreThulite"))
				.frequency(8)
				.range(24, 48)
				.size(5));
		group.add(OreGenerator.create("Raspite")
				.block(LBlocks.ore_gem, LBlocks.ore_gem.getMetaForName("oreRaspite"))
				.frequency(2)
				.range(18, 32)
				.size(8));
		
		GameRegistry.registerWorldGenerator(group, 5000);
	}

}

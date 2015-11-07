package com.unascribed.lanthanoid.init;

import java.util.Random;

import com.unascribed.lanthanoid.block.BlockEnergizedLutetium;
import com.unascribed.lanthanoid.block.BlockMulti;
import com.unascribed.lanthanoid.block.BlockTechnical;
import com.unascribed.lanthanoid.item.ItemBlockWithCustomName;
import com.unascribed.lanthanoid.util.LArrays;

import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.block.material.Material;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.util.MathHelper;
import net.minecraft.world.IBlockAccess;

public class LBlocks {

	public static BlockMulti ore_metal, ore_gem, ore_other, storage, weak_plating, plating;
	public static BlockTechnical technical;
	public static BlockEnergizedLutetium energized_lutetium;
	
	public static void init() {
		GameRegistry.registerBlock(LBlocks.ore_metal = new BlockMulti(
				Material.rock,
				Blocks.stone,
				
				LArrays.all(LMaterials.metals, "ore"))
				.setTemplate("oreYtterbium", Blocks.netherrack)
				.setTemplate("orePraseodymium", Blocks.gravel)
				.setTemplate("oreNeodymium", Blocks.nether_brick)
				.setTemplate("oreErbium", Blocks.end_stone)
				.setTemplate("oreGadolinium", Blocks.end_stone), ItemBlockWithCustomName.class, "ore_metal");
		GameRegistry.registerBlock(LBlocks.ore_gem = new BlockMulti(Material.rock, Blocks.stone, LArrays.all(LMaterials.gems, "ore")) {
			@Override
			public Item getItemDropped(int p_149650_1_, Random p_149650_2_, int p_149650_3_) {
				return LItems.gem;
			}
			@Override
			public int damageDropped(int meta) {
				String name = helper.getNameForMeta(meta);
				if (name == null) return 3000+meta;
				return LItems.gem.getMetaForName(name.replaceFirst("ore", "gem"));
			}
			@Override
			public int quantityDroppedWithBonus(int bonus, Random random) {
				if (bonus <= 0) return 1;
				return Math.max(1, random.nextInt(bonus + 2)+1);
			}
			private Random rand = new Random();
			@Override
			public int getExpDrop(IBlockAccess world, int metadata, int fortune) {
				return MathHelper.getRandomIntegerInRange(rand, 3, 7);
			}
		}, ItemBlockWithCustomName.class, "ore_gem");
		GameRegistry.registerBlock(LBlocks.ore_other = new BlockMulti(
				Material.rock,
				Blocks.stone,
				
				LArrays.all(LMaterials.others, "ore")), ItemBlockWithCustomName.class, "ore_other");
		
		GameRegistry.registerBlock(LBlocks.storage = new BlockMulti(
				Material.iron,
				Blocks.iron_block,
				
				LArrays.all(LMaterials.gemsAndMetal, "block")
				), ItemBlockWithCustomName.class, "storage");
		
		GameRegistry.registerBlock(LBlocks.weak_plating = new BlockMulti(
				Material.iron,
				Blocks.cobblestone,
				
				LArrays.all(LMaterials.metalsPlusVanilla, "weakplating")
				), ItemBlockWithCustomName.class, "weak_plating");
		
		GameRegistry.registerBlock(LBlocks.plating = new BlockMulti(
				Material.iron,
				Blocks.obsidian,
				
				LArrays.all(LMaterials.metalsPlusVanilla, "plating")
				), ItemBlockWithCustomName.class, "plating");
		
		GameRegistry.registerBlock(LBlocks.technical = new BlockTechnical(), null, "technical");
		
		GameRegistry.registerBlock(LBlocks.energized_lutetium = (BlockEnergizedLutetium) new BlockEnergizedLutetium()
				.setBlockName("energized_lutetium")
				.setBlockTextureName("lanthanoid_compositor:blockLutetium"), "energized_lutetium");
	}

}

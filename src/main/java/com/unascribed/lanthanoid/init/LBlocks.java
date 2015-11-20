package com.unascribed.lanthanoid.init;

import java.util.Random;

import com.unascribed.lanthanoid.block.BlockEnergizedLutetium;
import com.unascribed.lanthanoid.block.BlockMachine;
import com.unascribed.lanthanoid.block.BlockMulti;
import com.unascribed.lanthanoid.block.BlockTechnical;
import com.unascribed.lanthanoid.item.ItemBlockMachine;
import com.unascribed.lanthanoid.item.ItemBlockWithCustomName;
import com.unascribed.lanthanoid.tile.TileEntityEldritchCollector;
import com.unascribed.lanthanoid.tile.TileEntityEldritchDistributor;
import com.unascribed.lanthanoid.tile.TileEntityEldritchFaithPlate;
import com.unascribed.lanthanoid.tile.TileEntityEldritchInductor;
import com.unascribed.lanthanoid.tile.TileEntityWaypoint;
import com.unascribed.lanthanoid.util.LArrays;

import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.util.MathHelper;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class LBlocks {

	public static BlockMulti ore_metal, ore_gem, ore_other, storage, weak_plating, plating, storageβ;
	public static BlockTechnical technical;
	public static BlockEnergizedLutetium energized_lutetium;
	public static BlockMulti misc;
	public static BlockMachine machine;
	
	public static void init() {
		GameRegistry.registerBlock(ore_metal = new BlockMulti(
				Material.rock,
				Blocks.stone,
				
				LArrays.all(LMaterials.metals, "ore")) {
			@Override
			public float getBlockHardness(World worldIn, int x, int y, int z) { return super.getBlockHardness(worldIn, x, y, z)*1.5f; }
		}
				.setTemplate("oreYtterbium", Blocks.netherrack)
				.setTemplate("orePraseodymium", Blocks.gravel)
				.setTemplate("oreNeodymium", Blocks.nether_brick)
				.setTemplate("oreErbium", Blocks.end_stone)
				.setTemplate("oreGadolinium", Blocks.end_stone), ItemBlockWithCustomName.class, "ore_metal");
		GameRegistry.registerBlock(ore_gem = new BlockMulti(Material.rock, Blocks.stone, LArrays.all(LMaterials.gems, "ore")) {
			@Override
			public Item getItemDropped(int p_149650_1_, Random p_149650_2_, int p_149650_3_) {
				return LItems.gem;
			}
			@Override
			public int damageDropped(int meta) {
				String name = helper.getNameForMeta(meta);
				if (name == null) {
					return 3000+meta;
				}
				return LItems.gem.getMetaForName(name.replaceFirst("ore", "gem"));
			}
			@Override
			public int quantityDroppedWithBonus(int bonus, Random random) {
				if (bonus <= 0) {
					return 1;
				}
				return Math.max(1, random.nextInt(bonus + 2)+1);
			}
			private Random rand = new Random();
			@Override
			public int getExpDrop(IBlockAccess world, int metadata, int fortune) {
				return MathHelper.getRandomIntegerInRange(rand, 3, 7);
			}
			@Override
			public float getBlockHardness(World worldIn, int x, int y, int z) { return super.getBlockHardness(worldIn, x, y, z)*1.5f; }
		}, ItemBlockWithCustomName.class, "ore_gem");
		GameRegistry.registerBlock(ore_other = new BlockMulti(
				Material.rock,
				Blocks.stone,
				
				LArrays.all(LMaterials.others, "ore")) {
			@Override
			public float getBlockHardness(World worldIn, int x, int y, int z) { return super.getBlockHardness(worldIn, x, y, z)*1.5f; }
		}, ItemBlockWithCustomName.class, "ore_other");
		
		GameRegistry.registerBlock(storage = new BlockMulti(
				Material.iron,
				Blocks.iron_block,
				
				"blockCopper",
				"blockYttrium",
				"blockBarium",
				"blockYtterbium",
				"blockNeodymium",
				"blockPraseodymium",
				"blockHolmium",
				"blockErbium",
				"blockGadolinium",
				"blockDysprosium",
				"blockCerium",
				"blockLutetium",
				"blockActinolite",
				"blockDiaspore",
				"blockThulite",
				"blockRaspite"
				), ItemBlockWithCustomName.class, "storage");
		GameRegistry.registerBlock(storageβ = new BlockMulti(
				Material.iron,
				Blocks.iron_block,
				
				"blockRosasite",
				"blockYttriumBariumCopperOxide"
				), ItemBlockWithCustomName.class, "storageβ");
		
		GameRegistry.registerBlock(weak_plating = new BlockMulti(
				Material.iron,
				Blocks.iron_block,
				
				LArrays.all(LMaterials.metalsPlusVanilla, "weakplating")
				), ItemBlockWithCustomName.class, "weak_plating");
		
		GameRegistry.registerBlock(plating = new BlockMulti(
				Material.iron,
				Blocks.iron_block,
				
				LArrays.all(LMaterials.metalsPlusVanilla, "plating")
				) {
			@Override
			public float getExplosionResistance(Entity p_149638_1_) {
				return 5000;
			}
			@Override
			public float getExplosionResistance(Entity par1Entity, World world, int x, int y, int z, double explosionX, double explosionY, double explosionZ) {
				return 5000;
			}
		}, ItemBlockWithCustomName.class, "plating");
		
		GameRegistry.registerBlock(technical = new BlockTechnical(), null, "technical");
		
		GameRegistry.registerBlock(energized_lutetium = (BlockEnergizedLutetium) new BlockEnergizedLutetium()
				.setBlockName("energized_lutetium")
				.setBlockTextureName("lanthanoid:plasma"), "energized_lutetium");
		
		GameRegistry.registerBlock(misc = (BlockMulti) new BlockMulti(
				Material.glass,
				Blocks.glowstone,
				
				"lampThulite"
				).setBlockName("lamp"), "misc");
		
		GameRegistry.registerBlock(machine = new BlockMachine(), ItemBlockMachine.class, "machine");
		GameRegistry.registerTileEntity(TileEntityWaypoint.class, "lanthanoid:waypoint");
		GameRegistry.registerTileEntity(TileEntityEldritchFaithPlate.class, "lanthanoid:eldritch_faith_plate");
		GameRegistry.registerTileEntity(TileEntityEldritchCollector.class, "lanthanoid:eldritch_collector");
		GameRegistry.registerTileEntity(TileEntityEldritchDistributor.class, "lanthanoid:eldritch_distributor");
		GameRegistry.registerTileEntity(TileEntityEldritchInductor.class, "lanthanoid:eldritch_inductor");
	}

}

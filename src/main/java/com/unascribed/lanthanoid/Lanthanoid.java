package com.unascribed.lanthanoid;

import java.util.List;
import java.util.Map;
import java.util.Random;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.unascribed.lanthanoid.block.BlockMulti;
import com.unascribed.lanthanoid.item.ItemBlockWithCustomName;
import com.unascribed.lanthanoid.item.ItemMulti;
import com.unascribed.lanthanoid.item.ItemRifle;
import com.unascribed.lanthanoid.item.ItemTeleporter;
import com.unascribed.lanthanoid.network.BeamParticleHandler;
import com.unascribed.lanthanoid.network.BeamParticleMessage;
import com.unascribed.lanthanoid.network.ModifyRifleModeHandler;
import com.unascribed.lanthanoid.network.ModifyRifleModeMessage;
import com.unascribed.lanthanoid.network.RifleChargingSoundHandler;
import com.unascribed.lanthanoid.network.RifleChargingSoundRequest;
import com.unascribed.lanthanoid.proxy.Proxy;
import com.unascribed.lanthanoid.util.Generate;
import com.unascribed.lanthanoid.util.GeneratorGroup;
import com.unascribed.lanthanoid.util.OreGenerator;
import com.unascribed.lanthanoid.util.TextureCompositor;
import com.unascribed.lanthanoid.util.TextureCompositorImpl.BlockBackdrop;
import com.unascribed.lanthanoid.util.TextureCompositorImpl.BlockType;
import com.unascribed.lanthanoid.util.TextureCompositorImpl.ItemType;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;
import cpw.mods.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.AchievementPage;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.oredict.ShapedOreRecipe;
import net.minecraftforge.oredict.ShapelessOreRecipe;

@Mod(
	modid="lanthanoid",
	name="Lanthanoid",
	version="@VERSION@",
	acceptedMinecraftVersions="@MCVERSION@"
	)
public class Lanthanoid {
	public static final Logger log = LogManager.getLogger("Lanthanoid");
	@Instance
	public static Lanthanoid inst;
	@SidedProxy(clientSide="com.unascribed.lanthanoid.proxy.ClientProxy", serverSide="com.unascribed.lanthanoid.proxy.ServerProxy")
	public static Proxy proxy;
	
	public TextureCompositor compositor;
	public CreativeTabs creativeTab = new CreativeTabs("lanthanoid") {
		@Override
		public Item getTabIconItem() {
			return Item.getItemFromBlock(LBlocks.ore_metal);
		}
	};
	
	private List<String> metalsPlusVanilla = Lists.newArrayList("Gold", "Iron");
	private List<String> metals = Lists.newArrayList();
	private List<String> gems = Lists.newArrayList();
	private List<String> others = Lists.newArrayList();
	private List<String> gemsAndMetal = Lists.newArrayList();
	private List<String> gemsAndMetalPlusVanilla = Lists.newArrayList("Gold", "Iron", "Diamond", "Emerald");
	private Map<String, Integer> colors = Maps.newHashMap();
	
	public SimpleNetworkWrapper network;
	
	@EventHandler
	public void preInit(FMLPreInitializationEvent e) {
		log.info("\n"+
		"╔════╗                                                                                                                 ╔════╗\n" + 
		"║  1 ║                                                                                                                 ║  2 ║\n" + 
		"║  H ║                                                                                                                 ║ He ║\n" + 
		"╚════╝                                                                                                                 ╚════╝\n" + 
		"╔════╗ ╔════╗                                                                       ╔════╗ ╔════╗ ╔════╗ ╔════╗ ╔════╗ ╔════╗\n" + 
		"║  3 ║ ║  4 ║                                                                       ║  5 ║ ║  6 ║ ║  7 ║ ║  8 ║ ║  9 ║ ║ 10 ║\n" + 
		"║ Li ║ ║ Be ║                                                                       ║  B ║ ║  C ║ ║  N ║ ║  O ║ ║  F ║ ║ Ne ║\n" + 
		"╚════╝ ╚════╝                                                                       ╚════╝ ╚════╝ ╚════╝ ╚════╝ ╚════╝ ╚════╝\n" + 
		"╔════╗ ╔════╗                                                                       ╔════╗ ╔════╗ ╔════╗ ╔════╗ ╔════╗ ╔════╗\n" + 
		"║ 11 ║ ║ 12 ║                                                                       ║ 13 ║ ║ 14 ║ ║ 15 ║ ║ 16 ║ ║ 17 ║ ║ 18 ║\n" + 
		"║ Na ║ ║ Mg ║                                                                       ║ Al ║ ║ Si ║ ║  P ║ ║  S ║ ║ Cl ║ ║ Ar ║\n" + 
		"╚════╝ ╚════╝                                                                       ╚════╝ ╚════╝ ╚════╝ ╚════╝ ╚════╝ ╚════╝\n" + 
		"╔════╗ ╔════╗ ╔════╗ ╔════╗ ╔════╗ ╔════╗ ╔════╗ ╔════╗ ╔════╗ ╔════╗ ╔════╗ ╔════╗ ╔════╗ ╔════╗ ╔════╗ ╔════╗ ╔════╗ ╔════╗\n" + 
		"║ 19 ║ ║ 20 ║ ║ 21 ║ ║ 22 ║ ║ 23 ║ ║ 24 ║ ║ 25 ║ ║ 26 ║ ║ 27 ║ ║ 28 ║ ║ 29 ║ ║ 30 ║ ║ 31 ║ ║ 32 ║ ║ 33 ║ ║ 34 ║ ║ 35 ║ ║ 36 ║\n" + 
		"║  K ║ ║ Ca ║ ║ Sc ║ ║ Ti ║ ║  V ║ ║ Cr ║ ║ Mn ║ ║ Fe ║ ║ Co ║ ║ Ni ║ ║ Cu ║ ║ Zn ║ ║ Ga ║ ║ Ge ║ ║ As ║ ║ Se ║ ║ Br ║ ║ Kr ║\n" + 
		"╚════╝ ╚════╝ ╚════╝ ╚════╝ ╚════╝ ╚════╝ ╚════╝ ╚════╝ ╚════╝ ╚════╝ ╚════╝ ╚════╝ ╚════╝ ╚════╝ ╚════╝ ╚════╝ ╚════╝ ╚════╝\n" + 
		"╔════╗ ╔════╗ ╔════╗ ╔════╗ ╔════╗ ╔════╗ ╔════╗ ╔════╗ ╔════╗ ╔════╗ ╔════╗ ╔════╗ ╔════╗ ╔════╗ ╔════╗ ╔════╗ ╔════╗ ╔════╗\n" + 
		"║ 37 ║ ║ 38 ║ ║ 39 ║ ║ 40 ║ ║ 41 ║ ║ 42 ║ ║ 43 ║ ║ 44 ║ ║ 45 ║ ║ 46 ║ ║ 47 ║ ║ 48 ║ ║ 49 ║ ║ 50 ║ ║ 51 ║ ║ 52 ║ ║ 53 ║ ║ 54 ║\n" + 
		"║ Rb ║ ║ Sr ║ ║  Y ║ ║ Zr ║ ║ Nb ║ ║ Mo ║ ║ Tc ║ ║ Ru ║ ║ Rh ║ ║ Pd ║ ║ Ag ║ ║ Cd ║ ║ In ║ ║ Sn ║ ║ Sb ║ ║ Te ║ ║  I ║ ║ Xe ║\n" + 
		"╚════╝ ╚════╝ ╚════╝ ╚════╝ ╚════╝ ╚════╝ ╚════╝ ╚════╝ ╚════╝ ╚════╝ ╚════╝ ╚════╝ ╚════╝ ╚════╝ ╚════╝ ╚════╝ ╚════╝ ╚════╝\n" + 
		"╔════╗ ╔════╗ ╔════╗ ╔════╗ ╔════╗ ╔════╗ ╔════╗ ╔════╗ ╔════╗ ╔════╗ ╔════╗ ╔════╗ ╔════╗ ╔════╗ ╔════╗ ╔════╗ ╔════╗ ╔════╗\n" + 
		"║ 55 ║ ║ 56 ║ ║  * ║ ║ 72 ║ ║ 73 ║ ║ 74 ║ ║ 75 ║ ║ 76 ║ ║ 77 ║ ║ 78 ║ ║ 79 ║ ║ 80 ║ ║ 81 ║ ║ 82 ║ ║ 83 ║ ║ 84 ║ ║ 85 ║ ║ 86 ║\n" + 
		"║ Cs ║ ║ Ba ║ ║    ║ ║ Hf ║ ║ Ta ║ ║  W ║ ║ Re ║ ║ Os ║ ║ Ir ║ ║ Pt ║ ║ Au ║ ║ Hg ║ ║ Tl ║ ║ Pb ║ ║ Bi ║ ║ Po ║ ║ At ║ ║ Rn ║\n" + 
		"╚════╝ ╚════╝ ╚════╝ ╚════╝ ╚════╝ ╚════╝ ╚════╝ ╚════╝ ╚════╝ ╚════╝ ╚════╝ ╚════╝ ╚════╝ ╚════╝ ╚════╝ ╚════╝ ╚════╝ ╚════╝\n" + 
		"╔════╗ ╔════╗ ╔════╗ ╔════╗ ╔════╗ ╔════╗ ╔════╗ ╔════╗ ╔════╗ ╔════╗ ╔════╗ ╔════╗ ╔════╗ ╔════╗ ╔════╗ ╔════╗ ╔════╗ ╔════╗\n" + 
		"║ 87 ║ ║ 88 ║ ║  * ║ ║104 ║ ║105 ║ ║106 ║ ║107 ║ ║108 ║ ║109 ║ ║110 ║ ║111 ║ ║112 ║ ║113 ║ ║114 ║ ║115 ║ ║116 ║ ║117 ║ ║118 ║\n" + 
		"║ Fr ║ ║ Ra ║ ║  * ║ ║ Rf ║ ║ Db ║ ║ Sg ║ ║ Bh ║ ║ Hs ║ ║ Mt ║ ║ Ds ║ ║ Rg ║ ║ Cn ║ ║Uut ║ ║ Fl ║ ║Uup ║ ║ Lv ║ ║Uus ║ ║Uuo ║\n" + 
		"╚════╝ ╚════╝ ╚════╝ ╚════╝ ╚════╝ ╚════╝ ╚════╝ ╚════╝ ╚════╝ ╚════╝ ╚════╝ ╚════╝ ╚════╝ ╚════╝ ╚════╝ ╚════╝ ╚════╝ ╚════╝");
		log.info("\n"+
		"              ╔════╗ ╔════╗ ╔════╗ ╔════╗ ╔════╗ ╔════╗ ╔════╗ ╔════╗ ╔════╗ ╔════╗ ╔════╗ ╔════╗ ╔════╗ ╔════╗ ╔════╗\n" + 
		"           *  ║ 57 ║ ║ 58 ║ ║ 59 ║ ║ 60 ║ ║ 61 ║ ║ 62 ║ ║ 63 ║ ║ 64 ║ ║ 65 ║ ║ 66 ║ ║ 67 ║ ║ 68 ║ ║ 69 ║ ║ 70 ║ ║ 71 ║\n" + 
		"              ║ La ║ ║ Ce ║ ║ Pr ║ ║ Nd ║ ║ Pm ║ ║ Sm ║ ║ Eu ║ ║ Gd ║ ║ Tb ║ ║ Dy ║ ║ Ho ║ ║ Er ║ ║ Tm ║ ║ Yb ║ ║ Lu ║\n" + 
		"              ╚════╝ ╚════╝ ╚════╝ ╚════╝ ╚════╝ ╚════╝ ╚════╝ ╚════╝ ╚════╝ ╚════╝ ╚════╝ ╚════╝ ╚════╝ ╚════╝ ╚════╝\n" + 
		"              ╔════╗ ╔════╗ ╔════╗ ╔════╗ ╔════╗ ╔════╗ ╔════╗ ╔════╗ ╔════╗ ╔════╗ ╔════╗ ╔════╗ ╔════╗ ╔════╗ ╔════╗\n" + 
		"           *  ║ 89 ║ ║ 90 ║ ║ 91 ║ ║ 92 ║ ║ 93 ║ ║ 94 ║ ║ 95 ║ ║ 96 ║ ║ 97 ║ ║ 98 ║ ║ 99 ║ ║100 ║ ║101 ║ ║102 ║ ║103 ║\n" +
		"           *  ║ Ac ║ ║ Th ║ ║ Pa ║ ║  U ║ ║ Np ║ ║ Pu ║ ║ Am ║ ║ Cm ║ ║ Bk ║ ║ Cf ║ ║ Es ║ ║ Fm ║ ║ Md ║ ║ No ║ ║ Lr ║\n" + 
		"              ╚════╝ ╚════╝ ╚════╝ ╚════╝ ╚════╝ ╚════╝ ╚════╝ ╚════╝ ╚════╝ ╚════╝ ╚════╝ ╚════╝ ╚════╝ ╚════╝ ╚════╝");
		if (Loader.isModLoaded("farrago")) {
			log.warn("Farrago is deprecated, and duplicates some of the functionality in Lanthanoid. It is recommended you remove it.");
		}
		compositor = proxy.createCompositor();
		
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
		
		// Gems
		addAll("Actinolite", 0x40AD83, BlockType.GEM_ORE, BlockBackdrop.STONE, ItemType.SQUARE_GEM);
		addAll("Diaspore", 0x674BC3, BlockType.GEM_ORE, BlockBackdrop.STONE, ItemType.ROUND_GEM);
		
		addAll("Thulite", 0xCA5E52, BlockType.GEM_ORE, BlockBackdrop.STONE, ItemType.HEX_GEM);
		
		addAll("Rosasite", 0x00A6C3, BlockType.LUMPY_ORE, BlockBackdrop.NONE, ItemType.ORB);
		
		addAll("Raspite", 0xC67226, BlockType.GEM_SQUARE_ORE, BlockBackdrop.STONE, ItemType.WAFER);
		
		others.add("Gypsum");
		
		// ************************************************************************************************** //
		// ** NEW ORES, GEM OR METAL, MUST GO HERE, BELOW ALL EXISTING DEFINITIONS, OR METADATA WILL BREAK ** //
		// ************************************************************************************************** //
		
		
		int goldColor = 0xFDD753;
		int ironColor = 0xEEEEEE;
		int diamondColor = 0x00FFFF;
		int emeraldColor = 0x00FF00;
		
		colors.put("Gold", goldColor);
		colors.put("Iron", ironColor);
		colors.put("Diamond", diamondColor);
		colors.put("Emerald", emeraldColor);
		
		if (compositor != null) {
			compositor.addItem("rifle", colors.get("Holmium"), ItemType.RIFLE);
			
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
			
			compositor.addBlock("machineCobbleSide", 0xFFFFFF, BlockType.MACHINE_BLOCK, BlockBackdrop.COBBLESTONE);
			compositor.addBlock("machineCobbleTop", 0xFFFFFF, BlockType.MACHINE_BLOCK_TOP, BlockBackdrop.COBBLESTONE);
			compositor.addBlock("machineCobbleBottom", 0xFFFFFF, BlockType.MACHINE_BLOCK_BOTTOM, BlockBackdrop.COBBLESTONE);
			
			compositor.addBlock("machineCombustorFrontWorking", 0xFFFFFF, BlockType.MACHINE_COMBUSTOR_WORKING, BlockBackdrop.COBBLESTONE);
			compositor.addBlock("machineCombustorFrontIdle", 0xFFFFFF, BlockType.MACHINE_COMBUSTOR_IDLE, BlockBackdrop.COBBLESTONE);
		}
		
		metalsPlusVanilla.addAll(metals);
		gemsAndMetalPlusVanilla.addAll(gemsAndMetal);
		
		if (compositor != null) {
			for (String s : ItemTeleporter.flavors) {
				compositor.addItem("teleporter"+s, colors.get(s), ItemType.TELEPORTER);
			}
		}
		
		proxy.setupCompositor();
		
		network = new SimpleNetworkWrapper("Lanthanoid");
		network.registerMessage(RifleChargingSoundHandler.class, RifleChargingSoundRequest.class, 0, Side.CLIENT);
		network.registerMessage(ModifyRifleModeHandler.class, ModifyRifleModeMessage.class, 1, Side.SERVER);
		network.registerMessage(BeamParticleHandler.class, BeamParticleMessage.class, 2, Side.CLIENT);
		
		GameRegistry.registerItem(LItems.ingot = new ItemMulti(all(metals, "ingot")), "ingot");
		GameRegistry.registerItem(LItems.stick = new ItemMulti(all(metalsPlusVanilla, "stick")), "stick");
		GameRegistry.registerItem(LItems.nugget = new ItemMulti(exclude(all(metalsPlusVanilla, "nugget"), "nuggetGold")), "nugget");
		GameRegistry.registerItem(LItems.dust = new ItemMulti(all(gemsAndMetalPlusVanilla, "dust")), "dust");
		GameRegistry.registerItem(LItems.gem = new ItemMulti(all(gems, "gem")), "gem");
		
		GameRegistry.registerBlock(LBlocks.ore_metal = new BlockMulti(
				Material.rock,
				Blocks.stone,
				
				all(metals, "ore"))
				.setTemplate("oreYtterbium", Blocks.netherrack)
				.setTemplate("orePraseodymium", Blocks.gravel)
				.setTemplate("oreNeodymium", Blocks.nether_brick)
				.setTemplate("oreErbium", Blocks.end_stone)
				.setTemplate("oreGadolinium", Blocks.end_stone), ItemBlockWithCustomName.class, "ore_metal");
		GameRegistry.registerBlock(LBlocks.ore_gem = new BlockMulti(Material.rock, Blocks.stone, all(gems, "ore")) {
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
				
				all(others, "ore")), ItemBlockWithCustomName.class, "ore_other");
		
		GameRegistry.registerBlock(LBlocks.storage = new BlockMulti(
				Material.iron,
				Blocks.iron_block,
				
				all(gemsAndMetal, "block")
				), ItemBlockWithCustomName.class, "storage");
		
		GameRegistry.registerBlock(LBlocks.plating = new BlockMulti(
				Material.iron,
				Blocks.iron_block,
				
				all(metalsPlusVanilla, "plating")
				) {
			@Override
			public float getExplosionResistance(Entity p_149638_1_) {
				return 50000;
			}
			@Override
			public float getExplosionResistance(Entity par1Entity, World world, int x, int y, int z, double explosionX, double explosionY, double explosionZ) {
				return 50000;
			}
		}, ItemBlockWithCustomName.class, "plating");
		
		GameRegistry.registerItem(LItems.teleporter = new ItemTeleporter(), "teleporter");
		GameRegistry.registerItem(LItems.rifle = new ItemRifle(), "rifle");
		
		//EntityRegistry.registerModEntity(EntityRifleShot.class, "lanthanoid:rifle_shot", 0, this, 64, 12, true);
		
		LBlocks.ore_metal.registerOres();
		LBlocks.ore_gem.registerOres();
		LBlocks.ore_other.registerOres();
		LBlocks.storage.registerOres();
		LItems.ingot.registerOres();
		LItems.stick.registerOres();
		LItems.nugget.registerOres();
		LItems.dust.registerOres();
		LItems.gem.registerOres();
		
		LAchievements.init();
		AchievementPage.registerAchievementPage(LAchievements.page);
		
		OreDictionary.registerOre("lanthanoidPrivate-blockEndMetal", LBlocks.storage.getStackForName("blockErbium"));
		OreDictionary.registerOre("lanthanoidPrivate-blockEndMetal", LBlocks.storage.getStackForName("blockGadolinium"));
		
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
		
		for (String s : metals) {
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
		for (String s : gems) {	
			GameRegistry.addRecipe(new ShapelessOreRecipe(new ItemStack(LBlocks.storage, 1, LBlocks.storage.getMetaForName("block"+s)),
					"gem"+s, "gem"+s, "gem"+s,
					"gem"+s, "gem"+s, "gem"+s,
					"gem"+s, "gem"+s, "gem"+s));
			GameRegistry.addRecipe(new ShapelessOreRecipe(LItems.gem.getStackForName("gem"+s, 9),
					"block"+s));
		}
		
		for (String s : metalsPlusVanilla) {
			GameRegistry.addRecipe(new ShapedOreRecipe(LBlocks.plating.getStackForName("plating"+s, 8),
					"iii",
					"iIi",
					"iii",
					'i', "nugget"+s,
					'I', "stone"));
			if (s.equals("Gold")) {
				GameRegistry.addSmelting(LBlocks.plating.getStackForName("plating"+s, 1), new ItemStack(Items.gold_nugget), 0);
			} else {
				GameRegistry.addSmelting(LBlocks.plating.getStackForName("plating"+s, 1), LItems.nugget.getStackForName("nugget"+s), 0);
			}
			GameRegistry.addRecipe(new ShapedOreRecipe(LItems.stick.getStackForName("stick"+s, 4),
					"i",
					"i",
					'i', "ingot"+s));
		}
		
		GameRegistry.addRecipe(new ShapelessOreRecipe(LItems.nugget.getStackForName("nuggetIron", 9), "ingotIron"));
		GameRegistry.addRecipe(new ShapelessOreRecipe(Items.iron_ingot, 
				"nuggetIron", "nuggetIron", "nuggetIron",
				"nuggetIron", "nuggetIron", "nuggetIron",
				"nuggetIron", "nuggetIron", "nuggetIron"));
		GameRegistry.addRecipe(new ShapedOreRecipe(LItems.rifle,
				"fz ",
				"zdo",
				" sb",
				'f', "gemActinolite",
				'b', "blockHolmium",
				'o', "gemDiaspore",
				's', "stickHolmium",
				'z', "ingotHolmium",
				'd', "nuggetDysprosium"));
		
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
		
		LEventHandler handler = new LEventHandler();
		
		FMLCommonHandler.instance().bus().register(handler);
		MinecraftForge.EVENT_BUS.register(handler);
	}

	private String[] exclude(String[] arr, String... exclude) {
		List<String> li = Lists.newArrayList(arr);
		for (String s : exclude) {
			li.remove(s);
		}
		return li.toArray(new String[li.size()]);
	}

	@EventHandler
	public void onServerStart(FMLServerStartingEvent e) {
		e.registerServerCommand(new CommandBase() {
			
			@Override
			public void processCommand(ICommandSender sender, String[] args) {
				EntityPlayer p = ((EntityPlayer)sender);
				Vec3 look = p.getLookVec();
				Block block = getBlockByText(sender, args[0]);
				int meta = parseIntBounded(sender, args[1], 0, 15);
				int length = parseIntBounded(sender, args[2], 1, 150);
				func_152373_a(sender, this, "command.lanspike.start", length);
				int changed = Generate.spike(p.worldObj, block, meta,
						(int)p.posX, (int)p.posY, (int)p.posZ,
						(float)look.xCoord, (float)look.yCoord, (float)look.zCoord,
						length);
				func_152373_a(sender, this, "command.lanspike.end", changed);
			}
			
			@Override
			public int getRequiredPermissionLevel() {
				return 4;
			}
			
			@Override
			public String getCommandUsage(ICommandSender sender) {
				return "/lanspike <TileName> <dataValue> <length>";
			}
			
			@Override
			public String getCommandName() {
				return "lanspike";
			}
		});
	}
	
	private String[] all(List<String> types, String... prefixes) {
		int count = prefixes.length;
		String[] res = new String[types.size()*count];
		int idx = 0;
		for (int j = 0; j < count; j++) {
			for (int i = 0; i < types.size(); i++) {
				res[idx] = prefixes[j]+types.get(i);
				idx++;
			}
		}
		return res;
	}
	
	private void addAll(String name, int color, BlockType blockType, BlockBackdrop backdrop, ItemType itemType) {
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
	}
	
}

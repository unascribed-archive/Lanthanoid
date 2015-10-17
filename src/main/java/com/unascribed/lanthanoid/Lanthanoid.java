package com.unascribed.lanthanoid;

import java.util.List;
import java.util.Random;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.common.collect.Lists;
import com.unascribed.lanthanoid.TextureCompositor.BlockBackdrop;
import com.unascribed.lanthanoid.TextureCompositor.BlockType;
import com.unascribed.lanthanoid.TextureCompositor.ItemType;

import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;
import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.block.Block;
import net.minecraft.block.BlockOre;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.SimpleReloadableResourceManager;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatStyle;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.oredict.ShapelessOreRecipe;
import scala.actors.threadpool.Arrays;

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
	
	
	public TextureCompositor compositor;
	public CreativeTabs creativeTab = new CreativeTabs("lanthanoid") {
		@Override
		public Item getTabIconItem() {
			return Item.getItemFromBlock(LBlocks.ore_metal);
		}
	};
	
	private List<String> metals = Lists.newArrayList();
	private List<String> gems = Lists.newArrayList();
	private List<String> others = Lists.newArrayList();
	
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
		SimpleReloadableResourceManager srrm = ((SimpleReloadableResourceManager)Minecraft.getMinecraft().getResourceManager());
		compositor = new TextureCompositor(srrm);
		
		addAll("Copper", 0x944A09, BlockType.METAL_ORE, BlockBackdrop.STONE, ItemType.INGOT);
		addAll("Yttrium", 0x496B6E, BlockType.METAL_ORE, BlockBackdrop.STONE, ItemType.INGOT);
		addAll("Barium", 0x39190A, BlockType.METAL_ORE, BlockBackdrop.STONE, ItemType.INGOT);
		
		addAll("Ytterbium", 0x423D00, BlockType.METAL_ORE, BlockBackdrop.NETHERRACK, ItemType.INGOT);
		addAll("Praseodymium", 0x2B4929, BlockType.METAL_ORE, BlockBackdrop.GRAVEL, ItemType.INGOT);
		addAll("Neodymium", 0x363662, BlockType.METAL_ORE, BlockBackdrop.NETHER_BRICK, ItemType.INGOT);
		addAll("Holmium", 0xA8A18D, BlockType.METAL_ORE, BlockBackdrop.STONE, ItemType.INGOT);
		
		addAll("Erbium", 0x1A3996, BlockType.TRACE_ORE, BlockBackdrop.END_STONE, ItemType.INGOT);
		addAll("Gadolinium", 0x157952, BlockType.TRACE_ORE, BlockBackdrop.END_STONE, ItemType.INGOT);
		addAll("Dysprosium", 0x4F0059, BlockType.TRACE_ORE, BlockBackdrop.STONE, ItemType.INGOT);
		
		
		addAll("Actinolite", 0x40AD83, BlockType.GEM_ORE, BlockBackdrop.STONE, ItemType.SQUARE_GEM);
		addAll("Diaspore", 0x674BC3, BlockType.GEM_ORE, BlockBackdrop.STONE, ItemType.ROUND_GEM);
		
		addAll("Thulite", 0xCA5E52, BlockType.GEM_ORE, BlockBackdrop.STONE, ItemType.HEX_GEM);
		
		addAll("Rosasite", 0x00A6C3, BlockType.LUMPY_ORE, BlockBackdrop.NONE, ItemType.ORB);
		
		addAll("Raspite", 0xC67226, BlockType.GEM_SQUARE_ORE, BlockBackdrop.STONE, ItemType.WAFER);
		
		compositor.addBlock("oreGypsum", 0xCBCBCB, BlockType.CRYSTAL);
		others.add("Gypsum");
		
		srrm.registerReloadListener(it -> {
			compositor.load();
			compositor.generate();
		});
		
		
		GameRegistry.registerItem(LItems.resource = new ItemResource(union(
				all(metals, "ingot", "dust", "nugget"),
				all(gems, "gem", "dust"),
				new String[] { "gemRosasite", "dustRosasite" })), "resource");
		
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
				return LItems.resource;
			}
			@Override
			public int damageDropped(int meta) {
				if (meta < 0 || meta >= names.length) return 3000+meta;
				System.out.println(names[meta].replace("ore", "gem"));
				return LItems.resource.getMetaForName(names[meta].replaceFirst("ore", "gem"));
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
				
				union(all(metals, "block"), all(gems, "block"))
				), ItemBlockWithCustomName.class, "storage");
		
		
		
		LBlocks.ore_metal.registerOres();
		LBlocks.ore_gem.registerOres();
		LBlocks.ore_other.registerOres();
		LBlocks.storage.registerOres();
		LItems.resource.registerOres();
		
		for (String s : metals) {
			GameRegistry.addSmelting(new ItemStack(LBlocks.ore_metal, 1, LBlocks.ore_metal.getMetaForName("ore"+s)),
					new ItemStack(LItems.resource, 1, LItems.resource.getMetaForName("ingot"+s)), 1.0f);
			GameRegistry.addRecipe(new ShapelessOreRecipe(new ItemStack(LItems.resource, 9, LItems.resource.getMetaForName("nugget"+s)),
					"ingot"+s));
			GameRegistry.addRecipe(new ShapelessOreRecipe(new ItemStack(LItems.resource, 1, LItems.resource.getMetaForName("ingot"+s)),
					"nugget"+s, "nugget"+s, "nugget"+s,
					"nugget"+s, "nugget"+s, "nugget"+s,
					"nugget"+s, "nugget"+s, "nugget"+s));
			
			GameRegistry.addRecipe(new ShapelessOreRecipe(new ItemStack(LBlocks.storage, 1, LBlocks.storage.getMetaForName("block"+s)),
					"ingot"+s, "ingot"+s, "ingot"+s,
					"ingot"+s, "ingot"+s, "ingot"+s,
					"ingot"+s, "ingot"+s, "ingot"+s));
			GameRegistry.addRecipe(new ShapelessOreRecipe(new ItemStack(LItems.resource, 9, LItems.resource.getMetaForName("ingot"+s)),
					"block"+s));
		}
		for (String s : gems) {	
			GameRegistry.addRecipe(new ShapelessOreRecipe(new ItemStack(LBlocks.storage, 1, LBlocks.storage.getMetaForName("block"+s)),
					"gem"+s, "gem"+s, "gem"+s,
					"gem"+s, "gem"+s, "gem"+s,
					"gem"+s, "gem"+s, "gem"+s));
			GameRegistry.addRecipe(new ShapelessOreRecipe(new ItemStack(LItems.resource, 9, LItems.resource.getMetaForName("gem"+s)),
					"block"+s));
		}
		
		GeneratorGroup group = new GeneratorGroup();
		
		group.add(OreGenerator.create("Copper")
				.block(LBlocks.ore_metal, 0)
				.frequency(12)
				.range(48, 64)
				.size(5));
		group.add(OreGenerator.create("Yttrium")
				.block(LBlocks.ore_metal, 1)
				.frequency(10)
				.range(8, 48)
				.size(5));
		group.add(OreGenerator.create("Ytterbium")
				.block(LBlocks.ore_metal, 2)
				.target(Blocks.netherrack)
				.frequency(10)
				.range(8, 128)
				.dimension(OreGenerator.NETHER)
				.size(5));
		group.add(OreGenerator.create("Praseodymium")
				.block(LBlocks.ore_metal, 3)
				.target(Blocks.gravel)
				.frequency(8)
				.range(16, 24)
				.size(5));
		group.add(OreGenerator.create("Neodymium")
				.block(LBlocks.ore_metal, 4)
				.target(Blocks.nether_brick)
				.dimension(OreGenerator.NETHER)
				.frequency(24)
				.range(8, 128)
				.size(4));
		group.add(OreGenerator.create("Holmium")
				.block(LBlocks.ore_metal, 5)
				.frequency(8)
				.range(80, 120)
				.size(6));
		group.add(OreGenerator.create("Barium")
				.block(LBlocks.ore_metal, 6)
				.frequency(12)
				.range(24, 52)
				.size(4));
		group.add(OreGenerator.create("Erbium")
				.block(LBlocks.ore_metal, 7)
				.target(Blocks.end_stone)
				.dimension(OreGenerator.THE_END)
				.frequency(12)
				.range(8, 128)
				.size(6));
		group.add(OreGenerator.create("Gadolinium")
				.block(LBlocks.ore_metal, 8)
				.target(Blocks.end_stone)
				.dimension(OreGenerator.THE_END)
				.frequency(12)
				.range(8, 128)
				.size(6));
		
		group.add(OreGenerator.create("Actinolite")
				.block(LBlocks.ore_gem, 0)
				.frequency(12)
				.range(8, 64)
				.size(4));
		group.add(OreGenerator.create("Diaspore")
				.block(LBlocks.ore_gem, 1)
				.frequency(4)
				.range(8, 24)
				.size(6));
		group.add(OreGenerator.create("Thulite")
				.block(LBlocks.ore_gem, 2)
				.frequency(8)
				.range(24, 48)
				.size(5));
		group.add(OreGenerator.create("Raspite")
				.block(LBlocks.ore_gem, 3)
				.frequency(2)
				.range(18, 32)
				.size(8));
		
		GameRegistry.registerWorldGenerator(group, 5000);
	}

	private <T> List<T> union(List<T>... lis) {
		int len = 0;
		for (List<T> li : lis) {
			len += li.size();
		}
		List<T> res = Lists.newArrayListWithCapacity(len);
		for (List<T> li : lis) {
			res.addAll(li);
		}
		return res;
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
				int changed = Generate.spike(p.worldObj, block, meta,
						(int)p.posX, (int)p.posY, (int)p.posZ,
						(float)look.xCoord, (float)look.yCoord, (float)look.zCoord,
						length);
				sender.addChatMessage(new ChatComponentText(changed+" blocks changed").setChatStyle(new ChatStyle().setColor(EnumChatFormatting.LIGHT_PURPLE)));
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
	
	private String[] union(String[]... arr) {
		int len = 0;
		for (String[] s : arr) {
			len += s.length;
		}
		String[] res = new String[len];
		int ofs = 0;
		for (String[] s : arr) {
			System.arraycopy(s, 0, res, ofs, s.length);
			ofs += s.length;
		}
		return res;
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
		compositor.addBlock("ore"+name, color, blockType, backdrop);
		if (itemType == ItemType.INGOT) {
			compositor.addItem("ingot"+name, color, itemType);
			compositor.addItem("nugget"+name, color, ItemType.NUGGET);
		} else {
			compositor.addItem("gem"+name, color, itemType);
		}
		if (blockType == BlockType.METAL_ORE || blockType == BlockType.TRACE_ORE) {
			compositor.addBlock("block"+name, color, BlockType.METAL_BLOCK);
			metals.add(name);
		} else if (blockType == BlockType.GEM_ORE || blockType == BlockType.GEM_SQUARE_ORE) {
			compositor.addBlock("block"+name, color, BlockType.GEM_BLOCK);
			gems.add(name);
		} else {
			compositor.addBlock("block"+name, color, blockType);
			others.add(name);
		}
		compositor.addItem("dust"+name, color, ItemType.DUST);
	}
	
}

package com.unascribed.lanthanoid;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

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
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.SimpleReloadableResourceManager;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.util.Vec3;

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
		srrm.registerReloadListener(it -> {
			compositor = new TextureCompositor(srrm);
			compositor.load();
			addAll("Copper", 0x944A09, BlockType.METAL, BlockBackdrop.STONE, ItemType.INGOT);
			addAll("Yttrium", 0x496B6E, BlockType.METAL, BlockBackdrop.STONE, ItemType.INGOT);
			addAll("Barium", 0x39190A, BlockType.METAL, BlockBackdrop.STONE, ItemType.INGOT);
			
			addAll("Ytterbium", 0x423D00, BlockType.METAL, BlockBackdrop.NETHERRACK, ItemType.INGOT);
			addAll("Praseodymium", 0x2B4929, BlockType.METAL, BlockBackdrop.GRAVEL, ItemType.INGOT);
			addAll("Neodymium", 0x363662, BlockType.METAL, BlockBackdrop.NETHER_BRICK, ItemType.INGOT);
			addAll("Holmium", 0xA8A18D, BlockType.METAL, BlockBackdrop.STONE, ItemType.INGOT);
			
			addAll("Erbium", 0x1A3996, BlockType.TRACE, BlockBackdrop.END_STONE, ItemType.INGOT);
			addAll("Gadolinium", 0x157952, BlockType.TRACE, BlockBackdrop.END_STONE, ItemType.INGOT);
			addAll("Dysprosium", 0x4F0059, BlockType.TRACE, BlockBackdrop.OBSIDIAN, ItemType.INGOT);
			
			
			addAll("Actinolite", 0x40AD83, BlockType.GEM, BlockBackdrop.STONE, ItemType.SQUARE_GEM);
			addAll("Diaspore", 0x674BC3, BlockType.GEM, BlockBackdrop.STONE, ItemType.ROUND_GEM);
			
			addAll("Thulite", 0xCA5E52, BlockType.ROUGH, BlockBackdrop.STONE, ItemType.HEX_GEM);
			
			addAll("Rosasite", 0x00A6C3, BlockType.LUMPY, BlockBackdrop.STONE, ItemType.ORB);
			
			addAll("Raspite", 0xC67226, BlockType.SQUARE, BlockBackdrop.STONE, ItemType.WAFER);
			
			compositor.addBlock("oreGypsum", 0xCACACA, BlockType.CRYSTAL, BlockBackdrop.NONE);
			
			
			compositor.generate();
		});
		
		GameRegistry.registerBlock(LBlocks.ore_metal = new BlockOre(
				"oreCopper",
				"oreYttrium",
				"oreYtterbium",
				"orePraseodymium",
				"oreNeodymium",
				"oreHolmium",
				"oreBarium",
				"oreErbium",
				"oreGadolinium",
				"oreDysprosium")
				.setBackdrop("oreYtterbium", Blocks.netherrack)
				.setBackdrop("orePraseodymium", Blocks.gravel)
				.setBackdrop("oreNeodymium", Blocks.nether_brick)
				.setBackdrop("oreErbium", Blocks.end_stone)
				.setBackdrop("oreGadolinium", Blocks.end_stone)
				.setBackdrop("oreDysprosium", Blocks.obsidian), ItemBlockWithCustomName.class, "ore_metal");
		GameRegistry.registerBlock(LBlocks.ore_gem = new BlockOre(
				"oreActinolite",
				"oreDiaspore",
				"oreRaspite"), ItemBlockWithCustomName.class, "ore_gem");
		GameRegistry.registerBlock(LBlocks.ore_other = new BlockOre(
				"oreGypsum",
				"oreRosasite",
				"oreThulite"), ItemBlockWithCustomName.class, "ore_other");
		
		
		GameRegistry.registerItem(LItems.resource = new ItemResource(union(
				all(
					"ingot",
					
					"Copper",
					"Yttrium",
					"Ytterbium",
					"Praseodymium",
					"Neodymium",
					"Holmium",
					"Barium",
					"Erbium",
					"Gadolinium",
					"Dysprosium"
				),
				all(
					"gem",
					
					"Actinolite",
					"Diaspore",
					"Raspite",
					"Rosasite",
					"Thulite"
				))), "resource");
		
		LBlocks.ore_metal.registerOres();
		LBlocks.ore_gem.registerOres();
		LBlocks.ore_other.registerOres();
		LItems.resource.registerOres();
		
		GeneratorGroup group = new GeneratorGroup();
		
		// Copper (SEEN)
		group.add(OreGenerator.create("Copper")
				.block(LBlocks.ore_metal, 0)
				.frequency(12)
				.range(48, 64)
				.size(5));
		// Yttrium
		group.add(OreGenerator.create("Yttrium")
				.block(LBlocks.ore_metal, 1)
				.frequency(10)
				.range(8, 48)
				.size(2));
		// Ytterbium
		group.add(OreGenerator.create("Ytterbium")
				.block(LBlocks.ore_metal, 2)
				.target(Blocks.netherrack)
				.frequency(10)
				.range(8, 128)
				.dimension(OreGenerator.NETHER)
				.size(4));
		// Praseodymium
		group.add(OreGenerator.create("Praseodymium")
				.block(LBlocks.ore_metal, 3)
				.target(Blocks.gravel)
				.frequency(8)
				.range(16, 24)
				.size(4));
		// Neodymium
		group.add(OreGenerator.create("Neodymium")
				.block(LBlocks.ore_metal, 4)
				.target(Blocks.nether_brick)
				.dimension(OreGenerator.NETHER)
				.frequency(24)
				.range(8, 128)
				.size(4));
		// Holmium
		group.add(OreGenerator.create("Holmium")
				.block(LBlocks.ore_metal, 5)
				.frequency(8)
				.range(80, 256)
				.size(6));
		// Barium (SEEN)
		group.add(OreGenerator.create("Barium")
				.block(LBlocks.ore_metal, 6)
				.frequency(12)
				.range(24, 52)
				.size(4));
		// Erbium (SEEN)
		group.add(OreGenerator.create("Erbium")
				.block(LBlocks.ore_metal, 7)
				.target(Blocks.end_stone)
				.dimension(OreGenerator.THE_END)
				.frequency(12)
				.range(8, 128)
				.size(6));
		// Gadolinium (SEEN)
		group.add(OreGenerator.create("Gadolinium")
				.block(LBlocks.ore_metal, 8)
				.target(Blocks.end_stone)
				.dimension(OreGenerator.THE_END)
				.frequency(12)
				.range(8, 128)
				.size(6));
		// Dysprosium
		group.add(OreGenerator.create("Dysprosium")
				.block(LBlocks.ore_metal, 9)
				.target(Blocks.obsidian)
				.dimension(OreGenerator.THE_END)
				.frequency(48)
				.range(8, 128)
				.size(12));
		
		
		GameRegistry.registerWorldGenerator(group, 4);
	}

	@EventHandler
	public void onServerStart(FMLServerStartingEvent e) {
		e.registerServerCommand(new CommandBase() {
			
			@Override
			public void processCommand(ICommandSender sender, String[] args) {
				EntityPlayer p = ((EntityPlayer)sender);
				Vec3 look = p.getLookVec();
				Generate.spike(p.worldObj, LBlocks.ore_other, 0, (int)p.posX, (int)p.posY, (int)p.posZ, (float)look.xCoord, (float)look.yCoord, (float)look.zCoord, parseInt(sender, args[0]));
			}
			
			@Override
			public String getCommandUsage(ICommandSender sender) {
				return "hi";
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
	
	private String[] all(String prefix, String... types) {
		String[] res = new String[types.length*2];
		for (int i = 0; i < types.length; i++) {
			res[i*2] = prefix+types[i];
			res[(i*2)+1] = "dust"+types[i];
		}
		return res;
	}
	
	private void addAll(String name, int color, BlockType blockType, BlockBackdrop backdrop, ItemType itemType) {
		compositor.addBlock("ore"+name, color, blockType, backdrop);
		if (itemType == ItemType.INGOT) {
			compositor.addItem("ingot"+name, color, itemType);
		} else {
			compositor.addItem("gem"+name, color, itemType);
		}
		compositor.addItem("dust"+name, color, ItemType.DUST);
	}
	
}

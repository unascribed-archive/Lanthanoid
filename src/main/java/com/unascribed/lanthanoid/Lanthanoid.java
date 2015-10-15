package com.unascribed.lanthanoid;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.unascribed.lanthanoid.TextureCompositor.BlockBackdrop;
import com.unascribed.lanthanoid.TextureCompositor.BlockType;
import com.unascribed.lanthanoid.TextureCompositor.ItemType;

import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.SimpleReloadableResourceManager;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;

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
	
	@Mod.EventHandler
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
			
			addAll("Ytterbium", 0x423D00, BlockType.METAL, BlockBackdrop.STONE, ItemType.INGOT);
			addAll("Praseodymium", 0x2B4929, BlockType.METAL, BlockBackdrop.STONE, ItemType.INGOT);
			addAll("Neodymium", 0x363662, BlockType.METAL, BlockBackdrop.STONE, ItemType.INGOT);
			addAll("Holmium", 0xA8A18D, BlockType.METAL, BlockBackdrop.STONE, ItemType.INGOT);
			
			addAll("Europium", 0x1A3996, BlockType.TRACE, BlockBackdrop.STONE, ItemType.INGOT);
			addAll("Gadolinium", 0x157952, BlockType.TRACE, BlockBackdrop.STONE, ItemType.INGOT);
			addAll("Dysprosium", 0x4F0059, BlockType.TRACE, BlockBackdrop.STONE, ItemType.INGOT);
			
			
			addAll("Actinolite", 0x40AD83, BlockType.GEM, BlockBackdrop.STONE, ItemType.WAFER);
			addAll("Empholite", 0x674BC3, BlockType.GEM, BlockBackdrop.STONE, ItemType.WAFER);
			
			addAll("Thulite", 0xCA5E52, BlockType.ROUGH, BlockBackdrop.STONE, ItemType.WAFER);
			
			addAll("Rosasite", 0x00A6C3, BlockType.LUMPY, BlockBackdrop.STONE, ItemType.WAFER);
			
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
				"oreEuropium",
				"oreGadolinium",
				"oreDysprosium"), ItemBlockWithCustomName.class, "ore_metal");
		GameRegistry.registerBlock(LBlocks.ore_gem = new BlockOre(
				"oreActinolite",
				"oreEmpholite",
				"oreRaspite",
				"oreRosasite",
				"oreThulite"), ItemBlockWithCustomName.class, "ore_gem");
		GameRegistry.registerBlock(LBlocks.ore_other = new BlockOre(
				"oreGypsum"), ItemBlockWithCustomName.class, "ore_other");
		
		
		GameRegistry.registerItem(LItems.resource = new ItemResource(union(
				all(
					"ingot",
					
					"Copper",
					"Yttrium",
					"Barium",
					"Ytterbium",
					"Praseodymium",
					"Neodymium",
					"Holmium",
					"Europium",
					"Gadolinium",
					"Dysprosium"
				),
				all(
					"gem",
					
					"Actinolite",
					"Empholite",
					"Raspite",
					"Rosasite",
					"Thulite"
				))), "resource");
		
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

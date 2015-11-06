package com.unascribed.lanthanoid;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.unascribed.lanthanoid.client.LClientEventHandler;
import com.unascribed.lanthanoid.client.TextureCompositorImpl.ItemType;
import com.unascribed.lanthanoid.init.LBlocks;
import com.unascribed.lanthanoid.init.LCommands;
import com.unascribed.lanthanoid.init.LGenerator;
import com.unascribed.lanthanoid.init.LItems;
import com.unascribed.lanthanoid.init.LMaterials;
import com.unascribed.lanthanoid.init.LNetwork;
import com.unascribed.lanthanoid.init.LRecipes;
import com.unascribed.lanthanoid.item.ItemTeleporter;
import com.unascribed.lanthanoid.proxy.Proxy;
import com.unascribed.lanthanoid.util.TextureCompositor;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;
import cpw.mods.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.util.Vec3;
import net.minecraft.world.WorldProvider;
import net.minecraftforge.common.AchievementPage;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.oredict.OreDictionary;

@Mod(
	modid="lanthanoid",
	name="Lanthanoid",
	version="@VERSION@",
	acceptedMinecraftVersions="@MCVERSION@",
	dependencies="required-after:malisiscore"
	)
public class Lanthanoid {
	public static final Logger log = LogManager.getLogger("Lanthanoid");
	@Instance
	public static Lanthanoid inst;
	@SidedProxy(clientSide="com.unascribed.lanthanoid.proxy.ClientProxy", serverSide="com.unascribed.lanthanoid.proxy.ServerProxy")
	public static Proxy proxy;
	public static boolean isObfEnv;
	
	public TextureCompositor compositor;
	public CreativeTabs creativeTab = new CreativeTabs("lanthanoid") {
		@Override
		public Item getTabIconItem() {
			return Item.getItemFromBlock(LBlocks.ore_metal);
		}
	};
	
	public SimpleNetworkWrapper network;
	
	@EventHandler
	public void preInit(FMLPreInitializationEvent e) {
		PeriodicTable.print();
		if (Loader.isModLoaded("farrago")) {
			log.warn("Farrago is deprecated, and duplicates some of the functionality in Lanthanoid. It is recommended you remove it.");
		}
		compositor = proxy.createCompositor();
		
		LMaterials.init();
		
		if (compositor != null) {
			for (String s : ItemTeleporter.flavors) {
				compositor.addItem("teleporter"+s, LMaterials.colors.get(s), ItemType.TELEPORTER);
			}
		}
		
		proxy.setup();
		
		network = new SimpleNetworkWrapper("Lanthanoid");
		LNetwork.init();
		
		LItems.init();
		LBlocks.init();
		
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
		
		LRecipes.init();
		
		LGenerator.initOres();
		
		LEventHandler handler = new LEventHandler();
		
		FMLCommonHandler.instance().bus().register(handler);
		MinecraftForge.EVENT_BUS.register(handler);
		proxy.init();
	}

	@EventHandler
	public void onServerStart(FMLServerStartingEvent e) {
		LCommands.register(e::registerServerCommand);
	}
	
	@SideOnly(Side.CLIENT)
	public static Vec3 modifySkyColor(WorldProvider base, Entity entity, float partialTicks) {
		Vec3 color = base.getSkyColor(entity, partialTicks);
		LClientEventHandler.inst.onSkyColor(color, entity, partialTicks);
		return color;
	}

	@SideOnly(Side.CLIENT)
	public static double getDistanceWeight(double base) {
		return base*getDistanceWeightForFactor(LClientEventHandler.scopeFactor);
	}
	
	public static double getDistanceWeightForFactor(int scopeFactor) {
		double hscope = scopeFactor/2.5D;
		return Math.max(1, hscope);
	}
	
	public static boolean forceTrackingFor(EntityPlayerMP player, Entity entity) {
		int scopeFactor = ((LanthanoidProperties)player.getExtendedProperties("lanthanoid")).scopeFactor;
		double dist = 64*getDistanceWeightForFactor(scopeFactor);
		return scopeFactor > 1 && entity.getDistanceSqToEntity(player) <= (dist*dist);
	}
	
}

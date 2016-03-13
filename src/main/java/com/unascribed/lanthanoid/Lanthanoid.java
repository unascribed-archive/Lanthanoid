package com.unascribed.lanthanoid;

import org.apache.logging.log4j.Logger;

import com.unascribed.lanthanoid.client.LClientEventHandler;
import com.unascribed.lanthanoid.client.TextureCompositorImpl.BlockType;
import com.unascribed.lanthanoid.client.TextureCompositorImpl.ItemType;
import com.unascribed.lanthanoid.init.LAchievements;
import com.unascribed.lanthanoid.init.LBlocks;
import com.unascribed.lanthanoid.init.LCommands;
import com.unascribed.lanthanoid.init.LConfig;
import com.unascribed.lanthanoid.init.LGenerator;
import com.unascribed.lanthanoid.init.LItems;
import com.unascribed.lanthanoid.init.LMachines;
import com.unascribed.lanthanoid.init.LMaterials;
import com.unascribed.lanthanoid.init.LNetwork;
import com.unascribed.lanthanoid.init.LOres;
import com.unascribed.lanthanoid.init.LRecipes;
import com.unascribed.lanthanoid.item.ItemTeleporter;
import com.unascribed.lanthanoid.proxy.Proxy;
import com.unascribed.lanthanoid.util.TextureCompositor;
import com.unascribed.lanthanoid.waypoint.WaypointManager;
import com.unascribed.lanthanoid.waypoint.WaypointSavedData;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
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
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.common.MinecraftForge;

@Mod(
	modid="lanthanoid",
	name="Lanthanoid",
	version="@VERSION@",
	acceptedMinecraftVersions="@MCVERSION@",
	dependencies="required-after:malisiscore"
	)
public class Lanthanoid {
	public static Logger log;
	@Instance
	public static Lanthanoid inst;
	@SidedProxy(clientSide="com.unascribed.lanthanoid.proxy.ClientProxy", serverSide="com.unascribed.lanthanoid.proxy.ServerProxy")
	public static Proxy proxy;
	public static boolean isObfEnv;
	
	public TextureCompositor compositor;
	public CreativeTabs creativeTabOres = new CreativeTabs("lanthanoid_ores") {
		@Override
		public Item getTabIconItem() {
			return Item.getItemFromBlock(LBlocks.ore_metal);
		}
	};
	public CreativeTabs creativeTabEquipment = new CreativeTabs("lanthanoid_equipment") {
		@Override
		public Item getTabIconItem() {
			return LItems.rifle;
		}
	};
	public CreativeTabs creativeTabDecorative = new CreativeTabs("lanthanoid_decorative") {
		@Override
		public Item getTabIconItem() {
			return Item.getItemFromBlock(LBlocks.plating);
		}
	};
	public CreativeTabs creativeTabMachines = new CreativeTabs("lanthanoid_machines") {
		@Override
		public Item getTabIconItem() {
			return Item.getItemFromBlock(LBlocks.machine);
		}
	};
	public CreativeTabs creativeTabMaterials = new CreativeTabs("lanthanoid_materials") {
		@Override
		public Item getTabIconItem() {
			return LItems.ingot;
		}
	};
	
	public SimpleNetworkWrapper network;
	public WaypointManager waypointManager = new WaypointManager();
	
	@EventHandler
	public void preInit(FMLPreInitializationEvent e) {
		log = e.getModLog();
		if (Loader.isModLoaded("farrago")) {
			log.warn("Farrago is deprecated, and duplicates some of the functionality in Lanthanoid. It is recommended you remove it.");
		}
		compositor = proxy.createCompositor();
		
		LConfig.init(e.getSuggestedConfigurationFile());
		
		LMaterials.init();
		LMachines.init();
		
		if (compositor != null) {
			for (String s : ItemTeleporter.flavors) {
				compositor.addItem("teleporter"+s, LMaterials.colors.get(s), ItemType.TELEPORTER);
			}
			compositor.addBlock("lampThulite", LMaterials.colors.get("Thulite"), BlockType.LAMP);
		}
		
		proxy.setup();
		
		LNetwork.init();
		
		LItems.init();
		LBlocks.init();
		LItems.initBlocks();
		
		LOres.register();
		
		LAchievements.init();
		
		LRecipes.init();
		
		LGenerator.initOres();
		
		LEventHandler handler = new LEventHandler();
		
		FMLCommonHandler.instance().bus().register(handler);
		MinecraftForge.EVENT_BUS.register(handler);
		proxy.init();
	}
	
	@EventHandler
	public void onPostInit(FMLPostInitializationEvent e) {
		if (!Loader.isModLoaded("engination")) {
			log.info("Daremo ga modomeru shoukei wa tsukurareta mahoroshi..");
		}
	}
	
	@EventHandler
	public void onServerStart(FMLServerStartingEvent e) {
		LCommands.register(e::registerServerCommand);
		WorldServer w = DimensionManager.getWorld(0);
		WaypointSavedData wsd = (WaypointSavedData) w.mapStorage.loadData(WaypointSavedData.class, "lanthanoidWaypoints");
		if (wsd == null) {
			w.mapStorage.setData("lanthanoidWaypoints", wsd = new WaypointSavedData("lanthanoidWaypoints"));
		}
		waypointManager.setSaveManager(wsd);
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

package com.unascribed.lanthanoid;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.unascribed.lanthanoid.client.LClientEventHandler;
import com.unascribed.lanthanoid.client.TextureCompositorImpl.BlockBackdrop;
import com.unascribed.lanthanoid.client.TextureCompositorImpl.BlockType;
import com.unascribed.lanthanoid.client.TextureCompositorImpl.ItemType;
import com.unascribed.lanthanoid.gen.Generate;
import com.unascribed.lanthanoid.init.LBlocks;
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
import net.minecraft.block.Block;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
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

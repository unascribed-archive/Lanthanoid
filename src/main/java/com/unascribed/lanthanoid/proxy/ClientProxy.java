package com.unascribed.lanthanoid.proxy;

import com.unascribed.lanthanoid.client.EldritchItemRenderer;
import com.unascribed.lanthanoid.client.EldritchTileEntitySpecialRenderer;
import com.unascribed.lanthanoid.client.LClientEventHandler;
import com.unascribed.lanthanoid.client.MachineItemRenderer;
import com.unascribed.lanthanoid.client.RifleItemRenderer;
import com.unascribed.lanthanoid.client.TextureCompositorImpl;
import com.unascribed.lanthanoid.client.WaypointTileEntitySpecialRenderer;
import com.unascribed.lanthanoid.client.WreckingBallItemRenderer;
import com.unascribed.lanthanoid.init.LBlocks;
import com.unascribed.lanthanoid.init.LItems;
import com.unascribed.lanthanoid.tile.TileEntityEldritch;
import com.unascribed.lanthanoid.tile.TileEntityWaypoint;
import com.unascribed.lanthanoid.util.TextureCompositor;

import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.common.FMLCommonHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.SimpleReloadableResourceManager;
import net.minecraft.item.Item;
import net.minecraftforge.client.MinecraftForgeClient;
import net.minecraftforge.common.MinecraftForge;

public class ClientProxy implements Proxy {
	private TextureCompositor compositor;
	@Override
	public TextureCompositor createCompositor() {
		SimpleReloadableResourceManager srrm = ((SimpleReloadableResourceManager)Minecraft.getMinecraft().getResourceManager());
		return compositor = new TextureCompositorImpl(srrm);
	}
	@Override
	public void setup() {
		SimpleReloadableResourceManager srrm = ((SimpleReloadableResourceManager)Minecraft.getMinecraft().getResourceManager());
		srrm.registerReloadListener(it -> {
			compositor.load();
			compositor.generate();
		});
	}
	@Override
	public void init() {
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityWaypoint.class, new WaypointTileEntitySpecialRenderer());
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityEldritch.class, new EldritchTileEntitySpecialRenderer());
		LClientEventHandler ceh = new LClientEventHandler();
		ceh.init();
		FMLCommonHandler.instance().bus().register(ceh);
		MinecraftForge.EVENT_BUS.register(ceh);
		MinecraftForgeClient.registerItemRenderer(LItems.rifle, new RifleItemRenderer());
		
		MinecraftForgeClient.registerItemRenderer(Item.getItemFromBlock(LBlocks.machine), new MachineItemRenderer());
		
		MinecraftForgeClient.registerItemRenderer(LItems.ytterbium_wrecking_ball, new WreckingBallItemRenderer());
		MinecraftForgeClient.registerItemRenderer(LItems.erbium_wrecking_ball, new WreckingBallItemRenderer());
		MinecraftForgeClient.registerItemRenderer(LItems.dysprosium_wrecking_ball, new WreckingBallItemRenderer());
		
		MinecraftForgeClient.registerItemRenderer(LItems.eldritch_pickaxe, new EldritchItemRenderer(0.625f, 0.625f, -30f, LItems.eldritch_pickaxe::getGlyphs));
		MinecraftForgeClient.registerItemRenderer(LItems.eldritch_shovel, new EldritchItemRenderer(0.5f, 0.565f, 45f, LItems.eldritch_shovel::getGlyphs));
		MinecraftForgeClient.registerItemRenderer(LItems.eldritch_axe, new EldritchItemRenderer(0.585f, 0.585f, -30f, LItems.eldritch_axe::getGlyphs));
		MinecraftForgeClient.registerItemRenderer(LItems.eldritch_sword, new EldritchItemRenderer(0.65f, 0.5f, 225f, LItems.eldritch_sword::getGlyphs));
		MinecraftForgeClient.registerItemRenderer(LItems.eldritch_drill, new EldritchItemRenderer(0.35f, 0.5f, -45f, LItems.eldritch_drill::getGlyphs));
	}
}

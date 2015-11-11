package com.unascribed.lanthanoid.proxy;

import com.unascribed.lanthanoid.client.LClientEventHandler;
import com.unascribed.lanthanoid.client.MachineItemRenderer;
import com.unascribed.lanthanoid.client.RifleItemRenderer;
import com.unascribed.lanthanoid.client.TextureCompositorImpl;
import com.unascribed.lanthanoid.client.WaypointTileEntitySpecialRenderer;
import com.unascribed.lanthanoid.client.WreckingBallItemRenderer;
import com.unascribed.lanthanoid.init.LBlocks;
import com.unascribed.lanthanoid.init.LItems;
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
		LClientEventHandler ceh = new LClientEventHandler();
		ceh.init();
		FMLCommonHandler.instance().bus().register(ceh);
		MinecraftForge.EVENT_BUS.register(ceh);
		MinecraftForgeClient.registerItemRenderer(LItems.rifle, new RifleItemRenderer());
		MinecraftForgeClient.registerItemRenderer(Item.getItemFromBlock(LBlocks.machine), new MachineItemRenderer());
		MinecraftForgeClient.registerItemRenderer(LItems.ytterbium_wrecking_ball, new WreckingBallItemRenderer());
		MinecraftForgeClient.registerItemRenderer(LItems.erbium_wrecking_ball, new WreckingBallItemRenderer());
		MinecraftForgeClient.registerItemRenderer(LItems.dysprosium_wrecking_ball, new WreckingBallItemRenderer());
	}
}

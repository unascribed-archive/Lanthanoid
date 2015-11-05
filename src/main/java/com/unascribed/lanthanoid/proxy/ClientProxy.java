package com.unascribed.lanthanoid.proxy;

import com.unascribed.lanthanoid.LClientEventHandler;
import com.unascribed.lanthanoid.LItems;
import com.unascribed.lanthanoid.RifleItemRenderer;
import com.unascribed.lanthanoid.util.TextureCompositor;
import com.unascribed.lanthanoid.util.TextureCompositorImpl;

import cpw.mods.fml.common.FMLCommonHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.SimpleReloadableResourceManager;
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
		LClientEventHandler ceh = new LClientEventHandler();
		ceh.init();
		FMLCommonHandler.instance().bus().register(ceh);
		MinecraftForge.EVENT_BUS.register(ceh);
		MinecraftForgeClient.registerItemRenderer(LItems.rifle, new RifleItemRenderer());
	}
}

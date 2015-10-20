package com.unascribed.lanthanoid.proxy;

import com.unascribed.lanthanoid.util.TextureCompositor;
import com.unascribed.lanthanoid.util.TextureCompositorImpl;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.SimpleReloadableResourceManager;

public class ClientProxy implements Proxy {
	private TextureCompositor compositor;
	@Override
	public TextureCompositor createCompositor() {
		SimpleReloadableResourceManager srrm = ((SimpleReloadableResourceManager)Minecraft.getMinecraft().getResourceManager());
		return compositor = new TextureCompositorImpl(srrm);
	}
	@Override
	public void setupCompositor() {
		SimpleReloadableResourceManager srrm = ((SimpleReloadableResourceManager)Minecraft.getMinecraft().getResourceManager());
		srrm.registerReloadListener(it -> {
			compositor.load();
			compositor.generate();
		});
	}
}

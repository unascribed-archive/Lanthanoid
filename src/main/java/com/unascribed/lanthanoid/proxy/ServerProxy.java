package com.unascribed.lanthanoid.proxy;

import com.unascribed.lanthanoid.util.TextureCompositor;

public class ServerProxy implements Proxy {
	@Override public TextureCompositor createCompositor() { return null; }
	@Override public void setup() {}
	@Override public void init() {}
	@Override public int getTechnicalRenderType() { return 0; }
}

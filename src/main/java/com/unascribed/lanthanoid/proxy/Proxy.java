package com.unascribed.lanthanoid.proxy;

import com.unascribed.lanthanoid.util.TextureCompositor;

public interface Proxy {

	TextureCompositor createCompositor();

	void setupCompositor();

}

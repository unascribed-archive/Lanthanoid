package com.unascribed.lanthanoid.util;

import com.unascribed.lanthanoid.util.TextureCompositorImpl.BlockBackdrop;
import com.unascribed.lanthanoid.util.TextureCompositorImpl.BlockType;
import com.unascribed.lanthanoid.util.TextureCompositorImpl.ItemType;

public interface TextureCompositor {

	void load();

	void generate();

	void addBlock(String name, int color, BlockType type);

	void addBlock(String name, int color, BlockType type, BlockBackdrop backdrop);

	void addItem(String name, int color, ItemType type);

	void addAlias(String regex, String replacement);

}
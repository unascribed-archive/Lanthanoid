package com.unascribed.lanthanoid;

import cpw.mods.fml.common.Loader;

public class EnginationHint {
	static {
		if (Loader.isModLoaded("engination")) {
			Lanthanoid.log.info("Everything, for starters.");
		}
	}
}

package com.unascribed.lanthanoid;

import cpw.mods.fml.common.Loader;

public class EnginationHint {
	private static final String[] responses = {
			"Maybe some glass. I dunno, not feeling up to it today.",
			"Everything, for starters.",
			"Everything, obviously.",
			"Those goddamn Aether portals. They're so loud.",
	};
	static {
		if (Loader.isModLoaded("engination")) {
			Lanthanoid.log.info(responses[(int)(Math.random()*responses.length)]);
		}
	}
}

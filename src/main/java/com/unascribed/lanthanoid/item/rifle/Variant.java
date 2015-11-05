package com.unascribed.lanthanoid.item.rifle;

public enum Variant {
	NONE("ironsights", false),
	ZOOM("scope", false),
	OVERCLOCK("radiator", true),
	SUPERCLOCKED("radiator2", true),
	EFFICIENCY("cartridge", true),
	SUPEREFFICIENCY("wot", true),
	;
	public final String icon;
	public boolean colorize;
	Variant(String icon, boolean colorize) {
		this.icon = icon;
		this.colorize = colorize;
	}
	public float getSpeedMultiplier() {
		switch (this) {
			case OVERCLOCK:
				return 1.5f;
			case SUPERCLOCKED:
				return 2.0f;
			case EFFICIENCY:
				return 0.75f;
			case SUPEREFFICIENCY:
				return 0.5f;
			default:
				return 1.0f;
		}
	}
	public int getAmmoPerDust() {
		switch (this) {
			case OVERCLOCK:
				return 3;
			case SUPERCLOCKED:
				return 1;
			case EFFICIENCY:
				return 9;
			case SUPEREFFICIENCY:
				return 12;
			default:
				return 6;
		}
	}
}
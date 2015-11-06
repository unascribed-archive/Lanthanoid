package com.unascribed.lanthanoid.item.rifle;

public enum Variant {
	NONE("ironsights", false, 0),
	ZOOM("scope", false, 1),
	OVERCLOCK("radiator", true, 1),
	SUPERCLOCKED("radiator2", true, 2),
	EFFICIENCY("cartridge", true, 1),
	SUPEREFFICIENCY("wot", true, 2),
	;
	public final String icon;
	public final boolean colorize;
	public final int tier;
	Variant(String icon, boolean colorize, int tier) {
		this.icon = icon;
		this.colorize = colorize;
		this.tier = tier;
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
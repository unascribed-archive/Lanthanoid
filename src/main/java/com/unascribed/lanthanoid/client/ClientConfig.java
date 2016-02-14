package com.unascribed.lanthanoid.client;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Properties;

import com.google.common.base.Enums;
import com.google.common.base.Objects;
import com.google.common.io.Files;
import com.google.common.primitives.Floats;
import com.unascribed.lanthanoid.Lanthanoid;

import net.minecraft.client.resources.I18n;

public class ClientConfig {
	public enum Eagerness {
		EAGER("gui.lanthanoid.eager"),
		LAZY("gui.lanthanoid.lazy"),
		;
		public static final int count = values().length;
		public final String key;
		Eagerness(String key) {
			this.key = key;
		}
		@Override
		public String toString() {
			return I18n.format(key);
		}
	}

	private static final File configFile = new File("options-lanthanoid.txt");
	
	public static final Eagerness defaultFlightScheme = Eagerness.EAGER;
	public static final boolean defaultInvertSlowfall = false;
	public static final boolean defaultInvertHover = false;
	public static final float defaultWaypointScale = 1;
	public static final float defaultHudGlyphScale = 1;
	public static final boolean defaultBootsChangeFov = false;
	
	public static Eagerness flightScheme = defaultFlightScheme;
	public static boolean invertSlowfall = defaultInvertSlowfall;
	public static boolean invertHover = defaultInvertHover;
	public static float waypointScale = defaultWaypointScale;
	public static float hudGlyphScale = defaultHudGlyphScale;
	public static boolean bootsChangeFov = defaultBootsChangeFov;
	
	
	public static void save() {
		Properties props = new Properties();
		props.setProperty("flightScheme", flightScheme.name());
		props.setProperty("invertSlowfall", Boolean.toString(invertSlowfall));
		props.setProperty("invertHover", Boolean.toString(invertHover));
		props.setProperty("waypointScale", Float.toString(waypointScale));
		props.setProperty("hudGlyphScale", Float.toString(hudGlyphScale));
		props.setProperty("bootsChangeFov", Boolean.toString(invertHover));
		try {
			Files.createParentDirs(configFile);
			configFile.createNewFile();
			FileOutputStream fos = new FileOutputStream(configFile);
			props.store(fos, null);
		} catch (Exception e) {
			Lanthanoid.log.warn("Failed to save client configuration", e);
		}
	}
	
	public static void load() {
		if (configFile.exists()) {
			try {
				Properties props = new Properties();
				FileInputStream fis = new FileInputStream(configFile);
				props.load(fis);
				flightScheme = Enums.getIfPresent(Eagerness.class, props.getProperty("flightScheme")).or(defaultFlightScheme);
				invertSlowfall = Boolean.valueOf(props.getProperty("invertSlowfall"));
				invertHover = Boolean.valueOf(props.getProperty("invertHover"));
				waypointScale = Objects.firstNonNull(Floats.tryParse(props.getProperty("waypointScale")), defaultWaypointScale);
				hudGlyphScale = Objects.firstNonNull(Floats.tryParse(props.getProperty("hudGlyphScale")), defaultWaypointScale);
				bootsChangeFov = Boolean.valueOf(props.getProperty("bootsChangeFov"));
			} catch (Exception e) {
				Lanthanoid.log.warn("Failed to load client configuration", e);
			}
		} else {
			flightScheme = Eagerness.EAGER;
			invertSlowfall = false;
			invertHover = false;
			waypointScale = 1;
			hudGlyphScale = 1;
		}
	}
}

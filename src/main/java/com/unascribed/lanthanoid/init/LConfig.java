package com.unascribed.lanthanoid.init;

import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.google.common.base.Charsets;
import com.google.common.base.Throwables;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.io.Files;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import com.typesafe.config.ConfigObject;
import com.typesafe.config.ConfigOrigin;
import com.typesafe.config.ConfigRenderOptions;
import com.typesafe.config.ConfigValue;
import com.unascribed.lanthanoid.Lanthanoid;

public class LConfig {
	
	private static transient Config config;
	
	@Comment("Orange metal ore that generates high up in dimensions with stone")
	public static boolean generate_copper = true;
	@Comment("Blue-grey metal ore that generates everywhere in dimensions with stone")
	public static boolean generate_yttrium = true;
	
	@Comment("Dark yellow metal ore that generates in the middle of the Nether")
	public static boolean generate_ytterbium = true;
	@Comment("Dark green metal ore that generates in the bottom of the Nether")
	public static boolean generate_praseodymium = true;
	@Comment("Dark blue metal ore that generates in the top of the Nether")
	public static boolean generate_neodymium = true;
	
	@Comment("Off-white metal ore that generates in the mountains of dimensions with stone")
	public static boolean generate_holmium = true;
	@Comment("Brown metal ore that generates in the middle of dimensions with stone")
	public static boolean generate_barium = true;
	
	@Comment("Bright blue metal ore that generates in the End")
	public static boolean generate_erbium = true;
	@Comment("Bright green metal ore that generates in the End")
	public static boolean generate_gadolinum = true;
	
	@Comment("Light teal gem ore that generates everywhere in dimensions with stone")
	public static boolean generate_actinolite = true;
	@Comment("Light purple gem ore that generates low in dimensions with stone")
	public static boolean generate_diaspore = true;
	@Comment("Light red gem ore that generates in the middle of dimensions with stone")
	public static boolean generate_thulite = true;
	@Comment("Orange gem ore that generates very low in dimensions with stone")
	public static boolean generate_raspite = true;
	
	@Comment("Large caves full of gypsum and rosasite")
	public static boolean generate_gypsumCaves = true;
	@Comment("Huge craters from space ship crashes (required for endgame items)")
	public static boolean generate_crashSites = true;
	
	
	@Comment("Spelunker's Teleporter, an unreliable, dangerous, short-ranged handheld teleporter. Was actually designed for a no-teleportation modpack.")
	public static boolean item_teleporter = true;
	@Comment("Lanthanide Rifle, a versatile laser rifle, with many variants and modes.")
	public static boolean item_rifle = true;
	
	public static void init(File file) {
		try {
			if (!file.exists()) {
				generateConfig(file);
			} else {
				loadConfig(file);
			}
		} catch (Exception e) {
			Throwables.propagate(e);
		}
	}

	private static void loadConfig(File file) throws Exception {
		config = ConfigFactory.parseFile(file);
		for (Field f : LConfig.class.getDeclaredFields()) {
			if (Modifier.isTransient(f.getModifiers())) continue;
			String path = f.getName().replace('_', '.');
			if (!config.hasPath(path) || "unchanged".equals(config.getString(path))) {
				Lanthanoid.log.info("Ignoring missing or unchanged config value {}", path);
				continue;
			}
			if (f.getType() == boolean.class) {
				f.set(null, config.getBoolean(path));
			} else if (f.getType() == int.class) {
				f.set(null, config.getInt(path));
			} else {
				Lanthanoid.log.error("Unknown type {}", f.getType().getCanonicalName());
			}
			Lanthanoid.log.info("Set {} to {}", path, f.get(null));
		}
	}

	private static transient Class<? extends Enum> originType;
	private static transient Enum<?> generic;
	
	private static transient Class<? extends Enum> resolveStatus;
	private static transient Enum<?> resolved;
	
	private static transient Class<? extends ConfigObject> configObject;
	private static transient Constructor<? extends ConfigObject> configObjectConstructor;
	
	private static transient Class<? extends ConfigOrigin> configOrigin;
	private static transient Constructor<? extends ConfigOrigin> configOriginConstructor;
	
	private static transient Class<? extends ConfigValue> configValue;
	private static transient Constructor<? extends ConfigValue> configValueConstructor;
	
	private static void loadReflection() throws Exception {
		originType = (Class<? extends Enum>) Class.forName("com.typesafe.config.impl.OriginType");
		generic = originType.getEnumConstants()[0];
		
		resolveStatus = (Class<? extends Enum>) Class.forName("com.typesafe.config.impl.ResolveStatus");
		resolved = resolveStatus.getEnumConstants()[1];
		
		configObject = (Class<? extends ConfigObject>) Class.forName("com.typesafe.config.impl.SimpleConfigObject");
		configObjectConstructor = configObject.getDeclaredConstructor(ConfigOrigin.class, Map.class, resolveStatus, boolean.class);
		configObjectConstructor.setAccessible(true);
		
		configOrigin = (Class<? extends ConfigOrigin>) Class.forName("com.typesafe.config.impl.SimpleConfigOrigin");
		configOriginConstructor = configOrigin.getDeclaredConstructor(String.class, int.class, int.class, originType, String.class, List.class);
		configOriginConstructor.setAccessible(true);
		
		configValue = (Class<? extends ConfigValue>) Class.forName("com.typesafe.config.impl.ConfigString");
		configValueConstructor = configValue.getDeclaredConstructor(ConfigOrigin.class, String.class);
		configValueConstructor.setAccessible(true);

	}
	
	private static void generateConfig(File file) throws Exception {
		loadReflection();
		
		Map<String, Object> builder = Maps.newLinkedHashMap();
		Field[] fields = LConfig.class.getDeclaredFields();
		for (Field f : fields) {
			if (Modifier.isTransient(f.getModifiers())) continue;
			String[] parts = f.getName().split("_");
			Comment ann = f.getAnnotation(Comment.class);
			List<String> comments = Lists.newArrayList();
			if (ann != null) {
				comments.add(ann.value());
				comments.add("");
			}
			Map<String, Object> cursor = builder;
			for (int i = 0; i < parts.length-1; i++) {
				if (!cursor.containsKey(parts[i])) {
					cursor.put(parts[i], new LinkedHashMap<String, Object>());
				}
				cursor = (Map<String, Object>) cursor.get(parts[i]);
			}
			comments.add("Default value: "+f.get(null));
			ConfigOrigin origin = configOriginConstructor.newInstance("", 0, 0, generic, null, comments);
			ConfigValue val = configValueConstructor.newInstance(origin, "unchanged");
			cursor.put(parts[parts.length-1], val);
		}
		config = build(builder).toConfig();
		ConfigRenderOptions opt = ConfigRenderOptions.defaults().setOriginComments(false).setJson(false);
		Files.write("# Lanthanoid configuration\n# Any value can be set to 'unchanged' to use the default\n\n"+config.root().render(opt).replace("=", ": ").replace("unchanged", "unchanged\n"), file, Charsets.UTF_8);
	}

	private static ConfigObject build(Map<String, Object> builder) throws Exception {
		Map<String, ConfigValue> built = Maps.newLinkedHashMap();
		for (Map.Entry<String, Object> en : builder.entrySet()) {
			if (en.getValue() instanceof ConfigValue) {
				built.put(en.getKey(), (ConfigValue)en.getValue());
			} else {
				built.put(en.getKey(), build((Map<String, Object>) en.getValue()));
			}
		}
		return configObjectConstructor.newInstance(configOriginConstructor.newInstance("", 0, 0, generic, null, null), built, resolved, true);
	}
}

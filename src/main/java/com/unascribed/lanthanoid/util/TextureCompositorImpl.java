package com.unascribed.lanthanoid.util;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.imageio.ImageIO;

import com.google.common.base.Charsets;
import com.google.common.base.Throwables;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.unascribed.lanthanoid.Lanthanoid;

import net.minecraft.client.resources.IResourcePack;
import net.minecraft.client.resources.SimpleReloadableResourceManager;
import net.minecraft.client.resources.data.IMetadataSection;
import net.minecraft.client.resources.data.IMetadataSerializer;
import net.minecraft.util.ResourceLocation;

public class TextureCompositorImpl implements IResourcePack, TextureCompositor {

	public interface CompositeType {
		String name();
		String prefix();
	}
	
	public enum BlendMode {
		NORMAL,
		OVERLAY,
		COLORIZE,
	}

	public enum BlockType implements CompositeType {
		METAL_ORE,
		GEM_ORE,
		GEM_SQUARE_ORE,
		LUMPY_ORE,
		TRACE_ORE,
		CRYSTAL,
		METAL_BLOCK,
		GEM_BLOCK,
		PLATING,
		MACHINE_BLOCK,
		MACHINE_BLOCK_TOP,
		MACHINE_BLOCK_BOTTOM,
		MACHINE_COMBUSTOR_IDLE,
		MACHINE_COMBUSTOR_WORKING,
		;
		public String prefix() { return "blocks/"; }
	}
	
	public enum ItemType implements CompositeType {
		WAFER,
		INGOT,
		DUST,
		HEX_GEM,
		ROUND_GEM,
		SQUARE_GEM,
		TRIANGLE_GEM,
		ORB,
		NUGGET,
		TELEPORTER,
		STICK,
		RIFLE,
		RAIL,
		;
		public String prefix() { return "items/"; }
	}
	
	public enum BlockBackdrop {
		NONE,
		STONE("minecraft", "textures/blocks/stone.png"),
		END_STONE("minecraft", "textures/blocks/end_stone.png"),
		NETHERRACK("minecraft", "textures/blocks/netherrack.png"),
		NETHER_BRICK("minecraft", "textures/blocks/nether_brick.png"),
		GRAVEL("minecraft", "textures/blocks/gravel.png"),
		OBSIDIAN("minecraft", "textures/blocks/obsidian.png"),
		COBBLESTONE("minecraft", "textures/blocks/cobblestone.png"),
		;
		public final ResourceLocation loc;
		BlockBackdrop() {
			loc = null;
		}
		BlockBackdrop(String domain, String path) {
			loc = new ResourceLocation(domain, path);
		}
	}
	
	private static class Task {
		public String name;
		public int color;
		public List<CompositeStep> steps;
	}
	
	private static class CompositeStep {
		public LazyReference<BufferedImage> img;
		public BlendMode mode;
		public CompositeStep(LazyReference<BufferedImage> img, BlendMode mode) {
			this.img = img;
			this.mode = mode;
		}
		
	}
	
	private static final String PATH = "textures/composite/";
	
	private SimpleReloadableResourceManager rm;
	
	private List<Task> tasks = Lists.newArrayList();
	private Map<BlockBackdrop, BufferedImage> backdrops = Maps.newEnumMap(BlockBackdrop.class);
	private Map<CompositeType, List<CompositeStep>> types = Maps.newHashMap();
	private Map<Pattern, String> aliases = Maps.newHashMap();
	
	private Map<String, byte[]> results = Maps.newHashMap();
	private Set<String> animated = Sets.newHashSet();
	
	public TextureCompositorImpl(SimpleReloadableResourceManager rm) {
		this.rm = rm;
		try {
			loadSteps();
		} catch (IOException e) {
			Lanthanoid.log.error("Failed to load steps", e);
		}
	}

	@Override
	public void load() {
		try {
			loadSteps();
			loadBackdrops();
		} catch (Exception e) {
			Lanthanoid.log.error("Failed to load backdrops and steps", e);
		}
	}
	
	@Override
	public void generate() {
		try {
			Lanthanoid.log.info("Compositing "+tasks.size()+" textures");
			ByteArrayOutputStream baos = new ByteArrayOutputStream(16*16*4);
			for (Task o : tasks) {
				try {
					BufferedImage img = doComposite(o);
					baos.reset();
					ImageIO.write(img, "PNG", baos);
					results.put(o.name, baos.toByteArray());
					if (img.getWidth() != img.getHeight()) {
						animated.add(o.name);
					}
					if ("true".equals(System.getProperty("com.unascribed.lanthanoid.DebugCompositor"))) {
						ImageIO.write(buffer(img.getScaledInstance(
								img.getWidth()*2,
								img.getHeight()*2, Image.SCALE_FAST)), "PNG",
								new File("lanthanoid-compositor-"+o.name.replace("s/", "-")+".png"));
					}
				} catch (Exception e) {
					Lanthanoid.log.error("Failed to composite texture for "+o.name, e);
				}
			}
			rm.reloadResourcePack(this);
		} catch (Exception e) {
			Lanthanoid.log.error("Failed to composite ore textures", e);
		}
	}
	
	private BufferedImage doComposite(Task task) {
		int w = 0;
		int h = 0;
		for (CompositeStep step : task.steps) {
			step.img.clear();
			w = Math.max(step.img.get().getWidth(), w);
			h = Math.max(step.img.get().getHeight(), h);
		}
		for (CompositeStep step : task.steps) {
			step.img.set(buffer(step.img.get().getScaledInstance(w, -1, Image.SCALE_FAST)));
		}
		for (CompositeStep step : task.steps) {
			w = Math.max(step.img.get().getWidth(), w);
			h = Math.max(step.img.get().getHeight(), h);
		}
		BufferedImage img = new BufferedImage(w, h, BufferedImage.TYPE_4BYTE_ABGR);
		float inR = ((task.color >> 16) & 0xFF) / 255f;
		float inG = ((task.color >> 8) & 0xFF) / 255f;
		float inB = (task.color & 0xFF) / 255f;
		Graphics2D g2d = img.createGraphics();
		for (CompositeStep step : task.steps) {
			BufferedImage draw;
			switch (step.mode) {
				case NORMAL:
					draw = step.img.get();
					break;
				case COLORIZE:
					draw = copy(step.img.get());
					for (int x = 0; x < draw.getWidth(); x++) {
						for (int y = 0; y < draw.getHeight(); y++) {
							int rgb = draw.getRGB(x, y);
							float r = ((rgb >> 16) & 0xFF) / 255f;
							float g = ((rgb >> 8) & 0xFF) / 255f;
							float b = (rgb & 0xFF) / 255f;
							float a = ((rgb >> 24) & 0xFF) / 255f;
							r *= inR;
							g *= inG;
							b *= inB;
							int res = 0;
							res |= ((int)(r*255))<<16;
							res |= ((int)(g*255))<<8;
							res |= ((int)(b*255));
							res |= ((int)(a*255))<<24;
							draw.setRGB(x, y, res);
						}
					}
					break;
				case OVERLAY:
					draw = buffer(step.img.get().getScaledInstance(w, h, Image.SCALE_FAST));
					for (int x = 0; x < w; x++) {
						for (int y = 0; y < h; y++) {
							int srcRgb = draw.getRGB(x, y);
							int dstRgb = img.getRGB(x, y);
							float srcR = ((srcRgb >> 16) & 0xFF) / 255f;
							float srcG = ((srcRgb >> 8) & 0xFF) / 255f;
							float srcB = (srcRgb & 0xFF) / 255f;
							float srcA = ((srcRgb >> 24) & 0xFF) / 255f;
							
							float dstR = ((dstRgb >> 16) & 0xFF) / 255f;
							float dstG = ((dstRgb >> 8) & 0xFF) / 255f;
							float dstB = (dstRgb & 0xFF) / 255f;
							
							float r = overlay(dstR, srcR);
							float g = overlay(dstG, srcG);
							float b = overlay(dstB, srcB);
							
							int res = 0;
							res |= ((int)(r*255))<<16;
							res |= ((int)(g*255))<<8;
							res |= ((int)(b*255));
							res |= ((int)(srcA*255))<<24;
							draw.setRGB(x, y, res);
						}
					}
					break;
				default:
					throw new RuntimeException("Unknown blend mode "+step.mode);
			}
			for (int j = 0; j < h/draw.getHeight(); j++) {
				g2d.drawImage(draw, 0, j*draw.getHeight(), null);
			}
		}
		g2d.dispose();
		return img;
	}

	private float overlay(float a, float b) {
		if (a < 0.5f) {
			return (2*(a*b));
		} else {
			return 1 - (2*(1-a)*(1-b));
		}
	}

	private BufferedImage buffer(Image img) {
		if (img instanceof BufferedImage) return ((BufferedImage)img);
		BufferedImage out = new BufferedImage(img.getWidth(null), img.getHeight(null), BufferedImage.TYPE_4BYTE_ABGR);
		Graphics2D g2d = out.createGraphics();
		g2d.drawImage(img, 0, 0, img.getWidth(null), img.getHeight(null), null);
		g2d.dispose();
		return out;
	}

	private BufferedImage copy(BufferedImage img) {
		BufferedImage out = new BufferedImage(img.getWidth(), img.getHeight(), BufferedImage.TYPE_4BYTE_ABGR);
		Graphics2D g2d = out.createGraphics();
		g2d.drawImage(img, 0, 0, out.getWidth(), out.getHeight(), null);
		g2d.dispose();
		return out;
	}

	private void loadBackdrops() throws IOException {
		backdrops.clear();
		for (BlockBackdrop b : BlockBackdrop.values()) {
			if (b.loc == null) continue;
			backdrops.put(b, cropToWidth(readImage(b.loc).get()));
		}
	}
	
	private BufferedImage cropToWidth(BufferedImage img) {
		int s = img.getWidth();
		BufferedImage target = new BufferedImage(s, s, BufferedImage.TYPE_4BYTE_ABGR);
		int[] arr = new int[s*s];
		img.getRGB(0, 0, s, s, arr, 0, s);
		target.setRGB(0, 0, s, s, arr, 0, s);
		return target;
	}

	private LazyReference<BufferedImage> readImage(ResourceLocation loc) throws IOException {
		return new LazyReference<>(() -> {
			try {
				InputStream in = rm.getResource(loc).getInputStream();
				BufferedImage img = ImageIO.read(in);
				in.close();
				return img;
			} catch (Exception e) {
				throw Throwables.propagate(e);
			}
		});
	}

	private void loadSteps() throws IOException {
		types.clear();
		loadFourStep(BlockType.METAL_ORE);
		loadFourStep(BlockType.GEM_ORE);
		loadFourStep(BlockType.GEM_SQUARE_ORE);
		loadSingleStep(BlockType.LUMPY_ORE);
		loadFourStep(BlockType.TRACE_ORE);
		loadSingleStep(BlockType.CRYSTAL);
		loadTwoStepGlint(BlockType.METAL_BLOCK);
		loadTwoStepBevel(BlockType.GEM_BLOCK);
		loadSingleStep(BlockType.PLATING);
		loadSingleStepBevel(BlockType.MACHINE_BLOCK);
		loadSingleStepBevel(BlockType.MACHINE_BLOCK_BOTTOM);
		loadSingleStepBevel(BlockType.MACHINE_BLOCK_TOP);
		loadMachineFront(BlockType.MACHINE_COMBUSTOR_IDLE);
		loadMachineFront(BlockType.MACHINE_COMBUSTOR_WORKING);
		
		loadTwoStepGlint(ItemType.WAFER);
		loadTwoStepGlint(ItemType.INGOT);
		loadTwoStepGlint(ItemType.DUST);
		loadTwoStepBevel(ItemType.HEX_GEM);
		loadTwoStepBevel(ItemType.ROUND_GEM);
		loadTwoStepBevel(ItemType.TRIANGLE_GEM);
		loadTwoStepBevel(ItemType.SQUARE_GEM);
		loadTwoStepGlint(ItemType.ORB);
		loadTwoStepGlint(ItemType.NUGGET);
		loadThreeStep(ItemType.TELEPORTER);
		loadSingleStep(ItemType.STICK);
		loadSingleStep(ItemType.RIFLE);
		loadSingleStep(ItemType.RAIL);
	}

	private void loadMachineFront(CompositeType type) throws IOException {
		List<CompositeStep> li = Lists.newArrayList();
		String name = type.name().toLowerCase();
		String prefix = type.prefix();
		li.add(new CompositeStep(readImage(new ResourceLocation("lanthanoid", PATH+prefix+"machine_block_top_bevel.png")), BlendMode.NORMAL));
		li.add(new CompositeStep(readImage(new ResourceLocation("lanthanoid", PATH+prefix+name+".png")), BlendMode.NORMAL));
		types.put(type, li);
	}

	private void loadThreeStep(CompositeType type) throws IOException {
		List<CompositeStep> li = Lists.newArrayList();
		String name = type.name().toLowerCase();
		String prefix = type.prefix();
		li.add(new CompositeStep(readImage(new ResourceLocation("lanthanoid", PATH+prefix+name+"_basis.png")), BlendMode.COLORIZE));
		li.add(new CompositeStep(readImage(new ResourceLocation("lanthanoid", PATH+prefix+name+"_glint.png")), BlendMode.OVERLAY));
		li.add(new CompositeStep(readImage(new ResourceLocation("lanthanoid", PATH+prefix+name+"_decor.png")), BlendMode.NORMAL));
		types.put(type, li);
	}

	private void loadTwoStepGlint(CompositeType type) throws IOException {
		List<CompositeStep> li = Lists.newArrayList();
		String name = type.name().toLowerCase();
		String prefix = type.prefix();
		li.add(new CompositeStep(readImage(new ResourceLocation("lanthanoid", PATH+prefix+name+"_basis.png")), BlendMode.COLORIZE));
		li.add(new CompositeStep(readImage(new ResourceLocation("lanthanoid", PATH+prefix+name+"_glint.png")), BlendMode.OVERLAY));
		types.put(type, li);
	}
	
	private void loadTwoStepBevel(CompositeType type) throws IOException {
		List<CompositeStep> li = Lists.newArrayList();
		String name = type.name().toLowerCase();
		String prefix = type.prefix();
		li.add(new CompositeStep(readImage(new ResourceLocation("lanthanoid", PATH+prefix+name+"_basis.png")), BlendMode.COLORIZE));
		li.add(new CompositeStep(readImage(new ResourceLocation("lanthanoid", PATH+prefix+name+"_bevel.png")), BlendMode.NORMAL));
		types.put(type, li);
	}

	private void loadSingleStepBevel(CompositeType type) throws IOException {
		String name = type.name().toLowerCase();
		String prefix = type.prefix();
		types.put(type, Lists.newArrayList(new CompositeStep(readImage(new ResourceLocation("lanthanoid", PATH+prefix+name+"_bevel.png")), BlendMode.NORMAL)));
	}
	
	private void loadSingleStep(CompositeType type) throws IOException {
		String name = type.name().toLowerCase();
		String prefix = type.prefix();
		types.put(type, Lists.newArrayList(new CompositeStep(readImage(new ResourceLocation("lanthanoid", PATH+prefix+name+".png")), BlendMode.COLORIZE)));
	}

	private void loadFourStep(CompositeType type) throws IOException {
		List<CompositeStep> li = Lists.newArrayList();
		String name = type.name().toLowerCase();
		String prefix = type.prefix();
		li.add(new CompositeStep(readImage(new ResourceLocation("lanthanoid", PATH+prefix+name+"_basis.png")), BlendMode.COLORIZE));
		li.add(new CompositeStep(readImage(new ResourceLocation("lanthanoid", PATH+prefix+name+"_bevel.png")), BlendMode.NORMAL));
		li.add(new CompositeStep(readImage(new ResourceLocation("lanthanoid", PATH+prefix+name+"_glint.png")), BlendMode.OVERLAY));
		li.add(new CompositeStep(readImage(new ResourceLocation("lanthanoid", PATH+prefix+name+"_shade.png")), BlendMode.NORMAL));
		types.put(type, li);
	}

	@Override
	public void addBlock(String name, int color, BlockType type) {
		addBlock(name, color, type, BlockBackdrop.NONE);
	}
	
	@Override
	public void addBlock(String name, int color, BlockType type, BlockBackdrop backdrop) {
		Task o = new Task();
		o.name = "blocks/"+name;
		o.color = color;
		o.steps = Lists.newArrayList();
		if (backdrop.loc != null) {
			o.steps.add(new CompositeStep(new LazyReference<>(() -> backdrops.get(backdrop)), BlendMode.NORMAL));
		}
		o.steps.addAll(types.get(type));
		tasks.add(o);
	}
	
	@Override
	public void addItem(String name, int color, ItemType type) {
		Task o = new Task();
		o.name = "items/"+name;
		o.color = color;
		o.steps = Lists.newArrayList();
		o.steps.addAll(types.get(type));
		tasks.add(o);
	}
	
	@Override
	public void addAlias(String regex, String replacement) {
		aliases.put(Pattern.compile(regex), replacement);
	}

	private boolean isResultName(String name) {
		String str = "assets/lanthanoid_compositor/textures/";
		return name != null && name.startsWith(str) && (name.endsWith(".png") || name.endsWith(".png.mcmeta"));
	}
	
	private String nameToResultName(String name) {
		String str = "assets/lanthanoid_compositor/textures/";
		int len = (name.endsWith(".png") ? 4 : 11);
		String nm = name.substring(str.length(), name.length()-len);
		boolean modified;
		do {
			modified = false;
			for (Map.Entry<Pattern, String> en : aliases.entrySet()) {
				Matcher m = en.getKey().matcher(nm);
				if (m.matches()) {
					nm = m.replaceAll(en.getValue());
					modified = true;
					break;
				}
			}
		} while (modified);
		return nm;
	}
	
	@Override
	public Set getResourceDomains() {
		return ImmutableSet.of("lanthanoid_compositor");
	}

	private byte[] animationBytes = "{\"animation\":{\"frametime\":2}}".getBytes(Charsets.UTF_8);
	
	protected InputStream getInputStreamByName(String name) throws IOException {
		return hasResourceName(name) ? new ByteArrayInputStream(name.endsWith(".png.mcmeta") ? animationBytes : results.get(nameToResultName(name))) : null;
	}

	protected boolean hasResourceName(String name) {
		return isResultName(name) && (name.endsWith(".png.mcmeta") ? animated.contains(nameToResultName(name)) : results.containsKey(nameToResultName(name)));
	}

	@Override
	public String getPackName() {
		return "Lanthanoid Texture Compositor";
	}

	public InputStream getInputStream(ResourceLocation loc) throws IOException {
		return this.getInputStreamByName(locationToName(loc));
	}

	private String locationToName(ResourceLocation loc) {
		return "assets/"+loc.getResourceDomain()+"/"+loc.getResourcePath();
	}

	public boolean resourceExists(ResourceLocation loc) {
		return this.hasResourceName(locationToName(loc));
	}

	@Override
	public IMetadataSection getPackMetadata(IMetadataSerializer serializer, String path) throws IOException {
		return null;
	}

	@Override
	public BufferedImage getPackImage() throws IOException {
		return null;
	}
	
	
}
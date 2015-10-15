package com.unascribed.lanthanoid;

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

import javax.imageio.ImageIO;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import net.minecraft.client.resources.IResourcePack;
import net.minecraft.client.resources.SimpleReloadableResourceManager;
import net.minecraft.client.resources.data.IMetadataSection;
import net.minecraft.client.resources.data.IMetadataSerializer;
import net.minecraft.util.ResourceLocation;

public class TextureCompositor implements IResourcePack {

	public interface CompositeType {
		String name();
	}
	
	public enum BlendMode {
		NORMAL,
		OVERLAY,
		COLORIZE
	}

	public enum BlockType implements CompositeType {
		METAL,
		GEM,
		SQUARE,
		LUMPY,
		ROUGH,
		TRACE,
		CRYSTAL
	}
	
	public enum ItemType implements CompositeType {
		WAFER,
		INGOT,
		DUST
	}
	
	public enum BlockBackdrop {
		NONE,
		STONE("minecraft", "textures/blocks/stone.png"),
		END_STONE("minecraft", "textures/blocks/end_stone.png"),
		NETHERRACK("minecraft", "textures/blocks/netherrack.png"),
		NETHER_BRICK("minecraft", "textures/blocks/nether_brick.png"),
		GRAVEL("minecraft", "textures/blocks/gravel.png"),
		SAND("minecraft", "textures/blocks/sand.png"),
		RED_SAND("minecraft", "textures/blocks/red_sand.png"),
		DIRT("minecraft", "textures/blocks/dirt.png"),
		SANDSTONE("minecraft", "textures/blocks/sandstone_bottom.png"),
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
		public BufferedImage img;
		public BlendMode mode;
		public CompositeStep(BufferedImage img, BlendMode mode) {
			this.img = img;
			this.mode = mode;
		}
		
	}
	
	private static final String PATH = "textures/composite/";
	
	private SimpleReloadableResourceManager rm;
	
	private List<Task> tasks = Lists.newArrayList();
	private Map<BlockBackdrop, BufferedImage> backdrops = Maps.newEnumMap(BlockBackdrop.class);
	private Map<CompositeType, List<CompositeStep>> types = Maps.newHashMap();
	
	private Map<String, byte[]> results = Maps.newHashMap();
	
	public TextureCompositor(SimpleReloadableResourceManager rm) {
		this.rm = rm;
	}

	public void load() {
		try {
			loadBackdrops();
			loadSteps();
		} catch (Exception e) {
			Lanthanoid.log.error("Failed to load backdrops and steps", e);
		}
	}
	
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
					if ("true".equals(System.getProperty("com.unascribed.lanthanoid.DebugCompositor"))) {
						ImageIO.write(buffer(img.getScaledInstance(
								Integer.parseInt(System.getProperty("com.unascribed.lanthanoid.DebugCompositorWidth")),
								Integer.parseInt(System.getProperty("com.unascribed.lanthanoid.DebugCompositorHeight")), Image.SCALE_FAST)), "PNG",
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
			w = Math.max(step.img.getWidth(), w);
			h = Math.max(step.img.getHeight(), h);
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
					draw = step.img;
					break;
				case COLORIZE:
					draw = copy(step.img);
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
					draw = buffer(step.img.getScaledInstance(w, h, Image.SCALE_FAST));
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
			g2d.drawImage(draw, 0, 0, w, h, null);
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
		for (BlockBackdrop b : BlockBackdrop.values()) {
			if (b.loc == null) continue;
			backdrops.put(b, cropToWidth(readImage(b.loc)));
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

	private BufferedImage readImage(ResourceLocation loc) throws IOException {
		InputStream in = rm.getResource(loc).getInputStream();
		BufferedImage img = ImageIO.read(in);
		return img;
	}

	private void loadSteps() throws IOException {
		loadFiveStep(BlockType.METAL);
		loadFiveStep(BlockType.GEM);
		loadFiveStep(BlockType.SQUARE);
		loadSingleStep(BlockType.LUMPY);
		loadSingleStep(BlockType.ROUGH);
		loadFiveStep(BlockType.TRACE);
		loadSingleStep(BlockType.CRYSTAL);
		
		loadTwoStep(ItemType.WAFER);
		loadTwoStep(ItemType.INGOT);
		loadTwoStep(ItemType.DUST);
	}


	private void loadTwoStep(CompositeType type) throws IOException {
		List<CompositeStep> li = Lists.newArrayList();
		String name = type.name().toLowerCase();
		li.add(new CompositeStep(readImage(new ResourceLocation("lanthanoid", PATH+name+"_basis.png")), BlendMode.COLORIZE));
		li.add(new CompositeStep(readImage(new ResourceLocation("lanthanoid", PATH+name+"_glint.png")), BlendMode.OVERLAY));
		types.put(type, li);
	}

	private void loadSingleStep(CompositeType type) throws IOException {
		String name = type.name().toLowerCase();
		types.put(type, Lists.newArrayList(new CompositeStep(readImage(new ResourceLocation("lanthanoid", PATH+name+".png")), BlendMode.COLORIZE)));
	}

	private void loadFiveStep(CompositeType type) throws IOException {
		List<CompositeStep> li = Lists.newArrayList();
		String name = type.name().toLowerCase();
		li.add(new CompositeStep(readImage(new ResourceLocation("lanthanoid", PATH+name+"_basis.png")), BlendMode.COLORIZE));
		li.add(new CompositeStep(readImage(new ResourceLocation("lanthanoid", PATH+name+"_bevel.png")), BlendMode.NORMAL));
		li.add(new CompositeStep(readImage(new ResourceLocation("lanthanoid", PATH+name+"_shine.png")), BlendMode.NORMAL));
		li.add(new CompositeStep(readImage(new ResourceLocation("lanthanoid", PATH+name+"_glint.png")), BlendMode.OVERLAY));
		li.add(new CompositeStep(readImage(new ResourceLocation("lanthanoid", PATH+name+"_shade.png")), BlendMode.NORMAL));
		types.put(type, li);
	}


	public void addBlock(String name, int color, BlockType type, BlockBackdrop backdrop) {
		Task o = new Task();
		o.name = "blocks/"+name;
		o.color = color;
		o.steps = Lists.newArrayList();
		if (backdrop.loc != null) {
			o.steps.add(new CompositeStep(backdrops.get(backdrop), BlendMode.NORMAL));
		}
		o.steps.addAll(types.get(type));
		tasks.add(o);
	}
	
	public void addItem(String name, int color, ItemType type) {
		Task o = new Task();
		o.name = "items/"+name;
		o.color = color;
		o.steps = Lists.newArrayList();
		o.steps.addAll(types.get(type));
		tasks.add(o);
	}

	private boolean isResultName(String name) {
		String str = "assets/lanthanoid_compositor/textures/";
		return name != null && name.startsWith(str) && name.endsWith(".png");
	}
	
	private String nameToResultName(String name) {
		String str = "assets/lanthanoid_compositor/textures/";
		return name.substring(str.length(), name.length()-4);
	}
	
	@Override
	public Set getResourceDomains() {
		return ImmutableSet.of("lanthanoid_compositor");
	}

	protected InputStream getInputStreamByName(String name) throws IOException {
		return hasResourceName(name) ? new ByteArrayInputStream(results.get(nameToResultName(name))) : null;
	}

	protected boolean hasResourceName(String name) {
		return isResultName(name) && results.containsKey(nameToResultName(name));
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
		return ImageIO.read(this.getInputStreamByName("pack.png"));
	}
	
	
}
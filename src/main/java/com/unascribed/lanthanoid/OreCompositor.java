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
import net.minecraft.client.resources.AbstractResourcePack;
import net.minecraft.client.resources.SimpleReloadableResourceManager;
import net.minecraft.util.ResourceLocation;

public class OreCompositor extends AbstractResourcePack {

	public enum Mode {
		NORMAL,
		SCREEN,
		COLORIZE
	}

	public enum Type {
		METAL,
		GEM
	}
	
	public enum Backdrop {
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
		Backdrop(String domain, String path) {
			loc = new ResourceLocation(domain, path);
		}
	}
	
	private static class Ore {
		public String name;
		public int color;
		public Type type;
		public Backdrop backdrop;
	}
	
	private static class CompositeStep {
		public BufferedImage img;
		public Mode mode;
		public CompositeStep(BufferedImage img, Mode mode) {
			this.img = img;
			this.mode = mode;
		}
		
	}
	
	private SimpleReloadableResourceManager rm;
	
	private List<Ore> ores = Lists.newArrayList();
	private Map<Backdrop, BufferedImage> backdrops = Maps.newEnumMap(Backdrop.class);
	private Map<Type, List<CompositeStep>> types = Maps.newEnumMap(Type.class);
	
	private Map<String, byte[]> results = Maps.newHashMap();
	
	public OreCompositor(SimpleReloadableResourceManager rm) {
		super(null);
		this.rm = rm;
	}

	public void generate() {
		try {
			loadBackdrops();
			loadSteps();
			ByteArrayOutputStream baos = new ByteArrayOutputStream(16*16*4);
			for (Ore o : ores) {
				BufferedImage img = doComposite(o.backdrop, o.type, o.color);
				baos.reset();
				ImageIO.write(img, "PNG", baos);
				results.put(o.name, baos.toByteArray());
				if ("true".equals(System.getProperty("com.unascribed.lanthanoid.DebugCompositor"))) {
					ImageIO.write(buffer(img.getScaledInstance(
							Integer.parseInt(System.getProperty("com.unascribed.lanthanoid.DebugCompositorWidth")),
							Integer.parseInt(System.getProperty("com.unascribed.lanthanoid.DebugCompositorHeight")), Image.SCALE_FAST)), "PNG", new File("lanthanoid-compositor-"+o.name+".png"));
				}
			}
			rm.reloadResourcePack(this);
		} catch (Exception e) {
			Lanthanoid.log.error("Failed to composite ore textures", e);
		}
	}
	
	private BufferedImage doComposite(Backdrop backdrop, Type type, int color) {
		BufferedImage img = copy(backdrops.get(backdrop));
		float inR = ((color >> 16) & 0xFF) / 255f;
		float inG = ((color >> 8) & 0xFF) / 255f;
		float inB = (color & 0xFF) / 255f;
		Graphics2D g2d = img.createGraphics();
		int w = img.getWidth();
		int h = img.getHeight();
		for (CompositeStep step : types.get(type)) {
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
				case SCREEN:
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
							
							float r = 1 - ((1-srcR)*(1-dstR));
							float g = 1 - ((1-srcG)*(1-dstG));
							float b = 1 - ((1-srcB)*(1-dstB));
							
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
		img.copyData(out.getRaster());
		return out;
	}

	private void loadBackdrops() throws IOException {
		for (Backdrop b : Backdrop.values()) {
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
		loadStandardSteps(Type.METAL);
		loadStandardSteps(Type.GEM);
	}


	private void loadStandardSteps(Type type) throws IOException {
		List<CompositeStep> li = Lists.newArrayList();
		li.add(new CompositeStep(readImage(new ResourceLocation("lanthanoid", "textures/composite/ore_"+type.name().toLowerCase()+"_basis.png")), Mode.COLORIZE));
		li.add(new CompositeStep(readImage(new ResourceLocation("lanthanoid", "textures/composite/ore_"+type.name().toLowerCase()+"_bevel.png")), Mode.NORMAL));
		li.add(new CompositeStep(readImage(new ResourceLocation("lanthanoid", "textures/composite/ore_"+type.name().toLowerCase()+"_shine.png")), Mode.NORMAL));
		li.add(new CompositeStep(readImage(new ResourceLocation("lanthanoid", "textures/composite/ore_"+type.name().toLowerCase()+"_glint.png")), Mode.SCREEN));
		li.add(new CompositeStep(readImage(new ResourceLocation("lanthanoid", "textures/composite/ore_"+type.name().toLowerCase()+"_shade.png")), Mode.NORMAL));
		types.put(type, li);
	}


	public void addOre(String name, int color, Type type, Backdrop backdrop) {
		Ore o = new Ore();
		o.name = name;
		o.color = color;
		o.type = type;
		o.backdrop = backdrop;
		ores.add(o);
	}

	private boolean isResultName(String name) {
		String str = "assets/lanthanoid_compositor/textures/blocks/";
		return name != null && name.startsWith(str) && name.endsWith(".png");
	}
	
	private String nameToResultName(String name) {
		String str = "assets/lanthanoid_compositor/textures/blocks/";
		return name.substring(str.length(), name.length()-4);
	}
	
	@Override
	public Set getResourceDomains() {
		return ImmutableSet.of("lanthanoid_compositor");
	}

	@Override
	protected InputStream getInputStreamByName(String name) throws IOException {
		return hasResourceName(name) ? new ByteArrayInputStream(results.get(nameToResultName(name))) : null;
	}

	@Override
	protected boolean hasResourceName(String name) {
		return isResultName(name) && results.containsKey(nameToResultName(name));
	}

	@Override
	public String getPackName() {
		return "Lanthanoid Ore Texture Compositor";
	}
	
}
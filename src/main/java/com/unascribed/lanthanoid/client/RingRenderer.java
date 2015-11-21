package com.unascribed.lanthanoid.client;

import java.util.Map;

import org.lwjgl.opengl.GL11;
import com.google.common.base.Function;
import com.unascribed.lanthanoid.init.LItems;
import com.unascribed.lanthanoid.item.rifle.Mode;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityClientPlayerMP;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MathHelper;
import net.minecraft.util.StatCollector;

public class RingRenderer {

	public static final int ANIMATION_TIME = 4;
	
	public static RenderItem itemRenderer = new RenderItem();
	
	private String[] keys = {
			"1",
			"2",
			"3",
			"4",
			"5",
			"6",
			"7",
			"8",
			"9",
			"0",
			"-",
			"="
		};
	
	public static final double TAU = Math.PI*2;
	public static final float TAUf = (float)TAU;
	
	private final Mode[] values;
	private final Function<ItemStack, ? extends Mode> modeGetter;
	private final Function<ItemStack, Integer> bufferGetter;
	private int prevSelected, lastSelected, animTicks, diff;
	
	private final Minecraft mc = Minecraft.getMinecraft();
	private final Map<Mode, Integer> counts;
	private final Map<Integer, ItemStack> oreStacks;
	
	private boolean flipped;
	
	public RingRenderer(Function<ItemStack, ? extends Mode> modeGetter, Function<ItemStack, Integer> bufferGetter, Mode[] values, Map<Mode, Integer> counts, Map<Integer, ItemStack> oreStacks) {
		this.counts = counts;
		this.oreStacks = oreStacks;
		this.modeGetter = modeGetter;
		this.bufferGetter = bufferGetter;
		this.values = values;
	}

	public RingRenderer flip() {
		this.flipped = true;
		return this;
	}
	
	public void tick() {
		animTicks++;
	}
	
	public void render(EntityClientPlayerMP p, ItemStack stack, int oX, int oY, float partialTicks) {
		Mode selected = modeGetter.apply(stack);
		if (lastSelected == -1) {
			lastSelected = selected.ordinal();
		}
		if (lastSelected != selected.ordinal()) {
			int distA = selected.ordinal() - lastSelected;
			int distB = selected.ordinal() - (lastSelected+10);
			int distC = (lastSelected - (selected.ordinal()+10))*-1;
			int dist;
			if (Math.abs(distC) < Math.abs(distA) && Math.abs(distC) < Math.abs(distB)) {
				dist = distC;
			} else if (Math.abs(distB) < Math.abs(distA) && Math.abs(distB) < Math.abs(distC)) {
				dist = distB;
			} else if (Math.abs(distA) < Math.abs(distB) && Math.abs(distA) < Math.abs(distC)) {
				dist = distA;
			} else {
				dist = distA;
			}
			if (animTicks >= ANIMATION_TIME) {
				animTicks = 0;
				diff = dist;
			} else {
				diff += dist;
			}
			prevSelected = lastSelected;
			lastSelected = selected.ordinal();
		}
		boolean renderedAnything = false;
		for (Mode m : values) {
			if (LItems.rifle.hasAmmoFor(p, stack, m)) {
				float s = (m.ordinal()-selected.ordinal())+(flipped ? -2 : 2);
				float anim = 0;
				if (animTicks < ANIMATION_TIME) {
					anim = 1-((animTicks+partialTicks)/ANIMATION_TIME);
				}
				s += anim*diff;
				int x = oX;
				int y = oY;
				x += Math.round(MathHelper.sin((s / 10)*TAUf)*34f);
				y+= Math.round(MathHelper.cos((s / 10)*TAUf)*34f);
				x += (flipped ? -8 : 8);
				y += 10;
				GL11.glPushMatrix();
				GL11.glTranslatef(x, y, 0);
				float d = 0;
				if (m == selected) {
					String str = StatCollector.translateToLocal("mode."+m.translationKey+".name");
					int nx;
					if (flipped) {
						nx = -(mc.fontRendererObj.getStringWidth(str)+12);
					} else {
						nx = 24;
					}
					mc.fontRendererObj.drawStringWithShadow(str, nx, 8, m.color);
					d = (1-anim);
				} else if (m.ordinal() == prevSelected) {
					d = anim;
				}
				GL11.glTranslatef(d*-8, d*-8, 0);
				GL11.glScalef(d+1, d+1, 1.0f);
				ItemStack modeStack = null;
				if (m.type instanceof ItemStack) {
					modeStack = (ItemStack)m.type;
				} else if (m.type instanceof Integer) {
					modeStack = oreStacks.get(m.type);
				}
				if (modeStack != null) {
					if (m == selected) {
						GL11.glColor4f(1, 1, 1, 1);
					} else {
						GL11.glColor4f(1, 1, 1, 0.25f);
					}
					mc.renderEngine.bindTexture(modeStack.getItemSpriteNumber() == 0 ? TextureMap.locationBlocksTexture : TextureMap.locationItemsTexture);
					itemRenderer.renderIcon(0, 0, modeStack.getIconIndex(), 16, 16);
				} else {
					mc.fontRendererObj.drawStringWithShadow("/", 4, 4, m.color);
				}
				GL11.glScalef(0.5f, 0.5f, 1f);
				GL11.glTranslatef(0, 0, 100);
				mc.fontRendererObj.drawStringWithShadow(keys[m.ordinal()], 4, 4, -1);
				int count = counts.get(m)*(m.doesBuffer() ? LItems.rifle.getVariant(stack).getAmmoPerDust() : 1);
				if (m == selected) {
					count += bufferGetter.apply(stack);
				}
				boolean infinite = mc.thePlayer.capabilities.isCreativeMode || m.type == null;
				String str = infinite ? "∞" : Integer.toString(count);
				mc.fontRendererObj.drawStringWithShadow(str, 30-(mc.fontRendererObj.getStringWidth(str)), 20, -1);
				GL11.glPopMatrix();
				renderedAnything = true;
			}
		}
		if (!renderedAnything) {
			mc.fontRendererObj.drawStringWithShadow(StatCollector.translateToLocal("ui.no_ammo_hint"), 4, 4, -1);
		} else {
			GL11.glPushMatrix();
			GL11.glScalef(1.5f, 1.5f, 0);
			GL11.glPopMatrix();
		}
	}

}

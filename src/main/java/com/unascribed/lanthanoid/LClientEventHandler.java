package com.unascribed.lanthanoid;

import java.util.Map;

import org.apache.commons.lang3.SystemUtils;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import com.google.common.collect.Maps;
import com.unascribed.lanthanoid.item.ItemRifle;
import com.unascribed.lanthanoid.network.ModifyRifleModeMessage;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.InputEvent.KeyInputEvent;
import cpw.mods.fml.common.gameevent.InputEvent.MouseInputEvent;
import cpw.mods.fml.common.gameevent.TickEvent.ClientTickEvent;
import cpw.mods.fml.common.gameevent.TickEvent.Phase;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityClientPlayerMP;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.item.ItemStack;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StatCollector;
import net.minecraftforge.client.event.FOVUpdateEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderHandEvent;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;

@SideOnly(Side.CLIENT)
public class LClientEventHandler {
	@SideOnly(Side.CLIENT)
	private RenderItem itemRenderer = new RenderItem();
	
	public static final float TAUf = (float)(Math.PI*2f);
	
	private String[] keys = {
		"~",
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
	
	public static final int ANIMATION_TIME = 4;
	
	private static final ResourceLocation SCOPE_TEX = new ResourceLocation("lanthanoid", "textures/misc/scope.png");
	private static final ResourceLocation WIDGITS = new ResourceLocation("textures/gui/widgets.png");
	
	private int animTicks = 0;
	private int lastSelected = -1;
	private int prevSelected = -1;
	private int diff;
	
	public static int scopeFactor = 1;
	private Map<ItemRifle.Mode, Integer> counts = Maps.newEnumMap(ItemRifle.Mode.class);
	
	@SubscribeEvent
	public void onRenderHand(RenderHandEvent e) {
		if (scopeFactor > 1) {
			e.setCanceled(true);
		}
	}
	
	@SubscribeEvent
	public void onFOV(FOVUpdateEvent e) {
		if (scopeFactor > 1) {
			e.newfov /= scopeFactor;
		} else if (scopeFactor == 0) {
			e.newfov *= 0.75f;
		}
	}
	
	@SubscribeEvent
	public void onClientTick(ClientTickEvent e) {
		if (e.phase == Phase.START) {
			//Minecraft mc = Minecraft.getMinecraft();
			/*if (ticks % 20 == 0 || counts.isEmpty()) {
				if (mc.thePlayer != null) {
					counts.clear();
					for (ItemRifle.Mode mode : ItemRifle.Mode.values()) {
						counts.put(mode, 0);
					}
					blazeCount = 0;
					for (ItemStack is : mc.thePlayer.inventory.mainInventory) {
						if (is == null) continue;
						for (ItemRifle.Mode mode : ItemRifle.Mode.values()) {
							if (mode.stack.isItemEqual(is)) {
								counts.put(mode, counts.get(mode)+is.stackSize);
							}
						}
						if (is.getItem() == Items.blaze_powder) {
							blazeCount += is.stackSize;
						}
					}
				}
			}*/
		}
		if (e.phase == Phase.END) {
			animTicks++;
		}
	}
	
	@SubscribeEvent
	public void onPreRenderGameOverlay(RenderGameOverlayEvent.Pre e) {
		if (e.type == ElementType.HELMET) {
			Minecraft mc = Minecraft.getMinecraft();
			if (scopeFactor > 1) {
				GL11.glPushMatrix();
				float realWidth = e.resolution.getScaledHeight();
				GL11.glScalef(realWidth/256, (e.resolution.getScaledHeight())/256f, 1);
				mc.renderEngine.bindTexture(SCOPE_TEX);
				float f = 0.00390625F;
				float f1 = 0.00390625F;
				double y = 0;
				double height = 256;
				double width = 256;
				double realX = ((e.resolution.getScaledWidth_double()/2)-(realWidth/2));
				double x = realX/(realWidth/256);
				double u = 0;
				double v = 0;
				double z = -300;
				Tessellator tessellator = Tessellator.instance;
				tessellator.startDrawingQuads();
				tessellator.addVertexWithUV(x + 0, y + height, z, (u + 0) * f,(v + height) * f1);
				tessellator.addVertexWithUV(x + width, y + height, z, (u + width) * f, (v + height) * f1);
				tessellator.addVertexWithUV(x + width, y + 0, z, (u + width) * f, (v + 0) * f1);
				tessellator.addVertexWithUV(x + 0, y + 0, z, (u + 0) * f, (v + 0) * f1);
				tessellator.draw();
				GL11.glPopMatrix();
				Gui.drawRect(0, 0, (int)Math.ceil(realX), e.resolution.getScaledHeight(), 0xFF000000);
				Gui.drawRect((int)Math.floor(realX+realWidth), 0, e.resolution.getScaledWidth(), e.resolution.getScaledHeight(), 0xFF000000);
				GL11.glPushMatrix();
				GL11.glScalef(2, 2, 1);
				boolean oldUnicode = mc.fontRenderer.getUnicodeFlag();
				mc.fontRenderer.setUnicodeFlag(true);
				mc.fontRenderer.drawString(scopeFactor+"x", (e.resolution.getScaledWidth()/4)+20, (e.resolution.getScaledHeight()/4)+20, 0xC67226);
				mc.fontRenderer.setUnicodeFlag(oldUnicode);
				GL11.glPopMatrix();
				mc.renderEngine.bindTexture(WIDGITS);
			}
		} else if (e.type == ElementType.CROSSHAIRS) {
			if (scopeFactor != 1) {
				e.setCanceled(true);
			}
		}
	}
	
	@SubscribeEvent
	public void onPostRenderGameOverlay(RenderGameOverlayEvent.Post e) {
		if (e.type == ElementType.ALL) {
			Minecraft mc = Minecraft.getMinecraft();
			EntityClientPlayerMP p = mc.thePlayer;
			if (p.getHeldItem() != null && p.getHeldItem().getItem() == LItems.rifle) {
				counts.clear();
				for (ItemRifle.Mode mode : ItemRifle.Mode.values()) {
					counts.put(mode, 0);
				}
				//int blazeCount = 0;
				for (ItemStack is : mc.thePlayer.inventory.mainInventory) {
					if (is == null) continue;
					for (ItemRifle.Mode mode : ItemRifle.Mode.values()) {
						if (mode.stack.isItemEqual(is)) {
							counts.put(mode, counts.get(mode)+is.stackSize);
						}
					}
					/*if (is.getItem() == Items.blaze_powder) {
						blazeCount += is.stackSize;
					}*/
				}
				ItemStack stack = p.getHeldItem();
				ItemRifle.Mode selected = LItems.rifle.getMode(stack);
				ItemRifle.Mode[] vals = ItemRifle.Mode.values();
				if (lastSelected == -1) {
					lastSelected = selected.ordinal();
				}
				float i = 0;
				if (lastSelected != selected.ordinal()) {
					int distA = selected.ordinal() - lastSelected;
					int distB = selected.ordinal() - (lastSelected+vals.length);
					int distC = (lastSelected - (selected.ordinal()+vals.length))*-1;
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
				GL11.glEnable(GL12.GL_RESCALE_NORMAL);
				RenderHelper.enableGUIStandardItemLighting();
				boolean renderedAnything = false;
				for (ItemRifle.Mode m : vals) {
					if (LItems.rifle.hasAmmoFor(p, stack, m)) {
						float s = (i-selected.ordinal())+2;
						float anim = 0;
						if (animTicks < ANIMATION_TIME) {
							anim = 1-((animTicks+e.partialTicks)/(float)ANIMATION_TIME);
						}
						s += anim*diff;
						int x = Math.round(MathHelper.sin((s / vals.length)*TAUf)*38f);
						int y = Math.round(MathHelper.cos((s / vals.length)*TAUf)*38f);
						x += 8;
						y += 10;
						GL11.glPushMatrix();
						GL11.glTranslatef(x, y, 0);
						float d = 0;
						if (m == selected) {
							mc.fontRenderer.drawStringWithShadow(StatCollector.translateToLocal("mode."+m.translationKey+".name"), 24, 8, m.color);
							d = (1-anim);
						} else if (m.ordinal() == prevSelected) {
							d = anim;
						}
						GL11.glTranslatef(d*-8, d*-8, 0);
						GL11.glScalef(d+1, d+1, 1.0f);
						itemRenderer.renderItemAndEffectIntoGUI(mc.fontRenderer, mc.getTextureManager(), m.stack, 0, 0);
						GL11.glScalef(0.5f, 0.5f, 1f);
						GL11.glTranslatef(0, 0, 100);
						mc.fontRenderer.drawStringWithShadow(keys[(int)i], 4, 4, -1);
						int count = LItems.rifle.getBufferedShots(stack)+(counts.get(m)*LItems.rifle.getAttachment(stack).getAmmoPerDust());
						String str = Integer.toString(count);
						mc.fontRenderer.drawStringWithShadow(mc.thePlayer.capabilities.isCreativeMode ? "∞" : str, 30-(mc.fontRenderer.getStringWidth(str)), 20, -1);
						GL11.glPopMatrix();
						renderedAnything = true;
					}
					i++;
				}
				if (!renderedAnything) {
					mc.fontRenderer.drawStringWithShadow(StatCollector.translateToLocal("ui.no_ammo_hint"), 4, 4, -1);
				} else {
					GL11.glPushMatrix();
					GL11.glScalef(1.5f, 1.5f, 0);
					GL11.glPopMatrix();
				}
				RenderHelper.disableStandardItemLighting();
				GL11.glDisable(GL12.GL_RESCALE_NORMAL);
			}
		}
	}
	
	private boolean linuxNag = true;
	
	@SubscribeEvent
	public void onPostRender(RenderWorldLastEvent e) {
		if (RenderManager.debugBoundingBox && ItemRifle.latestAABB != null) {
			GL11.glDepthMask(false);
			GL11.glDisable(GL11.GL_TEXTURE_2D);
			GL11.glDisable(GL11.GL_LIGHTING);
			GL11.glDisable(GL11.GL_CULL_FACE);
			GL11.glDisable(GL11.GL_BLEND);
			AxisAlignedBB axisalignedbb = ItemRifle.latestAABB;
			RenderGlobal.drawOutlinedBoundingBox(axisalignedbb, 16777215);
			GL11.glEnable(GL11.GL_TEXTURE_2D);
			GL11.glEnable(GL11.GL_LIGHTING);
			GL11.glEnable(GL11.GL_CULL_FACE);
			GL11.glDisable(GL11.GL_BLEND);
			GL11.glDepthMask(true);
		}
	}
	
	@SubscribeEvent
	public void onKeyboardInput(KeyInputEvent e) {
		Minecraft mc = Minecraft.getMinecraft();
		if (mc.thePlayer != null) {
			if (mc.thePlayer.isSneaking()) {
				if (mc.thePlayer.getHeldItem() != null) {
					ItemStack held = mc.thePlayer.getHeldItem();
					if (held.getItem() == LItems.rifle) {
						// on Linux, Shift+2 and Shift+6 do not work. This is an LWJGL bug.
						// This is a QWERTY-only workaround.
						if (SystemUtils.IS_OS_LINUX) {
							if (linuxNag) {
								Lanthanoid.log.warn("We are running on Linux. Due to a bug in LWJGL, Shift+2 and Shift+6 do not work "+
											"properly. Activating workaround. This may cause strange issues and is only "+
											"confirmed to work with QWERTY keyboards. This message is only shown once.");
								linuxNag = false;
							}
							if (Keyboard.getEventCharacter() == '@') {
								while (mc.gameSettings.keyBindsHotbar[1].isPressed()) {}
								Lanthanoid.inst.network.sendToServer(new ModifyRifleModeMessage(true, 2));
								return;
							}
							if (Keyboard.getEventCharacter() == '^') {
								while (mc.gameSettings.keyBindsHotbar[5].isPressed()) {}
								Lanthanoid.inst.network.sendToServer(new ModifyRifleModeMessage(true, 6));
								return;
							}
						}
						if (Keyboard.getEventKey() == Keyboard.KEY_0) {
							Lanthanoid.inst.network.sendToServer(new ModifyRifleModeMessage(true, 10));
							return;
						}
						if (Keyboard.getEventKey() == Keyboard.KEY_UNDERLINE) {
							Lanthanoid.inst.network.sendToServer(new ModifyRifleModeMessage(true, 11));
							return;
						}
						if (Keyboard.getEventKey() == Keyboard.KEY_EQUALS) {
							Lanthanoid.inst.network.sendToServer(new ModifyRifleModeMessage(true, 12));
							return;
						}
						if (Keyboard.getEventKey() == Keyboard.KEY_GRAVE) {
							Lanthanoid.inst.network.sendToServer(new ModifyRifleModeMessage(true, 0));
							return;
						}
						for (int i = 0; i < 9; i++) {
							if (mc.gameSettings.keyBindsHotbar[i].isPressed()) {
								while (mc.gameSettings.keyBindsHotbar[i].isPressed()) {} // drain pressTicks to zero to suppress vanilla behavior
								Lanthanoid.inst.network.sendToServer(new ModifyRifleModeMessage(true, i+1));
							}
						}
						return;
					}
				}
			}
		}
	}
	@SubscribeEvent
	public void onMouseInput(MouseInputEvent e) {
		Minecraft mc = Minecraft.getMinecraft();
		if (mc.thePlayer != null) {
			int dWheel = Mouse.getEventDWheel();
			mc.thePlayer.inventory.changeCurrentItem(dWheel*-1);
			if (dWheel != 0) {
				if (mc.thePlayer.isSneaking()) {
					if (mc.thePlayer.getHeldItem() != null) {
						ItemStack held = mc.thePlayer.getHeldItem();
						if (held.getItem() == LItems.rifle) {
							if (dWheel > 0) {
								dWheel = 1;
							}
							if (dWheel < 0) {
								dWheel = -1;
							}
							Lanthanoid.inst.network.sendToServer(new ModifyRifleModeMessage(false, dWheel*-1));
							return;
						}
					}
				}
			}
			mc.thePlayer.inventory.changeCurrentItem(dWheel);
		}
	}
}

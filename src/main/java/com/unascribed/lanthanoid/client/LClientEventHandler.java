package com.unascribed.lanthanoid.client;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import org.apache.commons.lang3.SystemUtils;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.unascribed.lanthanoid.Lanthanoid;
import com.unascribed.lanthanoid.init.LItems;
import com.unascribed.lanthanoid.item.rifle.ItemRifle;
import com.unascribed.lanthanoid.item.rifle.Mode;
import com.unascribed.lanthanoid.item.rifle.PrimaryMode;
import com.unascribed.lanthanoid.item.rifle.SecondaryMode;
import com.unascribed.lanthanoid.network.ModifyRifleModeMessage;
import com.unascribed.lanthanoid.network.ToggleRifleBlazeModeMessage;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.InputEvent.KeyInputEvent;
import cpw.mods.fml.common.gameevent.InputEvent.MouseInputEvent;
import cpw.mods.fml.common.gameevent.TickEvent.ClientTickEvent;
import cpw.mods.fml.common.gameevent.TickEvent.Phase;
import cpw.mods.fml.common.gameevent.TickEvent.RenderTickEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityClientPlayerMP;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.entity.Entity;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StatCollector;
import net.minecraft.util.Vec3;
import net.minecraftforge.client.event.EntityViewRenderEvent;
import net.minecraftforge.client.event.FOVUpdateEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderHandEvent;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;
import net.minecraftforge.oredict.OreDictionary;

@SideOnly(Side.CLIENT)
public class LClientEventHandler {
	public static class SkyFlash {
		public SkyFlash(Vec3 color) {
			this.color = color;
		}
		public int ticks;
		public Vec3 color;
	}
	
	private static final ResourceLocation SCOPE_TEX = new ResourceLocation("lanthanoid", "textures/misc/scope.png");
	private static final ResourceLocation WIDGITS = new ResourceLocation("textures/gui/widgets.png");
	
	private RingRenderer primary;
	private RingRenderer secondary;
	
	private int ticks = 0;
	
	public static List<SkyFlash> flashes = Lists.newArrayList();
	
	public static int scopeFactor = 1;
	public static LClientEventHandler inst;
	private Map<Mode, Integer> counts = Maps.newHashMap();
	private Map<Integer, ItemStack> oreStacks = Maps.newHashMap();
	
	private boolean lastBlaze = false;
	private int blazeTicks = 0;
	
	public LClientEventHandler() {
		inst = this;
	}
	
	public void init() {
		primary = new RingRenderer(LItems.rifle::getPrimaryMode, LItems.rifle::getBufferedPrimaryShots, PrimaryMode.values(), counts, oreStacks);
		secondary = new RingRenderer(LItems.rifle::getSecondaryMode, LItems.rifle::getBufferedSecondaryShots, SecondaryMode.values(), counts, oreStacks).flip();
	}
	
	public void onSkyColor(Vec3 color, Entity entity, float partialTicks) {
		for (SkyFlash sf : flashes) {
			
		}
	}
	
	@SubscribeEvent
	public void onFogColor(EntityViewRenderEvent.FogColors e) {
		
	}
	
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
	
	private Pattern chunkUpdates = Pattern.compile(", [0-9]+ chunk updates");
	
	@SubscribeEvent
	public void onDebugText(RenderGameOverlayEvent.Text e) {
		if (!Minecraft.getMinecraft().thePlayer.capabilities.isCreativeMode && Minecraft.getMinecraft().gameSettings.showDebugInfo) {
			int s = e.left.size();
			for (int i = 1; i < s; i++) {
				e.left.remove(1);
			}
			e.left.set(0, chunkUpdates.matcher(e.left.get(0)).replaceAll(""));
			e.left.add("");
			e.left.add("Enter creative for full debug menu");
		}
	}
	
	@SubscribeEvent
	public void onPreRender(RenderTickEvent e) {
		if (e.phase == Phase.START) {
			Minecraft mc = Minecraft.getMinecraft();
			if (mc.thePlayer != null) {
				if (!mc.thePlayer.capabilities.isCreativeMode) {
					RenderManager.debugBoundingBox = false;
				}
			}
		}
	}
	
	@SubscribeEvent
	public void onClientTick(ClientTickEvent e) {
		if (e.phase == Phase.START) {
			Minecraft mc = Minecraft.getMinecraft();
			if (mc.thePlayer != null) {
				if (ticks % 20 == 0 || oreStacks.isEmpty()) {
					oreStacks.clear();
					for (ItemStack is : mc.thePlayer.inventory.mainInventory) {
						if (is == null) continue;
						int[] ids = OreDictionary.getOreIDs(is);
						for (int id : ids) {
							if (PrimaryMode.usedOreIDs.contains(id) || SecondaryMode.usedOreIDs.contains(id) && !oreStacks.containsKey(id)) {
								ItemStack stack = is.copy();
								stack.stackSize = 1;
								oreStacks.put(id, stack);
							}
						}
					}
					for (int id : Sets.union(PrimaryMode.usedOreIDs, SecondaryMode.usedOreIDs)) {
						if (!oreStacks.containsKey(id)) {
							@SuppressWarnings("deprecation")
							List<ItemStack> is = OreDictionary.getOres(id);
							oreStacks.put(id, is.get(0));
						}
					}
				}
				ticks++;
			} else {
				ticks = 0;
			}
		}
		if (e.phase == Phase.END) {
			primary.tick();
			secondary.tick();
			blazeTicks++;
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
	
	PrimaryMode[] primaryVals = PrimaryMode.values();
	SecondaryMode[] secondaryVals = SecondaryMode.values();
	Mode[] allVals = union(primaryVals, secondaryVals, new Mode[primaryVals.length+secondaryVals.length]);
	
	@SubscribeEvent
	public void onPostRenderGameOverlay(RenderGameOverlayEvent.Post e) {
		if (e.type == ElementType.ALL) {
			Minecraft mc = Minecraft.getMinecraft();
			EntityClientPlayerMP p = mc.thePlayer;
			if (p.getHeldItem() != null && p.getHeldItem().getItem() == LItems.rifle) {
				ItemStack stack = p.getHeldItem();
				
				int blazeCount = 0;
				counts.clear();
				for (Mode mode : allVals) {
					counts.put(mode, 0);
				}
				for (ItemStack is : mc.thePlayer.inventory.mainInventory) {
					if (is == null) continue;
					for (Mode mode : allVals) {
						if (mode.stackMatches(is)) {
							counts.put(mode, counts.get(mode)+is.stackSize);
						}
					}
					if (is.getItem() == Items.blaze_powder) {
						blazeCount += is.stackSize;
					}
				}
				GL11.glEnable(GL12.GL_RESCALE_NORMAL);
				RenderHelper.enableGUIStandardItemLighting();
				boolean blaze = LItems.rifle.isBlazeEnabled(stack);
				if (lastBlaze != blaze) {
					blazeTicks = 0;
					lastBlaze = blaze;
				}
				GL11.glPushMatrix();
					GL11.glTranslatef(e.resolution.getScaledWidth()/2, 0, 0);
					float anim = (blazeTicks < RingRenderer.ANIMATION_TIME ? ((float)blazeTicks+e.partialTicks)/RingRenderer.ANIMATION_TIME : 1);
					if (blaze) {
						GL11.glScalef(1f+anim, 1f+anim, 1f);
					} else {
						GL11.glScalef(2f-anim, 2f-anim, 1f);
					}
					GL11.glTranslatef(-8, 0, 0);
					GL11.glPushMatrix();
						mc.renderEngine.bindTexture(TextureMap.locationItemsTexture);
						if (blaze) {
							GL11.glColor4f(1, 1, 1, 1);
						} else {
							GL11.glColor4f(1, 1, 1, 0.5f);	
						}
						RingRenderer.itemRenderer.renderIcon(0, 0, Items.blaze_powder.getIconFromDamage(0), 16, 16);
					GL11.glPopMatrix();
					GL11.glPushMatrix();
						GL11.glTranslatef(0, 2, 0);
						GL11.glScalef(0.5f, 0.5f, 1f);
						mc.fontRenderer.drawStringWithShadow("~", 0, 0, -1);
					GL11.glPopMatrix();
					GL11.glPushMatrix();
						boolean infinite = mc.thePlayer.capabilities.isCreativeMode;
						String str = infinite ? "∞" : Integer.toString(blazeCount);
						GL11.glTranslatef(20-(mc.fontRenderer.getStringWidth(str)), 12, 0);
						GL11.glScalef(0.5f, 0.5f, 1f);
						mc.fontRenderer.drawStringWithShadow(str, 0, 0, -1);
					GL11.glPopMatrix();
				GL11.glPopMatrix();
				if (blaze) {
					String blz = StatCollector.translateToLocal("mode.blaze.name");
					mc.fontRenderer.drawStringWithShadow(blz, (e.resolution.getScaledWidth()/2)-(mc.fontRenderer.getStringWidth(blz)/2), (int)(16+(16*anim)), 0xFFAA00);
				}
				primary.render(p, stack, 0, 0, e.partialTicks);
				secondary.render(p, stack, e.resolution.getScaledWidth(), 0, e.partialTicks);
				RenderHelper.disableStandardItemLighting();
				GL11.glDisable(GL12.GL_RESCALE_NORMAL);
			}
		}
	}
	
	private <T> T[] union(T[] a, T[] b, T[] result) {
		if (result.length != a.length+b.length) {
			result = Arrays.copyOf(result, a.length+b.length);
		}
		System.arraycopy(a, 0, result, 0, a.length);
		System.arraycopy(b, 0, result, a.length, b.length);
		return result;
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
			boolean primary = Keyboard.isKeyDown(Minecraft.getMinecraft().gameSettings.keyBindSneak.getKeyCode());
			boolean secondary = Keyboard.isKeyDown(Minecraft.getMinecraft().gameSettings.keyBindSprint.getKeyCode());
			if (primary || secondary) {
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
								Lanthanoid.inst.network.sendToServer(new ModifyRifleModeMessage(true, primary, 1));
								return;
							}
							if (Keyboard.getEventCharacter() == '^') {
								while (mc.gameSettings.keyBindsHotbar[5].isPressed()) {}
								Lanthanoid.inst.network.sendToServer(new ModifyRifleModeMessage(true, primary, 5));
								return;
							}
						}
						if (Keyboard.getEventKey() == Keyboard.KEY_0 && Keyboard.getEventKeyState()) {
							Lanthanoid.inst.network.sendToServer(new ModifyRifleModeMessage(true, primary, 9));
							return;
						}
						if (Keyboard.getEventKey() == Keyboard.KEY_UNDERLINE && Keyboard.getEventKeyState()) {
							Lanthanoid.inst.network.sendToServer(new ModifyRifleModeMessage(true, primary, 10));
							return;
						}
						if (Keyboard.getEventKey() == Keyboard.KEY_EQUALS && Keyboard.getEventKeyState()) {
							Lanthanoid.inst.network.sendToServer(new ModifyRifleModeMessage(true, primary, 11));
							return;
						}
						if (Keyboard.getEventKey() == Keyboard.KEY_GRAVE && Keyboard.getEventKeyState()) {
							Lanthanoid.inst.network.sendToServer(new ToggleRifleBlazeModeMessage());
							return;
						}
						for (int i = 0; i < 9; i++) {
							if (mc.gameSettings.keyBindsHotbar[i].isPressed()) {
								while (mc.gameSettings.keyBindsHotbar[i].isPressed()) {} // drain pressTicks to zero to suppress vanilla behavior
								Lanthanoid.inst.network.sendToServer(new ModifyRifleModeMessage(true, primary, i));
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
				boolean primary = Keyboard.isKeyDown(Minecraft.getMinecraft().gameSettings.keyBindSneak.getKeyCode());
				boolean secondary = Keyboard.isKeyDown(Minecraft.getMinecraft().gameSettings.keyBindSprint.getKeyCode());
				if (primary || secondary) {
					if (mc.thePlayer.getHeldItem() != null) {
						ItemStack held = mc.thePlayer.getHeldItem();
						if (held.getItem() == LItems.rifle) {
							if (dWheel > 0) {
								dWheel = 1;
							}
							if (dWheel < 0) {
								dWheel = -1;
							}
							Lanthanoid.inst.network.sendToServer(new ModifyRifleModeMessage(false, primary, dWheel*-1));
							return;
						}
					}
				}
			}
			mc.thePlayer.inventory.changeCurrentItem(dWheel);
		}
	}
}

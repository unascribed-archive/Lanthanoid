package com.unascribed.lanthanoid.client;

import java.util.Arrays;
import java.util.Calendar;
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
import com.unascribed.lanthanoid.waypoint.Waypoint;

import cpw.mods.fml.common.eventhandler.EventPriority;
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
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiIngame;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.entity.Entity;
import net.minecraft.entity.monster.EntityEnderman;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MathHelper;
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
	
	private static final String[] directions = {
		"SORTH",
		"NOTCH",
		"SALAD",
		"MANGO",
		"BEYOND",
		"WEAST",
		"WATERMELON",
		"APOAPSIS",
		"PERIAPSIS",
		"OVER",
		"UNDER",
		"ABOVE",
		"BELOW",
		"SOMEWHERE",
		"WAFFLE",
		"POTATO",
		"THISWAY",
		"SLANTWARDS",
		"THATWAY",
		"NEVER",
		"EAT",
		"SOGGY",
		"WHEAT",
	};
	
	@SubscribeEvent(priority=EventPriority.LOWEST)
	public void onDebugText(RenderGameOverlayEvent.Text e) {
		if (!Minecraft.getMinecraft().thePlayer.capabilities.isCreativeMode && Minecraft.getMinecraft().gameSettings.showDebugInfo) {
			int s = e.left.size();
			for (int i = 1; i < s; i++) {
				e.left.remove(1);
			}
			EntityPlayer p = Minecraft.getMinecraft().thePlayer;
			double x = corrupt(p.posX);
			double y = corrupt(p.boundingBox.minY)%256;
			double z = corrupt(p.posZ);
			double yaw = Math.abs(corrupt(p.rotationYaw+p.rotationPitch)%360)-180;
			e.left.add("C: 121/4581. F: 182, O: 0, E: 28710000000");
			e.left.add("E: 4/438. B: 3, I: 812");
			e.left.add("P: 2. T: None: 37");
			e.left.add("MultiplayerChunkCache: 183, 4372");
			e.left.add("");
			e.left.add("x: "+x+" // c: "+((int)(x/16))+" ("+((int)(x%16))+")");
			e.left.add("y: "+y+" (feet pos, "+((y)+0.162)+" eyes pos)");
			e.left.add("z: "+z+" // c: "+((int)(z/16))+" ("+((int)(z%16))+")");
			int idx = Math.abs(((int)yaw)%directions.length);
			e.left.add("f: "+idx+" ("+directions[idx]+") / "+yaw);
			e.left.add("lc: 37 b: null bl: 182 sl: -9 rl: 218");
			if (SystemUtils.IS_OS_LINUX) {
				e.left.add("ws: 0.1839, fs: ext4, g: entoo, fl: studio");
			} else if (SystemUtils.IS_OS_MAC) {
				e.left.add("ws: 0.1839, fs: HFS+, g: entoo, fl: studio");
			} else if (SystemUtils.IS_OS_WINDOWS) {
				e.left.add("ws: 0.1839, fs: NTFS, g: entoo, fl: studio");
			} else if (SystemUtils.IS_OS_UNIX) {
				e.left.add("ws: 0.1839, fs: XFS, g: entoo, fl: studio");
			} else {
				e.left.add("ws: 0.1839, fs: null, g: entoo, fl: studio");
			}
			
			if (SystemUtils.IS_OS_WINDOWS_XP) {
				e.left.add("");
				e.left.add("Why the hell are you still using Windows XP? It's "+Calendar.getInstance().get(Calendar.YEAR)+".");
			} else if (SystemUtils.IS_OS_WINDOWS_7) {
				e.left.add("");
				e.left.add("Trusty ol' Windows 7! Screw Windows 10, amirite?");
			}
			
			e.right.add("");
			e.right.add("");
			e.right.add("This glorious debug screen");
			e.right.add("brought to you by Lanthanoid.");
			e.right.add("");
			e.right.add("Craft a waypoint or a map!");
		}
	}
	
	private double corrupt(double d) {
		return (Double.hashCode(Double.doubleToLongBits(d))^0xDEADBEEF)/50000f;
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
	
	private static final ResourceLocation beam = new ResourceLocation("textures/entity/beacon_beam.png");
	
	@SubscribeEvent
	public void onRenderLast(RenderWorldLastEvent e) {
		float partialTicks = e.partialTicks;
		EntityClientPlayerMP player = Minecraft.getMinecraft().thePlayer;
		double pX = player.lastTickPosX + (player.posX - player.lastTickPosX) * partialTicks;
		double pY = player.lastTickPosY + (player.posY - player.lastTickPosY) * partialTicks;
		double pZ = player.lastTickPosZ + (player.posZ - player.lastTickPosZ) * partialTicks;
		float f1 = 1;
		FontRenderer fontrenderer = Minecraft.getMinecraft().fontRenderer;
		Tessellator tess = Tessellator.instance;
		
		if (Lanthanoid.inst.waypointManager.allWaypoints(Minecraft.getMinecraft().theWorld).isEmpty()) return;
		GL11.glDisable(GL11.GL_LIGHTING);
		GL11.glDepthMask(false);
		GL11.glEnable(GL11.GL_BLEND);
		Vec3 vec3 = player.getLook(1.0F).normalize();
		for (Waypoint waypoint : Lanthanoid.inst.waypointManager.allWaypoints(Minecraft.getMinecraft().theWorld)) {
			GL11.glPushMatrix();
				String owner = waypoint.ownerName;
				String name = waypoint.name;
				double dX = waypoint.x-pX;
				double dY = waypoint.y-pY;
				double dZ = waypoint.z-pZ;
	
				GL11.glAlphaFunc(GL11.GL_GREATER, 0.1F);
	
				double qX = Math.min(100, Math.abs(dX))*Math.signum(dX);
				double qY = Math.min(100, Math.abs(dY))*Math.signum(dY);
				double qZ = Math.min(100, Math.abs(dZ))*Math.signum(dZ);
				
				float rawF = (float) (MathHelper.sqrt_double((dX * dX) + (dY * dY) + (dZ * dZ)));
				float f = rawF;
				
				if (f < 150) {
					GL11.glEnable(GL11.GL_DEPTH_TEST);
					
					Minecraft.getMinecraft().renderEngine.bindTexture(beam);
					GL11.glTexParameterf(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, 10497.0F);
					GL11.glTexParameterf(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, 10497.0F);
					GL11.glDisable(GL11.GL_LIGHTING);
					GL11.glEnable(GL11.GL_BLEND);
					GL11.glDepthMask(true);
					OpenGlHelper.glBlendFunc(770, 1, 1, 0);
					float f2 = 0;
					float f3 = -f2 * 0.2F - (float) MathHelper.floor_float(-f2 * 0.1F);
					byte b0 = 1;
					double d3 = (double) f2 * 0.025D * (1.0D - (double) (b0 & 1) * 2.5D);
					tess.startDrawingQuads();
					tess.setColorOpaque_I(waypoint.color);
					double d5 = (double) b0 * 0.2D;
					double d7 = 0.5D + Math.cos(d3 + 2.356194490192345D) * d5;
					double d9 = 0.5D + Math.sin(d3 + 2.356194490192345D) * d5;
					double d11 = 0.5D + Math.cos(d3 + (Math.PI / 4D)) * d5;
					double d13 = 0.5D + Math.sin(d3 + (Math.PI / 4D)) * d5;
					double d15 = 0.5D + Math.cos(d3 + 3.9269908169872414D) * d5;
					double d17 = 0.5D + Math.sin(d3 + 3.9269908169872414D) * d5;
					double d19 = 0.5D + Math.cos(d3 + 5.497787143782138D) * d5;
					double d21 = 0.5D + Math.sin(d3 + 5.497787143782138D) * d5;
					double d23 = (double) (512.0F * f1);
					double d25 = 0.0D;
					double d27 = 1.0D;
					double d28 = (double) (-1.0F + f3);
					double d29 = (double) (512.0F * f1) * (0.5D / d5) + d28;
					d29 -= (Minecraft.getMinecraft().theWorld.getTotalWorldTime()+partialTicks)/10f;
					d28 -= (Minecraft.getMinecraft().theWorld.getTotalWorldTime()+partialTicks)/10f;
					tess.addVertexWithUV(qX+ d7, qY + d23, qZ+ d9, d27, d29);
					tess.addVertexWithUV(qX+ d7, qY, qZ+ d9, d27, d28);
					tess.addVertexWithUV(qX+ d11, qY, qZ+ d13, d25, d28);
					tess.addVertexWithUV(qX+ d11, qY + d23, qZ+ d13, d25, d29);
					tess.addVertexWithUV(qX+ d19, qY + d23, qZ+ d21, d27, d29);
					tess.addVertexWithUV(qX+ d19, qY, qZ+ d21, d27, d28);
					tess.addVertexWithUV(qX+ d15, qY, qZ+ d17, d25, d28);
					tess.addVertexWithUV(qX+ d15, qY + d23, qZ+ d17, d25, d29);
					tess.addVertexWithUV(qX+ d11, qY + d23, qZ+ d13, d27, d29);
					tess.addVertexWithUV(qX+ d11, qY, qZ+ d13, d27, d28);
					tess.addVertexWithUV(qX+ d19, qY, qZ+ d21, d25, d28);
					tess.addVertexWithUV(qX+ d19, qY + d23, qZ+ d21, d25, d29);
					tess.addVertexWithUV(qX+ d15, qY + d23, qZ+ d17, d27, d29);
					tess.addVertexWithUV(qX+ d15, qY, qZ+ d17, d27, d28);
					tess.addVertexWithUV(qX+ d7, qY, qZ+ d9, d25, d28);
					tess.addVertexWithUV(qX+ d7, qY + d23, qZ+ d9, d25, d29);
					tess.draw();
					
					GL11.glEnable(GL11.GL_TEXTURE_2D);
					GL11.glDepthMask(true);
					
				}
				GL11.glDisable(GL11.GL_DEPTH_TEST);
				String dist = Integer.toString((int) f);
				if (f < 10) {
					f = 10;
				}
				if (f > 150) {
					f = 150-((f-150)/15f);
				}
				f *= 0.75f;
				float f8 = 0.016666668F * f;
				GL11.glTranslatef((float) qX + 0.5F, (float) qY + 0.5F, (float) qZ + 0.5f);
				GL11.glNormal3f(0.0F, 1.0F, 0.0F);
				GL11.glRotatef(-RenderManager.instance.playerViewY, 0.0F, 1.0F, 0.0F);
				GL11.glRotatef(RenderManager.instance.playerViewX, 1.0F, 0.0F, 0.0F);
				GL11.glScalef(-f1, -f1, f1);
				GL11.glScalef(f8, f8, f8);
				OpenGlHelper.glBlendFunc(770, 771, 1, 0);
				byte b9 = -8;
	
				Vec3 vec31 = Vec3.createVectorHelper(qX+0.5,
						qY+0.5 - player.getEyeHeight(),
						qZ+0.5);
				double d0 = vec31.lengthVector();
				vec31 = vec31.normalize();
				double d1 = vec3.dotProduct(vec31);
				GL11.glDisable(GL11.GL_TEXTURE_2D);
				if (d1 > 1.0D - Math.max(0.025D, rawF/1200) / d0) {
					tess.startDrawingQuads();
					int w = Math.max((fontrenderer.getStringWidth(dist) / 2) + (fontrenderer.getStringWidth(owner) / 2) + 8, fontrenderer.getStringWidth(name));
					int j = w / 2;
					tess.setColorRGBA_I(waypoint.color, 96);
					tess.addVertex((double) (-j - 1), (double) (-1 + b9), 0.0D);
					tess.addVertex((double) (-j - 1), (double) (14 + b9), 0.0D);
					tess.addVertex((double) (j + 1), (double) (14 + b9), 0.0D);
					tess.addVertex((double) (j + 1), (double) (-1 + b9), 0.0D);
					tess.draw();
					GL11.glEnable(GL11.GL_TEXTURE_2D);
					fontrenderer.drawString(name, -fontrenderer.getStringWidth(name) / 2, b9, -1);
					GL11.glScalef(0.5f, 0.5f, 0.5f);
					fontrenderer.drawString(dist+"m", w - fontrenderer.getStringWidth(dist+"m") - 1, (b9 * 2) + 18, -1);
					fontrenderer.drawString(owner, -w, (b9 * 2) + 18, -1);
				} else {
					GL11.glPushMatrix();
						GL11.glRotatef(45, 0, 0, 1);
						{
							tess.startDrawingQuads();
							tess.setColorRGBA_I(waypoint.color, 32);
							int w = 5;
							tess.addVertex((double) (-w), (double) (-w), 0.0D);
							tess.addVertex((double) (-w), (double) (w), 0.0D);
							tess.addVertex((double) (w), (double) (w), 0.0D);
							tess.addVertex((double) (w), (double) (-w), 0.0D);
							tess.draw();
						}
						{
							tess.startDrawingQuads();
							tess.setColorRGBA_I(waypoint.color, 64);
							int w = 4;
							tess.addVertex((double) (-w), (double) (-w), 0.0D);
							tess.addVertex((double) (-w), (double) (w), 0.0D);
							tess.addVertex((double) (w), (double) (w), 0.0D);
							tess.addVertex((double) (w), (double) (-w), 0.0D);
							tess.draw();
						}
					GL11.glPopMatrix();
					GL11.glEnable(GL11.GL_TEXTURE_2D);
					GL11.glScalef(0.5f, 0.5f, 0.5f);
					fontrenderer.drawString(dist, -fontrenderer.getStringWidth(dist)/2, -4, -1);
				}
			GL11.glPopMatrix();
		}
		GL11.glEnable(GL11.GL_LIGHTING);
		GL11.glEnable(GL11.GL_DEPTH_TEST);
		GL11.glDisable(GL11.GL_BLEND);
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		GL11.glDepthMask(true);
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

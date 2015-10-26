package com.unascribed.lanthanoid;

import org.apache.commons.lang3.SystemUtils;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import com.unascribed.lanthanoid.item.ItemRifle;
import com.unascribed.lanthanoid.network.ModifyRifleModeMessage;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.InputEvent.KeyInputEvent;
import cpw.mods.fml.common.gameevent.InputEvent.MouseInputEvent;
import cpw.mods.fml.common.gameevent.TickEvent.Phase;
import cpw.mods.fml.common.gameevent.TickEvent.ClientTickEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityClientPlayerMP;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MathHelper;
import net.minecraft.util.StatCollector;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;

public class LEventHandler {
	@SideOnly(Side.CLIENT)
	private RenderItem itemRenderer = new RenderItem();
	
	public static final float TAUf = (float)(Math.PI*2f);
	
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
		"-"
	};
	
	public static final int ANIMATION_TIME = 4;
	
	private int animTicks = 0;
	private int lastSelected = -1;
	private int prevSelected = -1;
	private int diff;
	
	@SubscribeEvent
	@SideOnly(Side.CLIENT)
	public void onClientTick(ClientTickEvent e) {
		if (e.phase == Phase.END) {
			animTicks++;
		}
	}
	
	@SubscribeEvent
	@SideOnly(Side.CLIENT)
	public void onRenderGameOverlay(RenderGameOverlayEvent.Post e) {
		if (e.type == ElementType.ALL) {
			Minecraft mc = Minecraft.getMinecraft();
			EntityClientPlayerMP p = mc.thePlayer;
			if (p.getHeldItem() != null && p.getHeldItem().getItem() == LItems.rifle) {
				ItemRifle.Mode selected = LItems.rifle.getMode(p.getHeldItem());
				ItemRifle.Mode[] vals = ItemRifle.Mode.values();
				if (lastSelected == -1) {
					lastSelected = selected.ordinal();
				}
				float i = 0;
				if (lastSelected != selected.ordinal()) {
					if (animTicks >= ANIMATION_TIME) {
						animTicks = 0;
						diff = selected.ordinal() - lastSelected;
					} else {
						diff += selected.ordinal() - lastSelected;
					}
					int far = vals.length-2;
					System.out.println(diff);
					if (diff > far) {
						diff -= far;
						diff *= -1;
					} else if (diff < -far) {
						diff += far;
						diff *= -1;
					}
					System.out.println(diff);
					prevSelected = lastSelected;
					lastSelected = selected.ordinal();
				}
				GL11.glEnable(GL12.GL_RESCALE_NORMAL);
				RenderHelper.enableGUIStandardItemLighting();
				boolean renderedAnything = false;
				for (ItemRifle.Mode m : vals) {
					if (LItems.rifle.hasAmmoFor(p, m)) {
						float s = (i-selected.ordinal())+2;
						float anim = 0;
						if (animTicks < ANIMATION_TIME) {
							anim = 1-((animTicks+e.partialTicks)/(float)ANIMATION_TIME);
						}
						s += anim*diff;
						int x = Math.round(MathHelper.sin((s / vals.length)*TAUf)*34f);
						int y = Math.round(MathHelper.cos((s / vals.length)*TAUf)*34f);
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
						mc.fontRenderer.drawStringWithShadow(keys[(int)i], 4, 4, -1);
						GL11.glPopMatrix();
						renderedAnything = true;
					}
					i++;
				}
				if (!renderedAnything) {
					mc.fontRenderer.drawStringWithShadow(StatCollector.translateToLocal("ui.no_ammo_hint"), 4, 4, -1);
				}
				RenderHelper.disableStandardItemLighting();
				GL11.glDisable(GL12.GL_RESCALE_NORMAL);
			}
		}
	}
	
	private boolean linuxNag = true;
	
	@SubscribeEvent
	@SideOnly(Side.CLIENT)
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
								Lanthanoid.inst.network.sendToServer(new ModifyRifleModeMessage(true, 1));
								return;
							}
							if (Keyboard.getEventCharacter() == '^') {
								while (mc.gameSettings.keyBindsHotbar[5].isPressed()) {}
								Lanthanoid.inst.network.sendToServer(new ModifyRifleModeMessage(true, 5));
								return;
							}
						}
						if (Keyboard.getEventKey() == Keyboard.KEY_0) {
							Lanthanoid.inst.network.sendToServer(new ModifyRifleModeMessage(true, 9));
							return;
						}
						if (Keyboard.getEventKey() == Keyboard.KEY_UNDERLINE) {
							Lanthanoid.inst.network.sendToServer(new ModifyRifleModeMessage(true, 10));
							return;
						}
						for (int i = 0; i < 9; i++) {
							if (mc.gameSettings.keyBindsHotbar[i].isPressed()) {
								while (mc.gameSettings.keyBindsHotbar[i].isPressed()) {} // drain pressTicks to zero to suppress vanilla behavior
								Lanthanoid.inst.network.sendToServer(new ModifyRifleModeMessage(true, i));
							}
						}
						return;
					}
				}
			}
		}
	}
	@SubscribeEvent
	@SideOnly(Side.CLIENT)
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

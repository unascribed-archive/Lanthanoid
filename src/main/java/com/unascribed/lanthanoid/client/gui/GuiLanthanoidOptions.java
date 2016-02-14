package com.unascribed.lanthanoid.client.gui;

import java.net.MalformedURLException;
import java.net.URL;

import org.lwjgl.input.Keyboard;

import com.unascribed.lanthanoid.client.ClientConfig;
import com.unascribed.lanthanoid.client.ClientConfig.Eagerness;

import cpw.mods.fml.client.config.GuiSlider;
import cpw.mods.fml.client.config.GuiSlider.ISlider;
import cpw.mods.fml.common.ObfuscationReflectionHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.SoundHandler;
import net.minecraft.client.audio.SoundManager;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;
import paulscode.sound.SoundSystem;

public class GuiLanthanoidOptions extends GuiScreen implements ISlider {
	private GuiScreen parent;
	
	private int[] konami = {
		Keyboard.KEY_UP,
		Keyboard.KEY_UP,
		Keyboard.KEY_DOWN,
		Keyboard.KEY_DOWN,
		Keyboard.KEY_LEFT,
		Keyboard.KEY_RIGHT,
		Keyboard.KEY_LEFT,
		Keyboard.KEY_RIGHT,
		Keyboard.KEY_B,
		Keyboard.KEY_A,
	};
	private int konamiPos = 0;
	
	public GuiLanthanoidOptions(GuiScreen parent) {
		this.parent = parent;
	}
	
	@Override
	public void initGui() {
		buttonList.clear();
		
		buttonList.add(new GuiButton(0, width / 2 - 155, height / 6 - 12, 150, 20, I18n.format("gui.lanthanoid.flight_scheme")+ClientConfig.flightScheme));
		buttonList.add(new GuiSlider(1, width / 2 + 5, height / 6 - 12, 150, 20, I18n.format("gui.lanthanoid.waypoint_scale"), "%", 10, 1000, ClientConfig.waypointScale*100, false, true, this));
		
		buttonList.add(new GuiButton(2, width / 2 - 155, height / 6 - 12 + 24, 150, 20, I18n.format("gui.lanthanoid.invert_slowfall")+I18n.format("gui.lanthanoid."+ClientConfig.invertSlowfall)));
		buttonList.add(new GuiSlider(3, width / 2 + 5, height / 6 - 12 + 24, 150, 20, I18n.format("gui.lanthanoid.hud_glyph_scale"), "%", 50, 250, ClientConfig.hudGlyphScale*100, false, true, this));
		
		buttonList.add(new GuiButton(4, width / 2 - 155, height / 6 - 12 + 48, 150, 20, I18n.format("gui.lanthanoid.invert_hover")+I18n.format("gui.lanthanoid."+ClientConfig.invertHover)));
		buttonList.add(new GuiButton(5, width / 2 + 5, height / 6 - 12 + 48, 150, 20, I18n.format("gui.lanthanoid.boots_change_fov")+I18n.format("gui.lanthanoid."+ClientConfig.bootsChangeFov)));
		
		buttonList.add(new GuiButton(200, width / 2 - 100, height / 6 + 168, I18n.format("gui.done")));
	}
	
	@Override
	public void onChangeSliderValue(GuiSlider slider) {
		if (slider.id == 1) {
			ClientConfig.waypointScale = (float)(slider.getValue()/100);
		} else if (slider.id == 3) {
			ClientConfig.hudGlyphScale = (float)(slider.getValue()/100);
		}
	}
	
	@Override
	protected void actionPerformed(GuiButton button) {
		if (button.id == 0) {
			ClientConfig.flightScheme = Eagerness.values()[(ClientConfig.flightScheme.ordinal()+1)%Eagerness.count];
		} else if (button.id == 1) {
			return;
		} else if (button.id == 2) {
			ClientConfig.invertSlowfall = !ClientConfig.invertSlowfall;
		} else if (button.id == 3) {
			return;
		} else if (button.id == 4) {
			ClientConfig.invertHover = !ClientConfig.invertHover;
		} else if (button.id == 5) {
			ClientConfig.bootsChangeFov = !ClientConfig.bootsChangeFov;
		} else if (button.id == 200) {
			Minecraft.getMinecraft().displayGuiScreen(parent);
			return;
		}
		initGui();
	}
	
	@Override
	public void onGuiClosed() {
		ClientConfig.save();
	}
	
	@Override
	protected void keyTyped(char typedChar, int keyCode) {
		super.keyTyped(typedChar, keyCode);
		if (keyCode == konami[konamiPos]) {
			konamiPos++;
			if (konamiPos >= konami.length) {
				konamiPos = 0;
				SoundSystem sndsys = ObfuscationReflectionHelper.getPrivateValue(SoundManager.class, (SoundManager)ObfuscationReflectionHelper.getPrivateValue(SoundHandler.class, mc.getSoundHandler(), "field_147694_f", "sndManager"), "field_148620_e", "sndSystem");
				if (sndsys.playing("LanthanoidEasterEgg")) {
					sndsys.fadeOut("LanthanoidEasterEgg", null, 2000);
				} else {
					try {
						sndsys.backgroundMusic("LanthanoidEasterEgg", new URL("http://unascribed.com/GoodbyeMyLechenaultia.ogg"), "GoodbyeMyLechenaultia.ogg", false);
					} catch (MalformedURLException e) {
						e.printStackTrace();
					}
				}
			}
		} else {
			konamiPos = 0;
		}
	}
	
	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		drawDefaultBackground();
		drawCenteredString(this.fontRendererObj, "Lanthanoid Options", this.width / 2, 15, 16777215);
		super.drawScreen(mouseX, mouseY, partialTicks);
	}

}

package com.unascribed.lanthanoid.client.gui;

import org.lwjgl.opengl.GL11;

import com.unascribed.lanthanoid.init.LMaterials;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.util.ResourceLocation;

public class GuiLanthanoidButton extends GuiButton {
	private static final ResourceLocation LANTHANIDE_RIFLE = new ResourceLocation("lanthanoid", "textures/items/rifle.png");
	
	public GuiLanthanoidButton(int x, int y) {
		super(34747, x, y, 20, 20, "");
	}
	
	@Override
	public void drawButton(Minecraft mc, int mouseX, int mouseY) {
		super.drawButton(mc, mouseX, mouseY);
		Minecraft.getMinecraft().renderEngine.bindTexture(LANTHANIDE_RIFLE);
		int color = LMaterials.colors.get("Holmium");
		GL11.glColor3f(((color>>16)&0xFF)/255f, ((color>>8)&0xFF)/255f, (color&0xFF)/255f);
		drawModalRectWithCustomSizedTexture(xPosition+2, yPosition+2, 0, 0, 16, 16, 16, 16);
		if (hovered) {
			FontRenderer fr = Minecraft.getMinecraft().fontRendererObj;
			String str = "Lanthanoid Options";
			fr.drawStringWithShadow(str, xPosition+10-(fr.getStringWidth(str)/2), yPosition+22, -1);
		}
	}
	
}

package com.unascribed.lanthanoid.client;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityClientPlayerMP;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.util.ResourceLocation;

public class Rendering {
	private static final ResourceLocation RES_ITEM_GLINT = new ResourceLocation("textures/misc/enchanted_item_glint.png");
	
	public static void renderItemDefault(ItemStack item) {
		EntityClientPlayerMP player = Minecraft.getMinecraft().thePlayer;
		float thickness = 0.0625f;
		for (int i = 0; i < item.getItem().getRenderPasses(item.getMetadata()); i++) {
			IIcon icon = item.getItem().getIcon(item, i, player, player.getItemInUse(), player.getItemInUseCount());
			int color = item.getItem().getColorFromItemStack(item, i);
			float r = ((color >> 16)&0xFF)/255f;
			float g = ((color >> 8)&0xFF)/255f;
			float b = (color&0xFF)/255f;
			GL11.glColor3f(r, g, b);
			Minecraft.getMinecraft().renderEngine.bindTexture(item.getItemSpriteNumber() == 0 ? TextureMap.locationBlocksTexture : TextureMap.locationItemsTexture);
			ItemRenderer.renderItemIn2D(Tessellator.instance, icon.getMinU(), icon.getMinV(), icon.getMaxU(), icon.getMaxV(), icon.getIconWidth(), icon.getIconHeight(), thickness);
			if (item.hasEffect(i)) {
				// TODO find out why this doesn't match vanilla exactly
				GL11.glDepthFunc(GL11.GL_EQUAL);
				GL11.glDisable(GL11.GL_LIGHTING);
				Minecraft.getMinecraft().renderEngine.bindTexture(RES_ITEM_GLINT);
				GL11.glEnable(GL11.GL_BLEND);
				GL11.glBlendFunc(GL11.GL_SRC_COLOR, GL11.GL_ONE);
				float f11 = 0.76F;
				GL11.glColor4f(0.5F * f11, 0.25F * f11, 0.8F * f11, 1.0F);
				GL11.glMatrixMode(GL11.GL_TEXTURE);
				GL11.glPushMatrix();
				float f12 = 0.125F;
				GL11.glScalef(f12, f12, f12);
				float f13 = (float) (Minecraft.getSystemTime() % 3000L) / 3000.0F * 8.0F;
				GL11.glTranslatef(f13, 0.0F, 0.0F);
				GL11.glRotatef(-5.0F, 0.0F, 0.0F, 1.0F);
				ItemRenderer.renderItemIn2D(Tessellator.instance, 0.0F, 0.0F, 1.0F, 1.0F, 255, 255, thickness);
				GL11.glPopMatrix();
				GL11.glPushMatrix();
				GL11.glScalef(f12, f12, f12);
				f13 = (float) (Minecraft.getSystemTime() % 4873L) / 4873.0F * 8.0F;
				GL11.glTranslatef(-f13, 0.0F, 0.0F);
				GL11.glRotatef(10.0F, 0.0F, 0.0F, 1.0F);
				ItemRenderer.renderItemIn2D(Tessellator.instance, 0.0F, 0.0F, 1.0F, 1.0F, 255, 255, thickness);
				GL11.glPopMatrix();
				GL11.glMatrixMode(GL11.GL_MODELVIEW);
				GL11.glDisable(GL11.GL_BLEND);
				GL11.glEnable(GL11.GL_LIGHTING);
				GL11.glDepthFunc(GL11.GL_LEQUAL);
			}
		}
	}

	public static void drawExtrudedHalfIcon(IIcon icon, float thickness) {
		if (icon == null) {
			return;
		}
		ItemRenderer.renderItemIn2D(Tessellator.instance, icon.getMinU(), icon.getMinV(), icon.getMaxU(), icon.getMinV()+((icon.getMaxV()-icon.getMinV())/2), icon.getIconWidth(), icon.getIconHeight()/2, thickness);
	}

	public static void drawExtrudedIcon(IIcon icon, float thickness) {
		if (icon == null) {
			return;
		}
		ItemRenderer.renderItemIn2D(Tessellator.instance, icon.getMinU(), icon.getMinV(), icon.getMaxU(), icon.getMaxV(), icon.getIconWidth(), icon.getIconHeight(), thickness);
	}

}

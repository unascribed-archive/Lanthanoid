package com.unascribed.lanthanoid.client;

import org.lwjgl.opengl.GL11;

import com.unascribed.lanthanoid.init.LBlocks;

import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.IItemRenderer;

public class MachineItemRenderer implements IItemRenderer {

	@Override
	public boolean handleRenderType(ItemStack item, ItemRenderType type) {
		return type == ItemRenderType.INVENTORY;
	}

	@Override
	public boolean shouldUseRenderHelper(ItemRenderType type, ItemStack item, ItemRendererHelper helper) {
		return false;
	}

	@Override
	public void renderItem(ItemRenderType type, ItemStack item, Object... data) {
		RenderBlocks rb = (RenderBlocks)data[0];
		GL11.glEnable(GL11.GL_LIGHTING);
		GL11.glPushMatrix();
			 GL11.glTranslatef(-2, 3, -3.0F);
	         GL11.glScalef(10F, 10F, 10F);
	         GL11.glTranslatef(1.0F, 0.5F, 1.0F);
	         GL11.glScalef(1.0F, 1.0F, -1F);
	         GL11.glRotatef(210F, 1.0F, 0.0F, 0.0F);
	         GL11.glRotatef(45F, 0.0F, 1.0F, 0.0F);
	         GL11.glRotatef(-90F, 0.0F, 1.0F, 0.0F);
			rb.renderBlockAsItem(LBlocks.machine, item.getItemDamage(), 1.0f);
		GL11.glPopMatrix();
		GL11.glDisable(GL11.GL_LIGHTING);
		
		int color = -1;
		if (item.hasTagCompound() && item.getTagCompound().hasKey("Color", 99)) {
			color = item.getTagCompound().getInteger("Color");
		}
		
		Tessellator tess = Tessellator.instance;
		GL11.glDisable(GL11.GL_TEXTURE_2D);
		GL11.glDisable(GL11.GL_DEPTH_TEST);
		GL11.glEnable(GL11.GL_BLEND);
		OpenGlHelper.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, 1, 0);
		GL11.glPushMatrix();
			GL11.glTranslatef(4f, 4f, 0f);
			GL11.glRotatef(45, 0, 0, 1);
			{
				tess.startDrawingQuads();
				tess.setColorRGBA_I(color, 128);
				int w = 3;
				tess.addVertex((double) (-w), (double) (-w), 0.0D);
				tess.addVertex((double) (-w), (double) (w), 0.0D);
				tess.addVertex((double) (w), (double) (w), 0.0D);
				tess.addVertex((double) (w), (double) (-w), 0.0D);
				tess.draw();
			}
			{
				tess.startDrawingQuads();
				tess.setColorRGBA_I(color, 255);
				int w = 2;
				tess.addVertex((double) (-w), (double) (-w), 0.0D);
				tess.addVertex((double) (-w), (double) (w), 0.0D);
				tess.addVertex((double) (w), (double) (w), 0.0D);
				tess.addVertex((double) (w), (double) (-w), 0.0D);
				tess.draw();
			}
		GL11.glPopMatrix();
		GL11.glDisable(GL11.GL_BLEND);
		GL11.glEnable(GL11.GL_TEXTURE_2D);
		GL11.glEnable(GL11.GL_DEPTH_TEST);
	}

}

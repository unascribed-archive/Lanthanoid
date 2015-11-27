package com.unascribed.lanthanoid.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityClientPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MathHelper;
import net.minecraftforge.client.IItemRenderer;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import com.unascribed.lanthanoid.init.LItems;

public class RifleItemRenderer implements IItemRenderer {
	@Override
	public boolean handleRenderType(ItemStack item, ItemRenderType type) {
		return item.getItem() == LItems.rifle && (type == ItemRenderType.EQUIPPED_FIRST_PERSON);
	}

	@Override
	public boolean shouldUseRenderHelper(ItemRenderType type, ItemStack item, ItemRendererHelper helper) {
		return false;
	}

	@Override
	public void renderItem(ItemRenderType type, ItemStack item, Object... data) {
		EntityClientPlayerMP player = ((EntityClientPlayerMP) data[1]);
		GL11.glTranslatef(1.0f, 0f, 0f);
		GL11.glRotatef(180F, 1.0f, 0.0f, 0.0f);
		GL11.glRotatef(180F, 0.0f, 0.0f, 1.0f);
		if (LClientEventHandler.scopeFactor == 0) {
			GL11.glPopMatrix();
			GL11.glPopMatrix();
			GL11.glPopMatrix();
			GL11.glPushMatrix();
			GL11.glPushMatrix();
			GL11.glPushMatrix();
			float f1 = 1.0f;
			float f13 = 0.8f;
			GL11.glTranslatef(0.7F * f13, -0.65F * f13 - (1.0F - f1) * 0.6F, -0.9F * f13);
			GL11.glRotatef(45.0F, 0.0F, 1.0F, 0.0F);
			GL11.glEnable(GL12.GL_RESCALE_NORMAL);
			float f5 = player.getSwingProgress(Minecraft.getMinecraft().timer.renderPartialTicks);
			float f6 = MathHelper.sin(f5 * f5 * (float) Math.PI);
			float f7 = MathHelper.sin(MathHelper.sqrt_float(f5) * (float) Math.PI);
			GL11.glRotatef(-f6 * 20.0F, 0.0F, 1.0F, 0.0F);
			GL11.glRotatef(-f7 * 20.0F, 0.0F, 0.0F, 1.0F);
			GL11.glRotatef(-f7 * 80.0F, 1.0F, 0.0F, 0.0F);
			float f8 = 0.4F;
			GL11.glScalef(f8, f8, f8);
			
			GL11.glTranslatef(-1.02f, 0.355f, -1f);
			GL11.glRotatef(0f, 1, 0, 0);
			GL11.glRotatef(-135f, 0, 1, 0);
			GL11.glRotatef(0f, 0, 0, 1);
		}
		Rendering.renderItemDefault(item);
	}

}
package com.unascribed.lanthanoid.client;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.util.MathHelper;
import net.minecraftforge.client.IItemRenderer;

public class WreckingBallItemRenderer implements IItemRenderer {

	@Override
	public boolean handleRenderType(ItemStack item, ItemRenderType type) {
		return type == ItemRenderType.EQUIPPED_FIRST_PERSON;
	}

	@Override
	public boolean shouldUseRenderHelper(ItemRenderType type, ItemStack item, ItemRendererHelper helper) {
		return false;
	}

	@Override
	public void renderItem(ItemRenderType type, ItemStack item, Object... data) {
		float y = (item.hasTagCompound() ? item.getTagCompound().getFloat("cooldown")/40f : 0);
		if (y > 0) {
			y -= Minecraft.getMinecraft().timer.renderPartialTicks/40f;
		}
		EntityPlayer player = Minecraft.getMinecraft().thePlayer;
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
		float f5 = 0;
		float f6 = MathHelper.sin(f5 * f5 * (float) Math.PI);
		float f7 = MathHelper.sin(MathHelper.sqrt_float(f5) * (float) Math.PI);
		GL11.glRotatef(-f6 * 20.0F, 0.0F, 1.0F, 0.0F);
		GL11.glRotatef(-f7 * 20.0F, 0.0F, 0.0F, 1.0F);
		GL11.glRotatef(-f7 * 80.0F, 1.0F, 0.0F, 0.0F);
		float f8 = 0.4F;
		GL11.glScalef(f8, f8, f8);
		
		GL11.glRotatef(15f, 1, 0, 0);
		GL11.glRotatef(-135f, 0, 1, 0);
		GL11.glRotatef(0f, 0, 0, 1);
		
		GL11.glTranslatef(0.8f, 0.3f-(y/2), 0.5f);
		GL11.glRotatef(180F-(y*45F), 1.0f, 0.0f, 0.0f);
		GL11.glRotatef(180F-(y*45F), 0.0f, 0.0f, 1.0f);
		if (player.isSwingInProgress) {
			GL11.glTranslatef(player.worldObj.rand.nextFloat()*5, player.worldObj.rand.nextFloat(), -player.worldObj.rand.nextFloat()*4);
			GL11.glRotatef(180f, player.worldObj.rand.nextFloat(), player.worldObj.rand.nextFloat(), player.worldObj.rand.nextFloat());
		}
		for (int i = 0; i < item.getItem().getRenderPasses(item.getItemDamage()); i++) {
			IIcon icon = item.getItem().getIcon(item, i, player, player.getItemInUse(), player.getItemInUseCount());
			int color = item.getItem().getColorFromItemStack(item, i);
			float r = ((color >> 16)&0xFF)/255f;
			float g = ((color >> 8)&0xFF)/255f;
			float b = (color&0xFF)/255f;
			GL11.glColor3f(r, g, b);
			ItemRenderer.renderItemIn2D(Tessellator.instance, icon.getMinU(), icon.getMinV(), icon.getMaxU(), icon.getMaxV(), icon.getIconWidth(), icon.getIconHeight(), 0.0625f);
		}
	}

}

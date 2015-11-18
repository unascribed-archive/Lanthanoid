package com.unascribed.lanthanoid.client;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.util.MathHelper;

@SideOnly(Side.CLIENT)
public class ModelSimpleBook extends ModelBase {
	public ModelRenderer coverRight = (new ModelRenderer(this)).setTextureOffset(0, 0).addBox(-6.0F, -5.0F, 0.0F, 6, 10, 0);
	public ModelRenderer coverLeft = (new ModelRenderer(this)).setTextureOffset(16, 0).addBox(0.0F, -5.0F, 0.0F, 6, 10, 0);
	public ModelRenderer pages = (new ModelRenderer(this)).setTextureOffset(0, 10).addBox(0.1F, -3.5F, -0.99F, 5, 7, 2);
	public ModelRenderer bookSpine = (new ModelRenderer(this)).setTextureOffset(12, 0).addBox(-1.0F, -5.0F, 0.0F, 2, 10, 0);

	public ModelSimpleBook() {
		this.coverRight.setRotationPoint(0.0F, 0.0F, -1.0F);
		this.coverLeft.setRotationPoint(0.0F, 0.0F, 1.0F);
		this.bookSpine.rotateAngleY = ((float) Math.PI / 2F);
	}

	public void render(Entity p_78088_1_, float p_78088_2_, float p_78088_3_, float p_78088_4_, float p_78088_5_, float p_78088_6_, float p_78088_7_) {
		this.setRotationAngles(p_78088_2_, p_78088_3_, p_78088_4_, p_78088_5_, p_78088_6_, p_78088_7_, p_78088_1_);
		GL11.glDisable(GL11.GL_TEXTURE_2D);
		GL11.glColor3f(0.8f, 0.8f, 0.8f);
		this.pages.render(p_78088_7_);
		GL11.glEnable(GL11.GL_TEXTURE_2D);
		GL11.glColor3f(1, 1, 1);
		this.coverRight.render(p_78088_7_);
		this.coverLeft.render(p_78088_7_);
		this.bookSpine.render(p_78088_7_);
	}

	public void setRotationAngles(float p_78087_1_, float p_78087_2_, float p_78087_3_, float p_78087_4_, float p_78087_5_, float p_78087_6_, Entity p_78087_7_) {
		float f6 = (MathHelper.sin(p_78087_1_ * 0.02F) * 0.1F + 1.25F) * p_78087_4_;
		this.coverRight.rotateAngleY = (float) Math.PI + f6;
		this.coverLeft.rotateAngleY = -f6;
		this.pages.rotateAngleY = f6;
	}
}
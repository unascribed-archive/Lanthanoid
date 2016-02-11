package com.unascribed.lanthanoid.client;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import com.google.common.base.Supplier;
import com.unascribed.lanthanoid.glyph.IGlyphHolderItem;
import com.unascribed.lanthanoid.item.ItemEldritchSword;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityClientPlayerMP;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.util.MathHelper;
import net.minecraftforge.client.IItemRenderer;

public class EldritchItemRenderer implements IItemRenderer {

	public class GlyphEmitter {
		private final float startX, startY, endX, endY;
		public GlyphEmitter(float startX, float startY, float endX, float endY) {
			this.startX = startX;
			this.startY = startY;
			this.endX = endX;
			this.endY = endY;
		}
		public void render(float t, float density) {
			
		}
	}
	
	private float glyphsX, glyphsY, glyphsAngle;
	private Supplier<IIcon> glyphs;
	private GlyphEmitter[] emitters;
	
	public EldritchItemRenderer(float glyphsX, float glyphsY, float glyphsAngle, Supplier<IIcon> glyphs, GlyphEmitter... emitters) {
		super();
		this.glyphsX = glyphsX;
		this.glyphsY = glyphsY;
		this.glyphsAngle = glyphsAngle;
		this.glyphs = glyphs;
		this.emitters = emitters;
	}

	@Override
	public boolean handleRenderType(ItemStack item, ItemRenderType type) {
		return true;
	}

	@Override
	public boolean shouldUseRenderHelper(ItemRenderType type, ItemStack item, ItemRendererHelper helper) {
		return type == ItemRenderType.ENTITY && (helper == ItemRendererHelper.ENTITY_BOBBING || helper == ItemRendererHelper.ENTITY_ROTATION);
	}

	@Override
	public void renderItem(ItemRenderType type, ItemStack item, Object... data) {
		if (!(item.getItem() instanceof IGlyphHolderItem)) return;
		
		IGlyphHolderItem holder = (IGlyphHolderItem) item.getItem();
		
		float partialTicks = Minecraft.getMinecraft().timer.renderPartialTicks;
		EntityClientPlayerMP p = Minecraft.getMinecraft().thePlayer;
		//ItemRenderer ir = Minecraft.getMinecraft().entityRenderer.itemRenderer;
		float playerAnim = 1;//(item == p.getHeldItem() ? ir.prevEquippedProgress + (ir.equippedProgress - ir.prevEquippedProgress) * partialTicks : 0);
		float glyphCount = holder.getMilliglyphs(item) / (float)holder.getMaxMilliglyphs(item);
		
		float t = p.ticksExisted+partialTicks;
		float sin = MathHelper.sin(t/20);
		
		float q = Math.max(0.25f, playerAnim);
		float q2 = Math.max(0.25f, playerAnim*glyphCount);
		
		float r = q2;
		float g = q-playerAnim;
		float b = q-(playerAnim*glyphCount);
		float a = 0.5f;

		
		GL11.glEnable(GL11.GL_ALPHA_TEST);
		if (type == ItemRenderType.INVENTORY) {
			GL11.glScalef(-16, -16, -1);
			GL11.glTranslatef(-1.0f, -1.0f, 0f);
		}
		if (type == ItemRenderType.ENTITY) {
			GL11.glTranslatef(0.5f, 0f, 0f);
		} else {
			GL11.glTranslatef(1.0f, 0f, 0f);
		}
		GL11.glRotatef(180F, 1.0f, 0.0f, 0.0f);
		GL11.glRotatef(180F, 0.0f, 0.0f, 1.0f);
		if (holder instanceof ItemEldritchSword) {
			ItemEldritchSword ies = (ItemEldritchSword)holder;
			float m = 1-(ies.getTicksUntilReady(item)/100f);
			r *= m;
			g *= m;
			b *= m;
			if (m == 1) {
				a = 0.5f;
			} else {
				a = (m/4)+0.15f;
			}
			if ((p.getItemInUse() == item || ies.isCharging(item)) && type == ItemRenderType.EQUIPPED_FIRST_PERSON) {
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
				float f5 = p.getSwingProgress(Minecraft.getMinecraft().timer.renderPartialTicks);
				float f6 = MathHelper.sin(f5 * f5 * (float) Math.PI);
				float f7 = MathHelper.sin(MathHelper.sqrt_float(f5) * (float) Math.PI);
				GL11.glRotatef(-f6 * 20.0F, 0.0F, 1.0F, 0.0F);
				GL11.glRotatef(-f7 * 20.0F, 0.0F, 0.0F, 1.0F);
				GL11.glRotatef(-f7 * 80.0F, 1.0F, 0.0F, 0.0F);
				float f8 = 0.4F;
				GL11.glScalef(f8, f8, f8);
				
				Minecraft.getMinecraft().renderEngine.bindTexture(p.getLocationSkin());
				GL11.glPushMatrix();
					GL11.glTranslatef(-0.3f, 0.15f, -0.1f);
					GL11.glScalef(1, 1, 1.75f);
					GL11.glRotatef(-70f, 1, 0, 0);
					((RenderPlayer)RenderManager.instance.entityRenderMap.get(EntityPlayer.class)).renderFirstPersonArm(p);
				GL11.glPopMatrix();
				
				GL11.glPushMatrix();
					GL11.glTranslatef(-1.8f, 0.15f, -1f);
					GL11.glScalef(1.75f, 1f, 1f);
					GL11.glRotatef(-90f, 0, 1, 0);
					GL11.glRotatef(-70f, 1, 0, 0);
					((RenderPlayer)RenderManager.instance.entityRenderMap.get(EntityPlayer.class)).renderFirstPersonArm(p);
				GL11.glPopMatrix();
				
				GL11.glTranslatef(-1.02f, 0.355f, -1f);
				GL11.glRotatef(90f, 1, 0, 0);
				GL11.glRotatef(-160f, 0, 1, 0);
				GL11.glRotatef(180f, 0, 0, 1);
			}
		}
		
		Rendering.renderItemDefault(item);
		GL11.glEnable(GL11.GL_ALPHA_TEST);
		
		IIcon glyphs = this.glyphs.get();
		
		Minecraft.getMinecraft().renderEngine.bindTexture(TextureMap.locationItemsTexture);
		
		GL11.glDisable(GL11.GL_LIGHTING);
		
		if (type == ItemRenderType.INVENTORY) {
			GL11.glDisable(GL11.GL_CULL_FACE);
			GL11.glScalef(1, -1, 1);
			GL11.glTranslatef(0f, -0.65f, 0);
			GL11.glColor4f(r, g, b, a);
			Tessellator tess = Tessellator.instance;
			tess.startDrawingQuads();
			tess.addVertexWithUV(0, 0, 0, glyphs.getMinU(), glyphs.getMinV());
			tess.addVertexWithUV(1, 0, 0, glyphs.getMaxU(), glyphs.getMinV());
			tess.addVertexWithUV(1, 1, 0, glyphs.getMaxU(), glyphs.getMaxV());
			tess.addVertexWithUV(0, 1, 0, glyphs.getMinU(), glyphs.getMaxV());
			tess.draw();
			GL11.glEnable(GL11.GL_CULL_FACE);
		} else {
			GL11.glTranslatef(glyphsX, glyphsY, 0.05f);
			GL11.glScalef(0.33f, 0.33f, 0.33f);
			GL11.glTranslatef(0.5f, 0.5f, 0.5f);
			GL11.glRotatef(glyphsAngle, 0, 0, 1);
			GL11.glTranslatef(-0.5f, -0.5f, -0.5f);
			
			GL11.glScalef(1, 0.5f, 1);
			GL11.glTranslatef(0.5f, 0.5f, 0.5f);
			GL11.glRotatef(((sin*2)-5)*playerAnim, 1, 0, 0);
			GL11.glRotatef((sin*2)*playerAnim, 0, 0, 1);
			GL11.glTranslatef(-0.5f, -0.5f, -0.5f);
			
			OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 240f, 240f);
			
			GL11.glDisable(GL11.GL_LIGHTING);
			GL11.glEnable(GL11.GL_BLEND);
			OpenGlHelper.glBlendFunc(770, 771, 0, 1);
			GL11.glColor4f(r, g, b, a/3);
			Rendering.drawExtrudedHalfIcon(glyphs, 0.2f);
			GL11.glColor4f(r, g, b, a);
			GL11.glTranslatef(0.0f, 0.0f, 0.0625f);
			Rendering.drawExtrudedHalfIcon(glyphs, 0.0625f);
		}
	}

}

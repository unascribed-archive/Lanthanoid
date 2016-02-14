package com.unascribed.lanthanoid.client.render.item;

import org.lwjgl.opengl.GL11;

import com.unascribed.lanthanoid.client.RingRenderer;
import com.unascribed.lanthanoid.client.render.tile.EldritchTileEntitySpecialRenderer;
import com.unascribed.lanthanoid.init.LBlocks;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityClientPlayerMP;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.util.MathHelper;
import net.minecraftforge.client.IItemRenderer;

public class MachineItemRenderer implements IItemRenderer {

	@Override
	public boolean handleRenderType(ItemStack item, ItemRenderType type) {
		return type != ItemRenderType.FIRST_PERSON_MAP;
	}

	@Override
	public boolean shouldUseRenderHelper(ItemRenderType type, ItemStack item, ItemRendererHelper helper) {
		return (type == ItemRenderType.ENTITY && (helper == ItemRendererHelper.ENTITY_BOBBING || helper == ItemRendererHelper.ENTITY_ROTATION)) ||
				(type == ItemRenderType.EQUIPPED && helper == ItemRendererHelper.EQUIPPED_BLOCK) ||
				(type == ItemRenderType.EQUIPPED_FIRST_PERSON && helper == ItemRendererHelper.EQUIPPED_BLOCK) ||
				(item.getMetadata() >= 3 && item.getMetadata() <= 7 && type == ItemRenderType.INVENTORY && helper == ItemRendererHelper.INVENTORY_BLOCK);
	}

	@Override
	public void renderItem(ItemRenderType type, ItemStack item, Object... data) {
		RenderBlocks rb = (RenderBlocks)data[0];
		int meta = item.getMetadata();
		/*if (type == ItemRenderType.INVENTORY) {
			float f = 20;
			GL11.glScalef(f, f, f);
		}*/
		GL11.glDepthMask(true);
		GL11.glEnable(GL11.GL_LIGHTING);
		GL11.glPushMatrix();
			if (type == ItemRenderType.INVENTORY) {
				if (meta <= 2) {
					GL11.glTranslatef(-2, 3, -3.0F);
					GL11.glScalef(10F, 10F, 10F);
					GL11.glTranslatef(1.0F, 0.5F, 1.0F);
					GL11.glScalef(1.0F, 1.0F, -1F);
					GL11.glRotatef(210F, 1.0F, 0.0F, 0.0F);
					GL11.glRotatef(45F, 0.0F, 1.0F, 0.0F);
					GL11.glRotatef(-90F, 0.0F, 1.0F, 0.0F);
				} else {
					GL11.glTranslatef(0, 0, 0);
				}
			} else if (type == ItemRenderType.EQUIPPED || type == ItemRenderType.EQUIPPED_FIRST_PERSON) {
				GL11.glTranslatef(0.45f, 0.525f, 0.5f);
			}
			rb.renderBlockAsItem(LBlocks.machine, item.getMetadata(), 1.0f);
		GL11.glPopMatrix();
		
		int color = -1;
		if (item.hasTagCompound() && item.getTagCompound().hasKey("Color", 99)) {
			color = item.getTagCompound().getInteger("Color");
		}
		
		if (meta == 0 || meta == 1) {
			if (type == ItemRenderType.INVENTORY) {
				Tessellator tess = Tessellator.instance;
				GL11.glDisable(GL11.GL_TEXTURE_2D);
				GL11.glDisable(GL11.GL_DEPTH_TEST);
				GL11.glDisable(GL11.GL_LIGHTING);
				GL11.glEnable(GL11.GL_BLEND);
				OpenGlHelper.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, 1, 0);
				boolean global = (item.getMetadata() == 1);
				GL11.glPushMatrix();
					GL11.glTranslatef(4f, 4f, 0f);
					GL11.glRotatef(45, 0, 0, 1);
					{
						tess.startDrawing(global ? GL11.GL_QUADS : GL11.GL_POLYGON);
						tess.setColorRGBA_I(color, 96);
						double w = 3;
						if (!global) {
							for (int i = 0; i < 20; i++) {
								double cX = MathHelper.sin((i/20f)*RingRenderer.TAUf) * w;
								double cY = MathHelper.cos((i/20f)*RingRenderer.TAUf) * w;
								tess.addVertex(cX, cY, 0);
							}
						} else {
							tess.addVertex((-w), (-w), 0.0D);
							tess.addVertex((-w), (w), 0.0D);
							tess.addVertex((w), (w), 0.0D);
							tess.addVertex((w), (-w), 0.0D);
						}
						tess.draw();
					}
					{
						tess.startDrawing(global ? GL11.GL_QUADS : GL11.GL_POLYGON);
						tess.setColorRGBA_I(color, 192);
						double w = 2;
						if (!global) {
							for (int i = 0; i < 20; i++) {
								double cX = MathHelper.sin((i/20f)*RingRenderer.TAUf) * w;
								double cY = MathHelper.cos((i/20f)*RingRenderer.TAUf) * w;
								tess.addVertex(cX, cY, 0);
							}
						} else {
							tess.addVertex((-w), (-w), 0.0D);
							tess.addVertex((-w), (w), 0.0D);
							tess.addVertex((w), (w), 0.0D);
							tess.addVertex((w), (-w), 0.0D);
						}
						tess.draw();
					}
					GL11.glEnable(GL11.GL_DEPTH_TEST);
				GL11.glPopMatrix();
			}
		} else if (meta >= 3 && meta <= 7) {
			GL11.glEnable(GL11.GL_DEPTH_TEST);
			float partialTicks = Minecraft.getMinecraft().timer.renderPartialTicks;
			IIcon glyphs = null;
				switch (meta) {
				case 3:
					glyphs = LBlocks.machine.collectorGlyphs;
					break;
				case 4:
					glyphs = LBlocks.machine.distributorGlyphs;
					break;
				case 5:
					glyphs = LBlocks.machine.chargerGlyphs;
					break;
				case 6:
					glyphs = LBlocks.machine.faithPlateGlyphs;
					break;
				case 7:
					glyphs = LBlocks.machine.collectorGlyphs;
					break;
			}
			if (glyphs != null) {
				ItemRenderer ir = Minecraft.getMinecraft().entityRenderer.itemRenderer;
				EntityClientPlayerMP p = Minecraft.getMinecraft().thePlayer;
				float ticks;
				if (type == ItemRenderType.EQUIPPED) {
					ticks = 1;
				} else {
					ticks = (item == p.getHeldItem() ? ir.prevEquippedProgress + (ir.equippedProgress - ir.prevEquippedProgress) * partialTicks : 0);
				}
				float x = -0.05f;
				float y = 0.025f;
				float z = 0f;
				if (type == ItemRenderType.ENTITY) {
					x = -0.5f;
					y = -0.5f;
					z = -0.5f;
				} else if (type == ItemRenderType.INVENTORY) {
					y = -0.1f;
				}
				EldritchTileEntitySpecialRenderer.renderEldritchBlock(x, y, z,
						partialTicks, ticks, 0, 0, glyphs, 0,
						p.ticksExisted+partialTicks,
						item.hashCode(), type == ItemRenderType.INVENTORY, false, true);
			}
		}
		GL11.glDisable(GL11.GL_BLEND);
		GL11.glEnable(GL11.GL_TEXTURE_2D);
		GL11.glEnable(GL11.GL_CULL_FACE);
		GL11.glDepthMask(true);
	}

}

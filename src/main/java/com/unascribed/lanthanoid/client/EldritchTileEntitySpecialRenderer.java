package com.unascribed.lanthanoid.client;

import org.lwjgl.opengl.GL11;

import com.unascribed.lanthanoid.Lanthanoid;
import com.unascribed.lanthanoid.init.LBlocks;
import com.unascribed.lanthanoid.tile.TileEntityEldritch;
import com.unascribed.lanthanoid.tile.TileEntityEldritchFaithPlate;
import com.unascribed.lanthanoid.tile.TileEntityEldritchWithBooks;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;

public class EldritchTileEntitySpecialRenderer extends TileEntitySpecialRenderer {

	private static ModelSimpleBook book = new ModelSimpleBook();
	
	private static final ResourceLocation bookTex = new ResourceLocation("textures/entity/enchanting_table_book.png");
	
	public static void renderEldritchBlock(float x, float y, float z, float partialTicks,
			float playerAnim, float glyphCount, int bookCount, IIcon glyphs, int brightness, float t, int hash,
			boolean inventory, boolean blink) {
		float q = Math.max(0.25f, playerAnim);
		float q2 = Math.max(0.25f, playerAnim*glyphCount);
		
		
		GL11.glPushMatrix();
			GL11.glDisable(GL11.GL_LIGHTING);
			
			GL11.glEnable(GL11.GL_BLEND);
			GL11.glDepthMask(true);
			OpenGlHelper.glBlendFunc(770, 771, 1, 0);
			
			/*if (te.getBlockMetadata() == 5) {
				GL11.glPushMatrix();
					GL11.glColor3f(0, 1, 0);
					GL11.glTranslatef(x, y+1.05f, z+1);
					GL11.glRotatef(90f, -1, 0, 0);
					drawExtrudedIcon(LBlocks.machine.coil, 0.05f);
				GL11.glPopMatrix();
			}*/
			
			int j = brightness % 65536;
			int k = brightness / 65536;
			
			OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, j+((240f-j)*playerAnim), k+((240f-k)*playerAnim));
			
			Minecraft.getMinecraft().renderEngine.bindTexture(TextureMap.locationBlocksTexture);
			
			float r = q2;
			float g = q-playerAnim;
			float b = q-(playerAnim*glyphCount);
			float a = 0.5f;
			
			if (glyphCount >= 1) {
				a = 1;
			}
			if (blink) {
				r = ((MathHelper.sin(t/((hash%5)+3))+1)/4)+0.5f;
				g = 0;
				b = 0;
			}
			
			GL11.glPushMatrix();
				GL11.glTranslatef(x, y+0.75f, z);
				for (int i = 0; i < (inventory ? 2 : 4); i++) {
					GL11.glRotatef(-90f, 0, 1, 0);
					GL11.glTranslatef(0, 0, -1f);
					
					t += 67;
					
					float sin = MathHelper.sin(t/20);
					
					GL11.glPushMatrix();
						GL11.glRotatef(180f, 0, 1, 0);
						GL11.glTranslatef(-0.5f, 0, (0.09f*playerAnim)+0.01f);
						if (glyphCount < 1) {
							GL11.glRotatef(((sin*10)-15)*playerAnim, 1, 0, 0);
							GL11.glRotatef((sin*4)*playerAnim, 0, 0, 1);
						} else {
							GL11.glRotatef(-15*playerAnim, 1, 0, 0);
						}
						GL11.glTranslatef(-0.5f, -0.25f, 0);
						GL11.glScalef(1, 0.5f, 1);
						GL11.glColor4f(r, g, b, a/3);
						if (!inventory) {
							GL11.glDepthMask(false);
							drawExtrudedHalfIcon(glyphs, 0.5f);
							GL11.glDepthMask(true);
						}
						GL11.glTranslatef(0, 0, 0.05f);
						GL11.glColor4f(r, g, b, a);
						drawExtrudedHalfIcon(glyphs, 0.05f);
						/*tessellator.startDrawingQuads();
						tessellator.setColorOpaque_F(q, q-playerAnim, q);
						tessellator.addVertexWithUV(-0.5, -0.25, 0, minU, maxV);
						tessellator.addVertexWithUV(0.5, -0.25, 0, maxU, maxV);
						tessellator.addVertexWithUV(0.5, 0.25, 0, maxU, minV);
						tessellator.addVertexWithUV(-0.5, 0.25, 0, minU, minV);
						tessellator.draw();*/
					GL11.glPopMatrix();
					
				}
			GL11.glPopMatrix();
			
			GL11.glColor3f(1, 1, 1);
			GL11.glDisable(GL11.GL_BLEND);
			
			OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, j / 1.0F, k / 1.0F);
			
			boolean moveX = false;
			int moveY = 0;
			for (int i = 0; i < bookCount; i++) {
				GL11.glPushMatrix();
					Minecraft.getMinecraft().renderEngine.bindTexture(bookTex);
					GL11.glTranslatef(x+0.0625f+(moveX ? 0.5f : 0f)+(moveY*0.05f), y+1.07f+(moveY*0.13f), z+0.5f);
					GL11.glRotatef(90f, 1, 0, 0);
					GL11.glRotatef((((i*67)^hash)%60)-30, 0, 0, 1);
					book.render(null, 0, 0, 1, 0, 0.0F, 0.0625F);
				GL11.glPopMatrix();
				if (!moveX) {
					moveX = true;
				} else {
					moveX = false;
					moveY++;
				}
			}
			GL11.glDepthMask(false);
			GL11.glEnable(GL11.GL_LIGHTING);
		GL11.glPopMatrix();
		GL11.glDepthMask(true);
	}
	
	@Override
	public void renderTileEntityAt(TileEntity teraw, double xdouble, double ydouble, double zdouble, float partialTicks) {
		float x = (float)xdouble;
		float y = (float)ydouble;
		float z = (float)zdouble;
		IIcon glyphs = null;
		switch (teraw.getBlockMetadata()) {
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
		if (glyphs == null || !(teraw instanceof TileEntityEldritch)) {
			if (teraw.hasWorldObj()) {
				Lanthanoid.log.error("WRONG TILE ENTITY AT {}, {}, {} IN {} (Normally, this would have crashed your game. Maybe log spam is worse. Who knows.)", teraw.xCoord, teraw.yCoord, teraw.zCoord, teraw.getWorld().provider.getDimensionName());
			}
			// if there's no world object, it's not worth complaining.
			// we're stuck in some crappy fakeworld and it's not getting better.
			return;
		}
		TileEntityEldritch te = (TileEntityEldritch)teraw;
		
		float playerAnim = te.playerAnim;
		if (te.playersNearby) {
			if (playerAnim < 20) {
				playerAnim += partialTicks;
			}
		} else {
			if (playerAnim > partialTicks) {
				playerAnim -= partialTicks;
			}
		}
		playerAnim /= 20;
		
		float glyphCount = Math.max(1, Math.min(te.getMilliglyphs(), te.getMaxMilliglyphs()))/(float)te.getMaxMilliglyphs();
		int brightness = te.getWorld().getLightBrightnessForSkyBlocks(te.xCoord, te.yCoord+1, te.zCoord, 0);
		float t = (te.ticksExisted+partialTicks)+((te.xCoord*31)+(te.yCoord*31)+(te.zCoord*31));
		
		int books = 0;
		
		if (te instanceof TileEntityEldritchWithBooks) {
			books = ((TileEntityEldritchWithBooks) te).getBookCount();
		}
		
		renderEldritchBlock(x, y, z, partialTicks, playerAnim, glyphCount, books, glyphs,
				brightness, t,
				te.hashCode(), false, te.getBlockMetadata() == 4 && te.getWorld().getBlockPowerInput(te.xCoord, te.yCoord, te.zCoord) > 0);
		
		if (Minecraft.getMinecraft().gameSettings.showDebugInfo && Minecraft.getMinecraft().thePlayer.capabilities.isCreativeMode) {
			String str = (te.getMilliglyphs()/1000)+"."+((te.getMilliglyphs()%1000)/10);
			FontRenderer fr = Minecraft.getMinecraft().fontRendererObj;
			
			GL11.glPushMatrix();
				GL11.glTranslatef(x+0.5f, y+1.85f, z+0.5f);
				GL11.glRotatef(t*4, 0, 1, 0);
				GL11.glScalef(0.025f, -0.025f, 0.025f);
				fr.drawString(str, -fr.getStringWidth(str)/2, 0, -1);
				
				GL11.glRotatef(180, 0, 1, 0);
				fr.drawString(str, -fr.getStringWidth(str)/2, 0, -1);
			GL11.glPopMatrix();
			
			String str2 = "[debug]";
			GL11.glPushMatrix();
				GL11.glTranslatef(x+0.5f, y+2.0f, z+0.5f);
				GL11.glRotatef(t*4, 0, 1, 0);
				GL11.glScalef(0.0125f, -0.0125f, 0.0125f);
				fr.drawString(str2, -fr.getStringWidth(str2)/2, 0, -1);
				
				GL11.glRotatef(180, 0, 1, 0);
				fr.drawString(str2, -fr.getStringWidth(str2)/2, 0, -1);
			GL11.glPopMatrix();
			
			String str3 = "(max: "+(te.getMaxMilliglyphs()/1000)+"."+((te.getMaxMilliglyphs()%1000)/10)+")";
			GL11.glPushMatrix();
				GL11.glTranslatef(x+0.5f, y+1.6f, z+0.5f);
				GL11.glRotatef(t*4, 0, 1, 0);
				GL11.glScalef(0.0125f, -0.0125f, 0.0125f);
				fr.drawString(str3, -fr.getStringWidth(str3)/2, 0, -1);
				
				GL11.glRotatef(180, 0, 1, 0);
				fr.drawString(str3, -fr.getStringWidth(str3)/2, 0, -1);
			GL11.glPopMatrix();
		}
		
		if (teraw instanceof TileEntityEldritchFaithPlate) {
			TileEntityEldritchFaithPlate tefp = (TileEntityEldritchFaithPlate) teraw;
			int len = tefp.getAmountStacked();
			GL11.glPushMatrix();
				Minecraft.getMinecraft().renderEngine.bindTexture(TextureMap.locationBlocksTexture);
				int j = brightness % 65536;
				int k = brightness / 65536;
				OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, j / 1.0F, k / 1.0F);
				Tessellator tess = Tessellator.instance;
				RenderBlocks rb = RenderBlocks.getInstance();
				rb.blockAccess = te.getWorld();
				float q = (10*len);
				float ofs = (Math.abs(MathHelper.sin((Math.min(q, tefp.bounceAnimTicks+(6*len)+partialTicks)/q)*(float)Math.PI))/2)*len;
				GL11.glTranslatef(x-te.xCoord, y+ofs-te.yCoord+0.025f, z-te.zCoord);
				GL11.glDisable(GL11.GL_LIGHTING);
				tess.startDrawingQuads();
				rb.setOverrideBlockTexture(LBlocks.machine.getIcon(1, 6));
				rb.setRenderBounds(6/16f, (15/16f)-ofs, 6/16f, 10/16f, 15/16f, 10/16f);
				rb.lockBlockBounds = true;
				rb.renderBlockAllFaces(LBlocks.machine, te.xCoord, te.yCoord, te.zCoord);
				rb.lockBlockBounds = false;
				rb.setRenderBounds(0.01, 0.9375, 0.01, 0.99, 0.99, 0.99);
				rb.lockBlockBounds = true;
				rb.renderBlockAllFaces(LBlocks.machine, te.xCoord, te.yCoord, te.zCoord);
				rb.lockBlockBounds = false;
				tess.draw();
			GL11.glPopMatrix();
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

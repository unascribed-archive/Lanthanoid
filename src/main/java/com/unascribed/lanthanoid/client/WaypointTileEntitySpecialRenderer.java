package com.unascribed.lanthanoid.client;

import org.lwjgl.opengl.GL11;

import com.unascribed.lanthanoid.Lanthanoid;

import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;

public class WaypointTileEntitySpecialRenderer extends TileEntitySpecialRenderer {
	private static final ResourceLocation error = new ResourceLocation("lanthanoid", "textures/no_te.png");

	@Override
	public void renderTileEntityAt(TileEntity te, double x, double y, double z, float partialTicks) {
		Tessellator tessellator = Tessellator.instance;
		if (!Lanthanoid.inst.waypointManager.hasWaypoint(te.getWorld(), te.xCoord, te.yCoord, te.zCoord)) {
			GL11.glPushMatrix();
			GL11.glDisable(GL11.GL_LIGHTING);
			float t = (te.getWorld().getTotalWorldTime()+partialTicks)+((te.xCoord*31)+(te.yCoord*31)+(te.zCoord*31));
			float sin = (float)(Math.sin(t/20)+1)/4;
			GL11.glTranslatef((float)x+0.5f, (float)y+1.25f+sin, (float)z+0.5f);
			GL11.glRotatef(t*4, 0, 1, 0);
			bindTexture(error);
			tessellator.startDrawingQuads();
			tessellator.setColorOpaque_I(-1);
			tessellator.addVertexWithUV(-0.5, -0.5, 0, 0, 1);
			tessellator.addVertexWithUV(0.5, -0.5, 0, 1, 1);
			tessellator.addVertexWithUV(0.5, 0.5, 0, 1, 0);
			tessellator.addVertexWithUV(-0.5, 0.5, 0, 0, 0);
			tessellator.draw();
			
			GL11.glRotatef(180, 0, 1, 0);
			tessellator.startDrawingQuads();
			tessellator.setColorOpaque_I(-1);
			tessellator.addVertexWithUV(-0.5, -0.5, 0, 0, 1);
			tessellator.addVertexWithUV(0.5, -0.5, 0, 1, 1);
			tessellator.addVertexWithUV(0.5, 0.5, 0, 1, 0);
			tessellator.addVertexWithUV(-0.5, 0.5, 0, 0, 0);
			tessellator.draw();
			GL11.glEnable(GL11.GL_LIGHTING);
			GL11.glPopMatrix();
			return;
		}
	}

}

package com.unascribed.lanthanoid.client.render.block;

import com.unascribed.lanthanoid.init.LBlocks;
import com.unascribed.lanthanoid.proxy.ClientProxy;

import cpw.mods.fml.client.registry.ISimpleBlockRenderingHandler;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.MathHelper;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.common.util.ForgeDirection;

public class TechnicalRenderer implements ISimpleBlockRenderingHandler {
	@Override
	public void renderInventoryBlock(Block block, int metadata, int modelId, RenderBlocks renderer) {

	}

	@Override
	public boolean renderWorldBlock(IBlockAccess world, int x, int y, int z, Block block, int modelId, RenderBlocks renderer) {
		Tessellator tess = Tessellator.instance;
		tess.addTranslation(x, y, z);
		tess.setColorOpaque_I(-1);
		int highBits = 0xAAAAAAAA;
		int lowBits  = 0x55555555;
		
		int mixA = (x & highBits) * (z & lowBits);
		int mixB = (z & highBits) * (x & lowBits);
		
		mixA = (int)Math.abs(MathHelper.sin((float) ((mixA+y)%(Math.PI*2)))*1024);
		mixB = (int)Math.abs(MathHelper.cos((float) (mixB%(Math.PI*2)))*1024);
		int hash = (mixA *3) ^ mixB;
		if (hash<0) hash = -hash;
		int wanted = hash % 25;
		
		float wantedU = (wanted % 5)/5f;
		float wantedV = (wanted / 5)/5f;
		
		float minUAbs = LBlocks.technical.glyphs.getMinU();
		float minVAbs = LBlocks.technical.glyphs.getMinV();
		float maxUDiff = LBlocks.technical.glyphs.getMaxU()-minUAbs;
		float maxVDiff = LBlocks.technical.glyphs.getMaxV()-minVAbs;
		float minU = minUAbs + (maxUDiff*wantedU);
		float minV = minVAbs + (maxVDiff*wantedV);
		float maxU = minU + (maxUDiff/5);
		float maxV = minV + (maxVDiff/5);
		
		minU += maxUDiff/1600;
		minV += maxVDiff/1600;
		maxU -= maxUDiff/1600;
		maxV -= maxVDiff/1600;
		
		double start = 0.25;
		double end = 0.75;
		
		if (world.getBlock(x, y-1, z) != null && world.getBlock(x, y-1, z).isSideSolid(world, x, y-1, z, ForgeDirection.UP)) {
			tess.addVertexWithUV(start, 0.01, end, minU, maxV);
			tess.addVertexWithUV(end, 0.01, end, maxU, maxV);
			tess.addVertexWithUV(end, 0.01, start, maxU, minV);
			tess.addVertexWithUV(start, 0.01, start, minU, minV);
		}
		if (world.getBlock(x, y+1, z) != null && world.getBlock(x, y+1, z).isSideSolid(world, x, y+1, z, ForgeDirection.DOWN)) {
			tess.addVertexWithUV(start, 0.99, start, minU, maxV);
			tess.addVertexWithUV(end, 0.99, start, maxU, maxV);
			tess.addVertexWithUV(end, 0.99, end, maxU, minV);
			tess.addVertexWithUV(start, 0.99, end, minU, minV);
		}
		
		if (world.getBlock(x-1, y, z) != null && world.getBlock(x-1, y, z).isSideSolid(world, x-1, y, z, ForgeDirection.EAST)) {
			tess.addVertexWithUV(0.01, end, end, maxU, minV);
			tess.addVertexWithUV(0.01, start, end, maxU, maxV);
			tess.addVertexWithUV(0.01, start, start, minU, maxV);
			tess.addVertexWithUV(0.01, end, start, minU, minV);
		}
		if (world.getBlock(x+1, y, z) != null && world.getBlock(x+1, y, z).isSideSolid(world, x+1, y, z, ForgeDirection.WEST)) {
			tess.addVertexWithUV(0.99, start, start, minU, maxV);
			tess.addVertexWithUV(0.99, start, end, maxU, maxV);
			tess.addVertexWithUV(0.99, end, end, maxU, minV);
			tess.addVertexWithUV(0.99, end, start, minU, minV);
		}
		
		if (world.getBlock(x, y, z-1) != null && world.getBlock(x, y, z-1).isSideSolid(world, x, y, z-1, ForgeDirection.NORTH)) {
			tess.addVertexWithUV(end, start, 0.01, minU, maxV);
			tess.addVertexWithUV(end, end, 0.01, minU, minV);
			tess.addVertexWithUV(start, end, 0.01, maxU, minV);
			tess.addVertexWithUV(start, start, 0.01, maxU, maxV);
			
		}
		if (world.getBlock(x, y, z+1) != null && world.getBlock(x, y, z+1).isSideSolid(world, x, y, z+1, ForgeDirection.SOUTH)) {
			tess.addVertexWithUV(start, start, 0.99, maxU, maxV);
			tess.addVertexWithUV(start, end, 0.99, maxU, minV);
			tess.addVertexWithUV(end, end, 0.99, minU, minV);
			tess.addVertexWithUV(end, start, 0.99, minU, maxV);
		}
		tess.addTranslation(-x, -y, -z);
		return false;
	}

	@Override
	public boolean shouldRender3DInInventory(int modelId) {
		return false;
	}

	@Override
	public int getRenderId() {
		return ClientProxy.technicalRenderId;
	}

}

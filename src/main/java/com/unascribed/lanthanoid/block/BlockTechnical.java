package com.unascribed.lanthanoid.block;

import java.util.Random;

import com.unascribed.lanthanoid.effect.EntityRifleFX;
import com.unascribed.lanthanoid.item.rifle.PrimaryMode;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.init.Blocks;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockTechnical extends Block {

	public BlockTechnical() {
		super(Material.circuits);
		setTickRandomly(true);
	}
	
	@Override
	public boolean isAir(IBlockAccess world, int x, int y, int z) {
		return true;
	}
	
	@Override
	public int getRenderType() {
		return -1;
	}

	@Override
	public AxisAlignedBB getCollisionBoundingBoxFromPool(World p_149668_1_, int p_149668_2_, int p_149668_3_, int p_149668_4_) {
		return null;
	}

	@Override
	public boolean isOpaqueCube() {
		return false;
	}
	
	@Override
	public boolean isCollidable() {
		return false;
	}

	@Override
	public void dropBlockAsItemWithChance(World p_149690_1_, int p_149690_2_, int p_149690_3_, int p_149690_4_, int p_149690_5_, float p_149690_6_, int p_149690_7_) {
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public void randomDisplayTick(World world, int x, int y, int z, Random rand) {
		if (world.getBlockMetadata(x, y, z) == 0) {
			PrimaryMode mode = PrimaryMode.LIGHT;
			float r = ((mode.color >> 16)&0xFF)/255f;
			float g = ((mode.color >> 8)&0xFF)/255f;
			float b = (mode.color&0xFF)/255f;
			for (int i = 0; i < 2; i++) {
				EntityRifleFX fx = new EntityRifleFX(Minecraft.getMinecraft().theWorld, (x+0.5)+(rand.nextDouble()-0.5), (y+0.5)+(rand.nextDouble()-0.5), (z+0.5)+(rand.nextDouble()-0.5), 1.0f, 0, 0, 0);
				fx.motionX = fx.motionY = fx.motionZ = 0;
				fx.setRBGColorF(r, g, b);
				Minecraft.getMinecraft().effectRenderer.addEffect(fx);
			}
		}
	}
	
	@Override
	public int getLightValue(IBlockAccess world, int x, int y, int z) {
		return world.getBlockMetadata(x, y, z) == 0 ? 10 : 0;
	}
	
	@Override
	public void updateTick(World world, int x, int y, int z, Random rand) {
		world.setBlock(x, y, z, Blocks.air, 0, 2);
	}
}

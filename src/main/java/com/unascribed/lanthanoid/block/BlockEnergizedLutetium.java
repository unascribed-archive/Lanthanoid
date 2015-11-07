package com.unascribed.lanthanoid.block;

import java.util.Random;

import com.unascribed.lanthanoid.effect.LightningArcFX;
import com.unascribed.lanthanoid.effect.LightningFX;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.relauncher.Side;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockEnergizedLutetium extends BlockBase {
	public BlockEnergizedLutetium() {
		super(Material.iron);
		setHarvestLevel("pickaxe", 1);
		setHardness(5);
		setResistance(10);
	}

	@Override
	public void setBlockBoundsBasedOnState(IBlockAccess world, int x, int y, int z) {
		double minX = 0.3;
		double minY = 0.3;
		double minZ = 0.3;
		double maxX = 0.6;
		double maxY = 0.6;
		double maxZ = 0.6;
		if (attachTo(world.getBlock(x-1, y, z))) {
			minX = 0;
		}
		if (attachTo(world.getBlock(x+1, y, z))) {
			maxX = 1;
		}
		if (attachTo(world.getBlock(x, y-1, z))) {
			minY = 0;
		}
		if (attachTo(world.getBlock(x, y+1, z))) {
			maxY = 1;
		}
		if (attachTo(world.getBlock(x, y, z-1))) {
			minZ = 0;
		}
		if (attachTo(world.getBlock(x, y, z+1))) {
			maxZ = 1;
		}
		setBlockBounds((float)minX, (float)minY, (float)minZ, (float)maxX, (float)maxY, (float)maxZ);
	}
	
	@Override
	public int getLightValue() {
		return 7;
	}
	
	@Override
	public void setBlockBoundsForItemRender() {
		setBlockBounds(0.25f, 0.25f, 0.25f, 0.75f, 0.75f, 0.75f);
	}
	
	private boolean attachTo(Block block) {
		return block == this || block.isOpaqueCube();
	}

	@Override
	public float getPlayerRelativeBlockHardness(EntityPlayer player, World world, int x, int y, int z) {
		ItemStack held = player.getHeldItem();
		if (held == null) {
			player.attackEntityFrom(zap, 4);
			if (FMLCommonHandler.instance().getEffectiveSide() == Side.CLIENT) {
				bzztToward(world, x, y, z, player, world.rand);
			}
		} else if (held.getItem().getHarvestLevel(held, "pickaxe") > 1 || held.getItem() == Items.golden_pickaxe) {
			player.attackEntityFrom(zap, 2);
			if (FMLCommonHandler.instance().getEffectiveSide() == Side.CLIENT) {
				bzztToward(world, x, y, z, player, world.rand);
			}
		}
		return super.getPlayerRelativeBlockHardness(player, world, x, y, z);
	}
	
	@Override
	public boolean isBlockNormalCube() {
		return false;
	}
	
	@Override
	public boolean isOpaqueCube() {
		return false;
	}
	
	@Override
	public boolean isNormalCube() {
		return false;
	}
	
	@Override
	public boolean isNormalCube(IBlockAccess world, int x, int y, int z) {
		return false;
	}
	
	@Override
	public boolean shouldSideBeRendered(IBlockAccess p_149646_1_, int p_149646_2_, int p_149646_3_, int p_149646_4_, int p_149646_5_) {
		return true;
	}
	
	@Override
	public AxisAlignedBB getCollisionBoundingBoxFromPool(World world, int x, int y, int z) {
		setBlockBoundsBasedOnState(world, x, y, z);
		return AxisAlignedBB.getBoundingBox(x + minX, y + minY, z + minZ, x + maxX, y + maxY, z + maxZ);
	}
	
	/*@Override
	public void addCollisionBoxesToList(World p_149743_1_, int p_149743_2_, int p_149743_3_, int p_149743_4_, AxisAlignedBB p_149743_5_, List p_149743_6_, Entity p_149743_7_) {
		setBlockBoundsBasedOnState(p_149743_1_, p_149743_2_, p_149743_2_, p_149743_4_);
		super.addCollisionBoxesToList(p_149743_1_, p_149743_2_, p_149743_3_, p_149743_4_, p_149743_5_, p_149743_6_, p_149743_7_);
	}*/
	
	private final DamageSource zap = new DamageSource("zap");
	
	@Override
	public void onEntityCollidedWithBlock(World world, int x, int y, int z, Entity entity) {
		if (entity instanceof EntityLivingBase) {
			EntityLivingBase elb = ((EntityLivingBase)entity);
			if (FMLCommonHandler.instance().getEffectiveSide() == Side.CLIENT) {
				bzztToward(world, x, y, z, elb, world.rand);
			}
			if (elb instanceof EntityPlayer && ((EntityPlayer)elb).capabilities.disableDamage) return;
			if (!elb.isEntityInvulnerable()) {
				elb.attackEntityFrom(zap, 2);
			}
		}
	}
	
	private void bzztToward(World world, int x, int y, int z, Entity e, Random rand) {
		bzztToward(world, x, y, z, e.posX+(world.rand.nextGaussian()*(e.width/2)),
				e.posY+(world.rand.nextGaussian()*(e.height/2)), e.posZ+(world.rand.nextGaussian()*(e.width/2)), rand);
	}

	@Override
	public void randomDisplayTick(World w, int x, int y, int z, Random r) {
		if (r.nextInt(24) != 0) return;
		bzzt(w, x, y, z, r);
	}
	
	public void bzztToward(World w, int x, int y, int z, double ex, double ey, double ez, Random r) {
		Minecraft.getMinecraft().getSoundHandler().playSound(new PositionedSoundRecord(new ResourceLocation("lanthanoid", "spark"),
				0.3f, 1.0f+(r.nextFloat()/4),
				x+0.5f, y+0.5f, z+0.5f));
		for (int i = 0; i < 4; i++) {
			LightningArcFX fx = new LightningArcFX(w, x+0.5, y+0.5, z+0.5, ex, ey, ez, 220/255D, 255/255D, 87/255D);
			Minecraft.getMinecraft().effectRenderer.addEffect(fx);
		}
	}
	
	public void bzzt(World w, int x, int y, int z, Random r) {
		Minecraft.getMinecraft().getSoundHandler().playSound(new PositionedSoundRecord(new ResourceLocation("lanthanoid", "spark"),
				0.3f, 1.0f+(r.nextFloat()/4),
				x+0.5f, y+0.5f, z+0.5f));
		for (int i = 0; i < 4; i++) {
			LightningFX fx = new LightningFX(w, x+0.5, y+0.5, z+0.5, 220/255D, 255/255D, 87/255D);
			Minecraft.getMinecraft().effectRenderer.addEffect(fx);
		}
	}
	
}

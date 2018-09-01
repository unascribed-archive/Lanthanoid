package com.unascribed.lanthanoid.item;

import net.minecraft.block.Block;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.event.world.BlockEvent;

import com.unascribed.lanthanoid.Lanthanoid;

public class ItemWreckingBall extends ItemBase {
	public ItemWreckingBall() {
		super(Lanthanoid.inst.creativeTabEquipment);
		setHarvestLevel("pickaxe", 4);
		setHarvestLevel("shovel", 4);
		setUnlocalizedName("wrecking_ball");
		setFull3D();
		setTextureName("lanthanoid:dysprosium_wrecking_ball");
	}
	
	@Override
	public int getItemEnchantability() {
		return 12;
	}
	
	@Override
	public boolean isDamageable() {
		return true;
	}
	
	@Override
	public int getMaxDurability() {
		return 32768;
	}
	
	@Override
	public float getDigSpeed(ItemStack itemstack, Block block, int metadata) {
		return block.isToolEffective("pickaxe", metadata) || block.isToolEffective("shovel", metadata) ? 5000f : 1f;
	}
	
	private boolean breaking = false;
	
	@Override
	public boolean shouldRotateAroundWhenRendering() {
		return true;
	}
	
	@Override
	public boolean onBlockDestroyed(ItemStack stack, World world, Block block, int x, int y, int z, EntityLivingBase destroyer) {
		if (breaking) return false;
		if (!(destroyer instanceof EntityPlayerMP)) return false;
		if (destroyer.isSneaking()) {
			breaking = true;
			harvest((EntityPlayerMP)destroyer, world, x, y, z, itemRand.nextInt(4) == 0);
			breaking = false;
			return true;
		}
		breaking = true;
		for (int oX = -1; oX <= 1; oX++) {
			for (int oY = -1; oY <= 1; oY++) {
				for (int oZ = -1; oZ <= 1; oZ++) {
					harvest((EntityPlayerMP)destroyer, world, x+oX, y+oY, z+oZ, itemRand.nextInt(4) == 0);
				}
			}
		}
		breaking = false;
		return true;
	}
	
	private boolean harvest(EntityPlayerMP player, World world, int x, int y, int z, boolean drop) {
		BlockEvent.BreakEvent event = ForgeHooks.onBlockBreakEvent(world, player.theItemInWorldManager.getGameType(), player, x, y, z);
		if (event.isCanceled()) {
			return false;
		} else {
			Block block = world.getBlock(x, y, z);
			int meta = world.getBlockMetadata(x, y, z);
			if (block.getBlockHardness(world, x, y, z) < 0)
				return false;
			world.playAuxSFX(2001, x, y, z, Block.getIdFromBlock(block) + (world.getBlockMetadata(x, y, z) << 12));
			block.onBlockHarvested(world, x, y, z, meta, player);
			boolean success = block.removedByPlayer(world, player, x, y, z, true);

			if (success) {
				block.onBlockDestroyedByPlayer(world, x, y, z, meta);
				if (drop) {
					block.harvestBlock(world, player, x, y, z, meta);
					block.dropXpOnBlockBreak(world, x, y, z, event.getExpToDrop() != 0 ? event.getExpToDrop() : block.getExpDrop(world, meta, 0));
				}
				player.getHeldItem().damageItem(1, player);
			}
			return success;
		}
	}
	
}

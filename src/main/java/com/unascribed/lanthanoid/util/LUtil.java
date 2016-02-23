package com.unascribed.lanthanoid.util;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.C07PacketPlayerDigging;
import net.minecraft.network.play.server.S23PacketBlockChange;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.event.world.BlockEvent;

public class LUtil {

	public static boolean harvest(EntityPlayerMP player, World world, int x, int y, int z, ItemStack stack, boolean drop, boolean particles, boolean degrade) {
		BlockEvent.BreakEvent event = ForgeHooks.onBlockBreakEvent(world, player.theItemInWorldManager.getGameType(), player, x, y, z);
		if (event.isCanceled()) {
			return false;
		} else {
			Block block = world.getBlock(x, y, z);
			int meta = world.getBlockMetadata(x, y, z);
			if (degrade) {
				if (block == Blocks.cobblestone) {
					block = Blocks.gravel;
				} else if (block == Blocks.stone) {
					block = Blocks.cobblestone;
				} else if (block == Blocks.gravel) {
					block = Blocks.sand;
				}
			}
			if (block.getBlockHardness(world, x, y, z) < 0) {
				return false;
			}
			if (particles) {
				world.playAuxSFX(2001, x, y, z, Block.getIdFromBlock(block) + (world.getBlockMetadata(x, y, z) << 12));
			}
			block.onBlockHarvested(world, x, y, z, meta, player);
			boolean success = block.removedByPlayer(world, player, x, y, z, true);
	
			if (success) {
				block.onBlockDestroyedByPlayer(world, x, y, z, meta);
				if (drop) {
					block.harvestBlock(world, player, x, y, z, meta);
					block.dropXpOnBlockBreak(world, x, y, z, event.getExpToDrop() != 0 ? event.getExpToDrop() : block.getExpDrop(world, meta, 0));
				}
				stack.damageItem(1, player);
			}
			return success;
		}
	}

	public static MovingObjectPosition rayTrace(EntityLivingBase entity, double distance) {
		return rayTrace(entity, distance, false, false, true);
	}
	
	public static MovingObjectPosition rayTrace(EntityLivingBase entity, double distance, boolean stopOnLiquid, boolean ignoreBlockWithoutBoundingBox, boolean returnLastUncollidableBlock) {
		Vec3 vec3;
		if (entity instanceof EntityPlayer) {
			vec3 = Vec3.createVectorHelper(entity.posX, entity.posY + entity.getEyeHeight(), entity.posZ);
		} else {
			vec3 = Vec3.createVectorHelper(entity.posX, entity.posY, entity.posZ);
		}
		Vec3 vec31 = entity.getLookVec();
		Vec3 vec32 = vec3.addVector(vec31.xCoord * distance, vec31.yCoord * distance, vec31.zCoord * distance);
		return entity.worldObj.rayTraceBlocks(vec3, vec32, stopOnLiquid, ignoreBlockWithoutBoundingBox, returnLastUncollidableBlock);
	}
	
	public static void breakExtraBlock(World world, int x, int y, int z, int sidehit, EntityPlayer playerEntity, int refX, int refY, int refZ) {
		// prevent calling that stuff for air blocks, could lead to unexpected behaviour since it fires events
		if (world.isAirBlock(x, y, z))
			return;

		// what?
		if(!(playerEntity instanceof EntityPlayerMP))
			return;
		EntityPlayerMP player = (EntityPlayerMP) playerEntity;

		// check if the block can be broken, since extra block breaks shouldn't instantly break stuff like obsidian
		// or precious ores you can't harvest while mining stone
		Block block = world.getBlock(x, y, z);
		int meta = world.getBlockMetadata(x, y, z);

		// only effective materials
		if (!isEffective(player.getCurrentEquippedItem(), block, meta))
			return;

		Block refBlock = world.getBlock(refX, refY, refZ);
		float refStrength = ForgeHooks.blockStrength(refBlock, player, world, refX, refY, refZ);
		float strength = ForgeHooks.blockStrength(block, player, world, x,y,z);

		// only harvestable blocks that aren't impossibly slow to harvest
		if (!ForgeHooks.canHarvestBlock(block, player, meta) || refStrength/strength > 10f)
			return;

		// send the blockbreak event
		BlockEvent.BreakEvent event = ForgeHooks.onBlockBreakEvent(world, player.theItemInWorldManager.getGameType(), player, x,y,z);
		if(event.isCanceled())
			return;

		if (player.capabilities.isCreativeMode) {
			block.onBlockHarvested(world, x, y, z, meta, player);
			if (block.removedByPlayer(world, player, x, y, z, false))
				block.onBlockDestroyedByPlayer(world, x, y, z, meta);

			// send update to client
			if (!world.isRemote) {
				player.playerNetServerHandler.sendPacket(new S23PacketBlockChange(x, y, z, world));
			}
			return;
		}

		// callback to the tool the player uses. Called on both sides. This damages the tool n stuff.
		player.getCurrentEquippedItem().onBlockDestroyed(world, block, x, y, z, player);

		// server sided handling
		if (!world.isRemote) {
			// serverside we reproduce ItemInWorldManager.tryHarvestBlock

			// ItemInWorldManager.removeBlock
			block.onBlockHarvested(world, x,y,z, meta, player);

			if(block.removedByPlayer(world, player, x,y,z, true)) // boolean is if block can be harvested, checked above
			{
				block.onBlockDestroyedByPlayer( world, x,y,z, meta);
				block.harvestBlock(world, player, x,y,z, meta);
				block.dropXpOnBlockBreak(world, x,y,z, event.getExpToDrop());
			}

			// always send block update to client
			player.playerNetServerHandler.sendPacket(new S23PacketBlockChange(x, y, z, world));
		}
		// client sided handling
		else {
			//PlayerControllerMP pcmp = Minecraft.getMinecraft().playerController;
			// clientside we do a "this clock has been clicked on long enough to be broken" call. This should not send any new packets
			// the code above, executed on the server, sends a block-updates that give us the correct state of the block we destroy.

			// following code can be found in PlayerControllerMP.onPlayerDestroyBlock
			world.playAuxSFX(2001, x, y, z, Block.getIdFromBlock(block) + (meta << 12));
			if(block.removedByPlayer(world, player, x,y,z, true))
			{
				block.onBlockDestroyedByPlayer(world, x,y,z, meta);
			}
			// callback to the tool
			ItemStack itemstack = player.getCurrentEquippedItem();
			if (itemstack != null)
			{
				itemstack.onBlockDestroyed(world, block, x, y, z, player);

				if (itemstack.stackSize == 0)
				{
					player.destroyCurrentEquippedItem();
				}
			}

			// send an update to the server, so we get an update back
			Minecraft.getMinecraft().getNetHandler().addToSendQueue(new C07PacketPlayerDigging(2, x,y,z, Minecraft.getMinecraft().objectMouseOver.sideHit));
		}
	}
	

	public static boolean isEffective(ItemStack stack, Block block, int meta) {
		Item item = stack.getItem();
		if (item == null) return false;
		return item.canHarvestBlock(block, stack);
	}

}

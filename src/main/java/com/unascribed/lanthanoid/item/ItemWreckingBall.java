package com.unascribed.lanthanoid.item;

import com.google.common.collect.Multimap;
import com.unascribed.lanthanoid.Lanthanoid;
import com.unascribed.lanthanoid.function.IntSupplier;
import com.unascribed.lanthanoid.init.LAchievements;
import com.unascribed.lanthanoid.init.LItems;
import com.unascribed.lanthanoid.network.ItemBreakMessage;

import cpw.mods.fml.common.network.NetworkRegistry.TargetPoint;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.MovingObjectPosition.MovingObjectType;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.event.world.BlockEvent;

public class ItemWreckingBall extends ItemBase {
	public enum Material {
		YTTERBIUM( 2048, 1, 4,   20, 4),
		ERBIUM(    4096, 2, 8,   40, 8),
		DYSPROSIUM(8192, 3, 16, 100, 16),
		;
		public final String lowerName;
		public final int maxDamage, radius, enchantability, blocksBeforeCooldown, breaksPerSwing;
		private Material(int maxDamage, int radius, int enchantability, int blocksBeforeCooldown, int breaksPerSwing) {
			lowerName = name().toLowerCase();
			this.maxDamage = maxDamage;
			this.radius = radius;
			this.enchantability = enchantability;
			this.blocksBeforeCooldown = blocksBeforeCooldown;
			this.breaksPerSwing = breaksPerSwing;
		}
	}
	private int radius, enchantability, blocksBeforeCooldown, breaksPerSwing;
	public ItemWreckingBall(Material material) {
		setHarvestLevel("pickaxe", 4);
		setHarvestLevel("shovel", 4);
		setUnlocalizedName(material.lowerName+"_wrecking_ball");
		setFull3D();
		setTextureName("lanthanoid:"+material.lowerName+"_wrecking_ball");
		setMaxDurability(material.maxDamage);
		radius = material.radius;
		enchantability = material.enchantability;
		blocksBeforeCooldown = material.blocksBeforeCooldown;
		breaksPerSwing = material.breaksPerSwing;
	}
	
	@Override
	public Multimap getAttributeModifiers(ItemStack stack) {
		Multimap multimap = super.getAttributeModifiers(stack);
		multimap.put(SharedMonsterAttributes.attackDamage.getAttributeUnlocalizedName(), new AttributeModifier(itemModifierUUID, "Weapon modifier", 8, 0));
		return multimap;
	}
	
	@Override
	public int getItemStackLimit(ItemStack stack) {
		return 1;
	}
	
	@Override
	public int getItemEnchantability() {
		return enchantability;
	}
	
	@Override
	public boolean isDamageable() {
		return true;
	}
	
	@Override
	public boolean onLeftClickEntity(ItemStack stack, EntityPlayer player, Entity entity) {
		if (entity instanceof EntityLivingBase) {
			EntityLivingBase living = (EntityLivingBase)entity;
			PotionEffect eff = new PotionEffect(Potion.moveSlowdown.id, 100, 10);
			eff.getCurativeItems().clear();
			living.addPotionEffect(eff);
		}
		stack.damageItem(40, player);
		return false;
	}
	
	@Override
	public float getDigSpeed(ItemStack itemstack, Block block, int metadata) {
		try {
			return itemstack.hasTagCompound() && itemstack.getTagCompound().getInteger("cooldown") > 0 ? 0f : block.getBlockHardness(null, 0, 0, 0);//block.isToolEffective("pickaxe", metadata) || block.isToolEffective("shovel", metadata) ? 30f : 1.5f;
		} catch (NullPointerException e) {
			// in case some mod requires a world handle for hardness (sorry, I don't have one!)
			return 3f;
		}
	}
	
	//private boolean breaking = false;
	
	@Override
	public boolean shouldRotateAroundWhenRendering() {
		return true;
	}
	
	@Override
	public boolean onEntitySwing(EntityLivingBase entityLiving, ItemStack stack) {
		if (entityLiving.getHeldItem().hasTagCompound() && entityLiving.getHeldItem().getTagCompound().getInteger("cooldown") > 0) {
			return true;
		}
		if (entityLiving.worldObj.isRemote) {
			return false;
		}
		if (!(entityLiving instanceof EntityPlayerMP)) {
			return false;
		}
		EntityPlayerMP player = (EntityPlayerMP)entityLiving;
		MovingObjectPosition mop = getMovingObjectPositionFromPlayer(player.worldObj, player, false);
		if (mop != null && mop.typeOfHit == MovingObjectType.BLOCK) {
			IntSupplier gauss = () -> Math.round((itemRand.nextFloat()*radius)*(itemRand.nextBoolean() ? -1 : 1));
			for (int i = 0; i < breaksPerSwing; i++) {
				int x = gauss.get()+mop.blockX;
				int y = gauss.get()+mop.blockY;
				int z = gauss.get()+mop.blockZ;
				if (x == mop.blockX && y == mop.blockY && z == mop.blockZ) {
					continue;
				}
				Block block = player.worldObj.getBlock(x, y, z);
				World world = entityLiving.worldObj;
				if (block == Blocks.stone) {
					world.setBlock(x, y, z, Blocks.cobblestone);
					world.playAuxSFX(2001, x, y, z, Block.getIdFromBlock(block) + (world.getBlockMetadata(x, y, z) << 12));
					continue;
				} else if (block == Blocks.cobblestone) {
					world.setBlock(x, y, z, Blocks.gravel);
					world.playAuxSFX(2001, x, y, z, Block.getIdFromBlock(block) + (world.getBlockMetadata(x, y, z) << 12));
					continue;
				}
				if (harvest(player, player.worldObj, x, y, z, itemRand.nextInt(4) == 0, true)) {
					if (!stack.hasTagCompound()) {
						stack.setTagCompound(new NBTTagCompound());
					}
					stack.getTagCompound().setInteger("blocksBroken", stack.getTagCompound().getInteger("blocksBroken")+1);
					if (stack.getTagCompound().getInteger("blocksBroken") >= blocksBeforeCooldown) {
						stack.getTagCompound().setInteger("cooldown", 40);
						stack.getTagCompound().setInteger("blocksBroken", 0);
						harvest(player, player.worldObj, mop.blockX, mop.blockY, mop.blockZ, itemRand.nextInt(4) == 0, true);
					}
				}
				if (stack.stackSize <= 0) {
					Lanthanoid.inst.network.sendToAllAround(new ItemBreakMessage(player.getEntityId(), stack.copy()), new TargetPoint(player.worldObj.provider.dimensionId, player.posX, player.posY, player.posZ, 64));
					stack.setTagCompound(null);
					stack.setItem(LItems.stick);
					stack.setMetadata(LItems.stick.getMetaForName("stickHolmium"));
					stack.stackSize = 1;
					return false;
				}
			}
		}
		return false;
	}
	
	@Override
	public void onUpdate(ItemStack stack, World world, Entity entity, int slot, boolean isHeld) {
		if (entity instanceof EntityPlayer) {
			((EntityPlayer)entity).triggerAchievement(LAchievements.craftWreckingBall);
			if (this == LItems.dysprosium_wrecking_ball) {
				((EntityPlayer)entity).triggerAchievement(LAchievements.craftDysWreckingBall);
			}
		}
		if (stack.hasTagCompound()) {
			if (stack.getTagCompound().getInteger("cooldown") > 0) {
				stack.getTagCompound().setInteger("cooldown", stack.getTagCompound().getInteger("cooldown")-1);
			}
		}
	}
	
	@Override
	public boolean onBlockStartBreak(ItemStack itemstack, int X, int Y, int Z, EntityPlayer player) {
		return itemstack.hasTagCompound() && itemstack.getTagCompound().getInteger("cooldown") > 0;
	}
	
	@Override
	public boolean onBlockDestroyed(ItemStack stack, World world, Block block, int x, int y, int z, EntityLivingBase destroyer) {
		if (!(destroyer instanceof EntityPlayerMP)) {
			return false;
		}
		harvest(((EntityPlayerMP)destroyer), world, x, y, z, itemRand.nextInt(4) == 0, false);
		/*if (breaking) return false;
		if (!(destroyer instanceof EntityPlayerMP)) return false;
		if (destroyer.isSneaking()) {
			breaking = true;
			harvest((EntityPlayerMP)destroyer, world, x, y, z, itemRand.nextInt(4) == 0, false);
			breaking = false;
			return true;
		}
		int metadata = world.getBlockMetadata(x, y, z);
		if (!block.isToolEffective("pickaxe", metadata) && !block.isToolEffective("shovel", metadata)) {
			return false;
		}
		Vec3 look = destroyer.getLookVec();
		destroyer.motionX += look.xCoord * 0.5;
		destroyer.motionY += look.yCoord * 0.5;
		destroyer.motionZ += look.zCoord * 0.5;
		breaking = true;
		for (int oX = -3; oX <= 3; oX++) {
			for (int oY = -3; oY <= 3; oY++) {
				for (int oZ = -3; oZ <= 3; oZ++) {
					int cX = x+oX;
					int cY = y+oY;
					int cZ = z+oZ;
					Block bl = world.getBlock(cX, cY, cZ);
					if (Math.abs(oX) > 1 || Math.abs(oY) > 1 || Math.abs(oZ) > 1) {
						if (itemRand.nextInt(3) != 0) {
							if (bl == Blocks.stone) {
								world.setBlock(cX, cY, cZ, Blocks.cobblestone);
							} else if (bl == Blocks.cobblestone) {
								world.setBlock(cX, cY, cZ, Blocks.gravel);
							} else if (bl == Blocks.gravel) {
								world.setBlock(cX, cY, cZ, Blocks.sand);
							}
							continue;
						}
					}
					if (bl.getMaterial() == bl.getMaterial()) {
						boolean center = (oX == 0 && oY == 0 && oZ == 0);
						harvest((EntityPlayerMP)destroyer, world, cX, cY, cZ, itemRand.nextInt(4) == 0, !center);
					}
				}
			}
		}
		breaking = false;*/
		return true;
	}
	
	private boolean harvest(EntityPlayerMP player, World world, int x, int y, int z, boolean drop, boolean particles) {
		BlockEvent.BreakEvent event = ForgeHooks.onBlockBreakEvent(world, player.theItemInWorldManager.getGameType(), player, x, y, z);
		if (event.isCanceled()) {
			return false;
		} else {
			Block block = world.getBlock(x, y, z);
			int meta = world.getBlockMetadata(x, y, z);
			if (block == Blocks.cobblestone) {
				block = Blocks.gravel;
			} else if (block == Blocks.stone) {
				block = Blocks.cobblestone;
			} else if (block == Blocks.gravel) {
				block = Blocks.sand;
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
				player.getHeldItem().damageItem(1, player);
			}
			return success;
		}
	}
	
}

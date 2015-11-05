package com.unascribed.lanthanoid.item.rifle;

import java.util.List;
import java.util.Set;

import com.google.common.base.Predicate;
import com.google.common.collect.Multimap;
import com.google.common.collect.Sets;
import com.unascribed.lanthanoid.LBlocks;
import com.unascribed.lanthanoid.Lanthanoid;
import com.unascribed.lanthanoid.LanthanoidProperties;
import com.unascribed.lanthanoid.item.ItemBase;
import com.unascribed.lanthanoid.network.BeamParticleMessage;
import com.unascribed.lanthanoid.network.RifleChargingSoundRequest;
import com.unascribed.lanthanoid.network.SetScopeFactorMessage;
import com.unascribed.lanthanoid.util.LVectors;

import cpw.mods.fml.common.network.NetworkRegistry.TargetPoint;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityAgeable;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.monster.EntitySlime;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.EnumAction;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EntityDamageSource;
import net.minecraft.util.IIcon;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.StatCollector;
import net.minecraft.util.Vec3;
import net.minecraft.util.MovingObjectPosition.MovingObjectType;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;

public class ItemRifle extends ItemBase {
	private IIcon base;
	private IIcon[] overlays = new IIcon[12];
	private IIcon[] variantOverlays = new IIcon[Variant.values().length];
	
	public static AxisAlignedBB latestAABB;
	
	public ItemRifle() {
		setMaxStackSize(1);
		setFull3D();
		setHasSubtypes(true);
	}
	
	@Override
	public void addInformation(ItemStack stack, EntityPlayer player, List list, boolean advanced) {
		Variant variant = getVariant(stack);
		float rate = 6f/variant.getAmmoPerDust();
		int percent = (int)(rate*100);
		list.add(StatCollector.translateToLocalFormatted("item.rifle.ammo_usage_tooltip", percent));
		list.add(StatCollector.translateToLocalFormatted("item.rifle.charge_speed_tooltip", (int)(variant.getSpeedMultiplier()*100)));
		list.add(StatCollector.translateToLocalFormatted("item.rifle.cooldown_speed_tooltip", variant == Variant.SUPERCLOCKED ? 50 : 100));
		super.addInformation(stack, player, list, advanced);
	}
	
	@Override
	public Multimap getAttributeModifiers(ItemStack stack) {
		Multimap multimap = super.getAttributeModifiers(stack);
		multimap.put(SharedMonsterAttributes.attackDamage.getAttributeUnlocalizedName(), new AttributeModifier(field_111210_e, "Weapon modifier", 4, 0));
		return multimap;
	}
	
	@Override
	public IIcon getIconFromDamage(int meta) {
		return base;
	}
	
	@Override
	public IIcon getIcon(ItemStack stack, int renderPass, EntityPlayer player, ItemStack usingItem, int useRemaining) {
		if (renderPass == 0) return base;
		if (renderPass == 1) return variantOverlays[getVariant(stack).ordinal()%variantOverlays.length];
		if (useRemaining == 0) {
			return overlays[0];
		}
		int i = Math.round(((getMaxItemUseDuration(stack)-useRemaining)/(20f/getVariant(stack).getSpeedMultiplier()))*overlays.length);
		if (i < 0) {
			return overlays[0];
		} else if (i >= overlays.length) {
			return (i % 2 == 0 ? overlays[overlays.length-1] : overlays[overlays.length-2]);
		}
		return overlays[i];
	}
	
	@Override
	public IIcon getIcon(ItemStack stack, int pass) {
		return pass == 0 ? base : pass == 1 ? variantOverlays[getVariant(stack).ordinal()%variantOverlays.length] : overlays[0];
	}
	
	@Override
	public int getColorFromItemStack(ItemStack stack, int pass) {
		return pass == 2 ? getPrimaryMode(stack).color : pass == 1 ? getVariant(stack).colorize ? Lanthanoid.inst.colors.get("Holmium") : -1 : Lanthanoid.inst.colors.get("Holmium");
	}
	
	@Override
	public String getUnlocalizedName(ItemStack stack) {
		return "item.rifle_"+getVariant(stack).name().toLowerCase();
	}
	
	@Override
	public String getHoverBaseKey(ItemStack stack) {
		return "item.rifle";
	}
	
	public PrimaryMode getPrimaryMode(ItemStack stack) {
		if (!getCompound(stack).hasKey("mode", 99)) return PrimaryMode.DAMAGE;
		PrimaryMode[] val = PrimaryMode.values();
		return val[getCompound(stack).getInteger("mode")%val.length];
	}
	
	public SecondaryMode getSecondaryMode(ItemStack stack) {
		if (!getCompound(stack).hasKey("mode", 99)) return SecondaryMode.NONE;
		SecondaryMode[] val = SecondaryMode.values();
		return val[getCompound(stack).getInteger("mode2")%val.length];
	}
	
	public int getBufferedShots(ItemStack stack) {
		return getCompound(stack).getInteger("buffer");
	}
	
	public void setBufferedShots(ItemStack stack, int shots) {
		getCompound(stack).setInteger("buffer", shots);
	}
	
	public Variant getVariant(ItemStack stack) {
		int meta = stack.getItemDamage();
		Variant[] val = Variant.values();
		return val[meta%val.length];
	}
	
	public void setVariant(ItemStack stack, Variant variant) {
		stack.setItemDamage(variant.ordinal());
	}

	@Override
	public EnumAction getItemUseAction(ItemStack stack) {
		return EnumAction.bow;
	}
	
	@Override
	public int getMaxItemUseDuration(ItemStack stack) {
		return (int)(70/getVariant(stack).getSpeedMultiplier());
	}
	
	@Override
	public boolean showDurabilityBar(ItemStack stack) {
		return getCompound(stack).getInteger("cooldown") > 0;
	}
	
	@Override
	public double getDurabilityForDisplay(ItemStack stack) {
		return getCompound(stack).getInteger("cooldown") / getCompound(stack).getFloat("cooldownStart");
	}
	
	@Override
	public void onUpdate(ItemStack stack, World world, Entity entity, int slot, boolean selected) {
		int cool = getCompound(stack).getInteger("cooldown");
		if (cool > 0) {
			if (entity instanceof EntityPlayer && ((EntityPlayer)entity).capabilities.isCreativeMode) {
				cool = 0;
			} else {
				int speed = (selected ? (entity.isInWater() ? 9 : 5) : (slot < 9 ? 3 : 1));
				if (getVariant(stack) == Variant.SUPERCLOCKED) {
					speed /= 2;
				}
				cool -= speed;
				if (cool % 4 == 0) {
					if (entity.worldObj instanceof WorldServer) {
						((WorldServer)entity.worldObj).func_147487_a("explode", entity.posX, entity.posY, entity.posZ, speed*2, entity.width/2, entity.height/2, entity.width/2, 0);
					}
					entity.playSound("random.fizz", 0.5f, (speed/9f)+0.5f);
				}
			}
		}
		if (cool < 0) {
			cool = 0;
		}
		getCompound(stack).setInteger("cooldown", cool);
	}
	
	public void modifyMode(EntityPlayer player, ItemStack stack, boolean absolute, int i, boolean primary) {
		Mode oldMode = primary ? getPrimaryMode(stack) : getSecondaryMode(stack);
		Mode mode;
		Mode[] vals = primary ? PrimaryMode.values() : SecondaryMode.values();
		if (absolute) {
			if (i == oldMode.ordinal()) return;
			mode = vals[i];
		} else {
			if (i == 0) return;
			int idx = oldMode.ordinal()+i;
			if (idx < 0) {
				idx += vals.length;
			}
			mode = vals[idx%vals.length];
			int tries = 0;
			while (!hasAmmoFor(player, stack, mode)) {
				if (tries++ >= vals.length) {
					idx = oldMode.ordinal()+i;
					break;
				}
				idx += i;
				if (idx < 0) {
					idx += vals.length;
				}
				mode = vals[idx%vals.length];
			}
		}
		if (mode != oldMode) {
			setBufferedShots(stack, 0);
		}
		player.worldObj.playSoundAtEntity(player, "lanthanoid:rifle_mode", 1.0f, (mode.ordinal()*0.05f)+1.0f);
		if (primary) {
			getCompound(stack).setInteger("mode", mode.ordinal());
		} else {
			getCompound(stack).setInteger("mode2", mode.ordinal());
		}
	}
	
	public boolean hasAmmoFor(EntityPlayer player, ItemStack stack, Mode mode) {
		if (player.capabilities.isCreativeMode) return true;
		if (mode == getPrimaryMode(stack) && getBufferedShots(stack) > 0) return true;
		if (mode.type == null) return true;
		for (ItemStack is : player.inventory.mainInventory) {
			if (is == null) continue;
			if (mode.stackMatches(is)) {
				return true;
			}
		}
		return false;
	}
	
	@Override
	public ItemStack onEaten(ItemStack stack, World world, EntityPlayer player) {
		if (!world.isRemote) {
			setBufferedShots(stack, getBufferedShots(stack)-2);
			if (getBufferedShots(stack) <= 0) {
				PrimaryMode mode = getPrimaryMode(stack);
				if (consumeInventoryItem(player.inventory, mode::stackMatches)) {
					setBufferedShots(stack, getBufferedShots(stack)+getVariant(stack).getAmmoPerDust());
				}
			}
		}
		player.playSound("lanthanoid:rifle_overheat", 1.0f, 1.0f);
		getCompound(stack).setInteger("cooldownStart", 480);
		getCompound(stack).setInteger("cooldown", 480);
		player.attackEntityFrom(DamageSource.inFire, 4);
		player.setFire(8);
		return stack;
	}
	
	@Override
	public boolean onEntitySwing(EntityLivingBase entityLiving, ItemStack stack) {
		if (entityLiving instanceof EntityPlayer) {
			EntityPlayer player = ((EntityPlayer)entityLiving);
			if (player.isSneaking()) {
				LanthanoidProperties props = (LanthanoidProperties)entityLiving.getExtendedProperties("lanthanoid");
				if (getVariant(stack) == Variant.ZOOM) {
					props.scopeFactor = ((props.scopeFactor)%10)+1;
					player.playSound("lanthanoid:rifle_scope", 1.0f, 1.0f+(props.scopeFactor/10f));
				} else if (getVariant(stack) == Variant.NONE) {
					if (props.scopeFactor == 0) {
						props.scopeFactor = 1;
					} else {
						props.scopeFactor = 0;
					}
				} else {
					return false;
				}
				if (!player.worldObj.isRemote && player instanceof EntityPlayerMP) {
					Lanthanoid.inst.network.sendTo(new SetScopeFactorMessage(props.scopeFactor), ((EntityPlayerMP)player));
				}
				return true;
			}
		}
		return false;
	}
	
	@Override
	public void onPlayerStoppedUsing(ItemStack stack, World world, EntityPlayer player, int useRemaining) {
		if (!world.isRemote) {
			Lanthanoid.inst.network.sendToAllAround(new RifleChargingSoundRequest(player.getEntityId(), getVariant(stack).getSpeedMultiplier(), false),
					new TargetPoint(player.worldObj.provider.dimensionId, player.posX, player.posY, player.posZ, 64));
		}
		if (useRemaining <= (50/getVariant(stack).getSpeedMultiplier())) {
			player.playSound("lanthanoid:rifle_fire", 1.0f, (itemRand.nextFloat()*0.2f)+1.0f);
			getCompound(stack).setInteger("cooldownStart", 10);
			getCompound(stack).setInteger("cooldown", 10);
			Variant variant = getVariant(stack);
			if (!world.isRemote) {
				PrimaryMode primaryMode = getPrimaryMode(stack);
				SecondaryMode secondaryMode = getSecondaryMode(stack);
				setBufferedShots(stack, getBufferedShots(stack)-1);
				if (getBufferedShots(stack) <= 0) {
					if (consumeInventoryItem(player.inventory, primaryMode::stackMatches)) {
						setBufferedShots(stack, getBufferedShots(stack)+variant.getAmmoPerDust());
					}
				}
				Vec3 start = Vec3.createVectorHelper(player.posX, player.boundingBox.maxY-0.2f, player.posZ);
				Vec3 look = player.getLookVec();
				float range;
				if (primaryMode == PrimaryMode.LIGHT) {
					range = 25;
				} else if (primaryMode == PrimaryMode.MINE) {
					range = 30;
				} else {
					range = 150;
				}
				Vec3 direction = Vec3.createVectorHelper(look.xCoord*range, look.yCoord*range, look.zCoord*range);
				int scopeFactor = ((LanthanoidProperties)player.getExtendedProperties("lanthanoid")).scopeFactor;
				if (scopeFactor == 1) {
					Vec3 right = player.getLookVec();
					right.rotateAroundY(-90f);
					float rightAdj = 0.25f;
					start = start.addVector(right.xCoord*rightAdj, right.yCoord*rightAdj, right.zCoord*rightAdj);
				} else if (scopeFactor > 1) {
					start.yCoord -= 0.25;
				}
				double spread;
				switch (scopeFactor) {
					case 1:
						spread = 2;
						break;
					case 0:
						spread = 1;
						break;
					default:
						spread = 0;
						break;
				}
				switch (variant) {
					case SUPERCLOCKED:
						spread *= 2;
						break;
					case EFFICIENCY:
						spread *= 0.75;
						break;
					case SUPEREFFICIENCY:
						spread *= 0.35;
						break;
					default:
						break;
				}
				spread *= range;
				if (spread > 0) {
					direction.xCoord += itemRand.nextGaussian() * 0.0075 * spread;
					direction.yCoord += itemRand.nextGaussian() * 0.0075 * spread;
					direction.zCoord += itemRand.nextGaussian() * 0.0075 * spread;
				}
				boolean fire = false;//(variant == Variant.FIRE && player.inventory.consumeInventoryItem(Items.blaze_powder));
				shootLaser(world, primaryMode, secondaryMode, fire, start, direction, player);
			}
			if (variant == Variant.SUPERCLOCKED) {
				getCompound(stack).setInteger("cooldownStart", 50);
				getCompound(stack).setInteger("cooldown", 50);
			}
		}
	}
	
	private void shootLaser(World world, PrimaryMode primaryMode, SecondaryMode secondaryMode, boolean fire, Vec3 start, Vec3 direction, EntityPlayer shooter) {
		if (primaryMode == PrimaryMode.KNOCKBACK) {
			
		} else if (primaryMode == PrimaryMode.WORMHOLE) {
			
		} else if (primaryMode == PrimaryMode.MINE) {
			Vec3 end = start.addVector(direction.xCoord, direction.yCoord, direction.zCoord);
			spawnParticles(world, primaryMode, fire, start.xCoord, start.yCoord, start.zCoord, end.xCoord, end.yCoord, end.zCoord);
		} else {
			Vec3 end = start.addVector(direction.xCoord, direction.yCoord, direction.zCoord);
			MovingObjectPosition mop = rayTrace(world, shooter, clone(start), clone(direction));
			if (mop != null) {
				end = mop.hitVec;
			}
			spawnParticles(world, primaryMode, fire, start.xCoord, start.yCoord, start.zCoord, end.xCoord, end.yCoord, end.zCoord);
			if (mop != null) {
				if (mop.entityHit instanceof EntityLivingBase) {
					if (primaryMode.doesDamage()) {
						((EntityLivingBase)mop.entityHit).attackEntityFrom(new EntityDamageSource("laser", shooter), 7);
					}
					if (primaryMode.doesHeal()) {
						((EntityLivingBase)mop.entityHit).heal(10);
					}
				}
				if (secondaryMode == SecondaryMode.CHAIN && (primaryMode.doesDamage() || primaryMode.doesHeal())) {
					if (mop.entityHit instanceof EntityLivingBase) {
						EntityLivingBase hit = (EntityLivingBase) mop.entityHit;
						world.playSoundEffect(end.xCoord, end.yCoord, end.zCoord, "lanthanoid:rifle_fire", 0.5f, 1.5f);
						Set<Entity> shot = Sets.newHashSet(shooter, hit);
						Vec3 vec3 = end;
						for (int i = 0; i < 4; i++) {
							double minDist = Double.MAX_VALUE;
							Entity nxt = null;
							for (Entity e : (List<Entity>)world.getEntitiesWithinAABB(EntityLivingBase.class, hit.boundingBox.expand(5, 5, 5))) {
								if (shot.contains(e)) continue;
								double dist = e.getDistanceSq(end.xCoord, end.yCoord, end.zCoord);
								if (dist <= minDist) {
									minDist = dist;
									nxt = e;
								}
							}
							shot.add(nxt);
							if (nxt == null) break;
							if (!(nxt instanceof EntityLivingBase)) continue;
							Vec3 nxtVec = Vec3.createVectorHelper(nxt.posX, nxt.posY+(nxt.height/2), nxt.posZ);
							MovingObjectPosition check = world.rayTraceBlocks(Vec3.createVectorHelper(hit.posX, hit.posY+hit.height/2, hit.posZ), nxtVec);
							Vec3 particleVec = check == null ? nxtVec : check.hitVec;
							spawnParticles(world, primaryMode, fire, vec3.xCoord, vec3.yCoord, vec3.zCoord, particleVec.xCoord, particleVec.yCoord, particleVec.zCoord);
							if (check == null) {
								hit = (EntityLivingBase)nxt;
								vec3 = Vec3.createVectorHelper(hit.posX, hit.posY+hit.height/2, hit.posZ);
								if (primaryMode.doesDamage()) {
									hit.attackEntityFrom(new EntityDamageSource("laser", shooter), 6-i);
								}
								if (primaryMode.doesHeal()) {
									hit.heal(9-i);
								}
							}
						}
					}
				}
				if (secondaryMode == SecondaryMode.BOUNCE && (primaryMode.doesDamage() || primaryMode.doesHeal()) && mop.entityHit == null) {
					AxisAlignedBB aabb = AxisAlignedBB.getBoundingBox(end.xCoord-5, end.yCoord-5, end.zCoord-5,
							end.xCoord+5, end.yCoord+5, end.zCoord+5);
					latestAABB = aabb;
					List<EntityLivingBase> li = (List<EntityLivingBase>)world.getEntitiesWithinAABB(EntityLivingBase.class, aabb);
					
					EntityLivingBase shoot = null;
					
					double minDistance = Double.MAX_VALUE;
					EntityLivingBase closest = null;
					for (EntityLivingBase elb : li) {
						if (elb == shooter) continue;
						double distance = elb.getDistanceSq(end.xCoord, end.yCoord, end.zCoord);
						if (distance < minDistance) {
							minDistance = distance;
							closest = elb;
						}
						Vec3 nxtVec = Vec3.createVectorHelper(elb.posX, elb.posY+(elb.height/2), elb.posZ);
						MovingObjectPosition check = world.rayTraceBlocks(Vec3.createVectorHelper(end.xCoord, end.yCoord, end.zCoord), nxtVec);
						if (check == null) {
							shoot = elb;
							break;
						}
					}
					if (shoot == null) {
						shoot = closest;
					}
					if (shoot != null) {
						spawnParticles(world, primaryMode, fire, end.xCoord, end.yCoord, end.zCoord, shoot.posX, shoot.posY, shoot.posZ);
						if (primaryMode.doesDamage()) {
							shoot.attackEntityFrom(new EntityDamageSource("laser", shooter), 5);
						}
						if (primaryMode.doesHeal()) {
							shoot.heal(7);
						}
					}
				}
			}
			if (primaryMode == PrimaryMode.GROW) {
				if (mop != null) {
					if (mop.entityHit instanceof EntitySlime) {
						EntitySlime slime = ((EntitySlime)mop.entityHit);
						if (slime.getSlimeSize() < 10) {
							slime.setSlimeSize(slime.getSlimeSize()+1);
						}
					} else if (mop.entityHit instanceof EntityAgeable) {
						EntityAgeable ageable = ((EntityAgeable)mop.entityHit);
						ageable.setGrowingAge(0);
					}
				}
			} else if (primaryMode == PrimaryMode.SHRINK) {
				if (mop != null) {
					if (mop.entityHit instanceof EntitySlime) {
						EntitySlime slime = ((EntitySlime)mop.entityHit);
						if (slime.getSlimeSize() > 1) {
							slime.setSlimeSize(slime.getSlimeSize()-1);
						}
					} else if (mop.entityHit instanceof EntityAgeable) {
						EntityAgeable ageable = ((EntityAgeable)mop.entityHit);
						ageable.setGrowingAge(-10000);
					}
				}
			} else if (primaryMode == PrimaryMode.LIGHT) {
				double steps = start.distanceTo(end);
				for (int i = 0; i < steps; i++) {
					double[] pos = LVectors.interpolate(start.xCoord, start.yCoord, start.zCoord, end.xCoord, end.yCoord, end.zCoord, i/steps);
					int x = (int)pos[0];
					int y = (int)pos[1];
					int z = (int)pos[2];
					if (world.isAirBlock(x, y, z)) {
						world.setBlock(x, y, z, LBlocks.technical, 0, 2);
					}
				}
			} else if (primaryMode == PrimaryMode.REPLICATE) {
				if (mop != null) {
					if (mop.typeOfHit == MovingObjectType.BLOCK) {
						
					} else if (mop.typeOfHit == MovingObjectType.ENTITY) {
						if (mop.entityHit instanceof EntityAnimal) {
							((EntityAnimal)mop.entityHit).func_146082_f(shooter);
						}
					}
				}
			} else if (primaryMode == PrimaryMode.EXPLODE) {
				shooter.worldObj.newExplosion(null, end.xCoord, end.yCoord, end.zCoord, 3f, fire, true);
			}
		}
	}
	
	private Vec3 clone(Vec3 vec) {
		return Vec3.createVectorHelper(vec.xCoord, vec.yCoord, vec.zCoord);
	}

	private void spawnParticles(World world, PrimaryMode mode, boolean fire, double startX, double startY, double startZ, double endX, double endY, double endZ) {
		Lanthanoid.inst.network.sendToAllAround(new BeamParticleMessage(fire, mode.doesPoof(), startX, startY, startZ, endX, endY, endZ, mode.color), new TargetPoint(
				world.provider.dimensionId,
				startX,
				startY,
				startZ,
				150
				));
	}

	private MovingObjectPosition rayTrace(World world, Entity shooter, Vec3 start, Vec3 direction) {
		Vec3 vec31 = Vec3.createVectorHelper(start.xCoord, start.yCoord, start.zCoord);
		Vec3 vec3 = Vec3.createVectorHelper(start.xCoord+direction.xCoord, start.yCoord+direction.yCoord, start.zCoord+direction.zCoord);
		MovingObjectPosition movingobjectposition = world.func_147447_a(vec31, vec3, false, false, false);
		vec31 = Vec3.createVectorHelper(start.xCoord, start.yCoord, start.zCoord);
		vec3 = Vec3.createVectorHelper(start.xCoord+direction.xCoord, start.yCoord+direction.yCoord, start.zCoord+direction.zCoord);

		if (movingobjectposition != null) {
			vec3 = Vec3.createVectorHelper(movingobjectposition.hitVec.xCoord, movingobjectposition.hitVec.yCoord, movingobjectposition.hitVec.zCoord);
		}

		Entity entity = null;
		AxisAlignedBB aabb = AxisAlignedBB.getBoundingBox(
					start.xCoord-0.125, start.yCoord-0.125, start.zCoord-0.125,
					start.xCoord+0.125, start.yCoord+0.125, start.zCoord+0.125)
				.addCoord(direction.xCoord, direction.yCoord, direction.zCoord)
				.expand(1, 1, 1);
		
		latestAABB = aabb;
		List list = world.getEntitiesWithinAABBExcludingEntity(null, aabb);
		double d0 = 0.0D;
		int i;
		float f1;

		Vec3 hit = null;
		
		for (i = 0; i < list.size(); ++i) {
			Entity entity1 = (Entity) list.get(i);
			if (entity1.canBeCollidedWith() && entity1 != shooter) {
				f1 = 0.3F;
				AxisAlignedBB axisalignedbb1 = entity1.boundingBox.expand((double) f1, (double) f1, (double) f1);
				MovingObjectPosition movingobjectposition1 = axisalignedbb1.calculateIntercept(vec31, vec3);

				if (movingobjectposition1 != null) {
					double d1 = vec31.distanceTo(movingobjectposition1.hitVec);

					if (d1 < d0 || d0 == 0.0D) {
						entity = entity1;
						hit = movingobjectposition1.hitVec;
						d0 = d1;
					}
				}
			}
		}

		if (entity != null) {
			movingobjectposition = new MovingObjectPosition(entity);
			movingobjectposition.hitVec = hit;
		}
		return movingobjectposition;
	}

	
	@Override
	public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player) {
		if (getCompound(stack).getInteger("cooldown") > 0) return stack;
		if (hasAmmoFor(player, stack, getPrimaryMode(stack))) {
			player.setItemInUse(stack, getMaxItemUseDuration(stack));
			if (!world.isRemote) {
				Lanthanoid.inst.network.sendToAllAround(new RifleChargingSoundRequest(player.getEntityId(), getVariant(stack).getSpeedMultiplier(), true),
						new TargetPoint(player.worldObj.provider.dimensionId, player.posX, player.posY, player.posZ, 64));
			}
		}
		return stack;
	}
	
	private boolean consumeInventoryItem(InventoryPlayer inv, Predicate<ItemStack> predicate) {
		int i = find(inv, predicate);

		if (i < 0) {
			return false;
		} else {
			if (--inv.mainInventory[i].stackSize <= 0) {
				inv.mainInventory[i] = null;
			}
			return true;
		}
	}
	
	private int find(InventoryPlayer inv, Predicate<ItemStack> predicate) {
		for (int i = 0; i < inv.mainInventory.length; i++) {
			if (inv.mainInventory[i] != null && predicate.apply(inv.mainInventory[i])) {
				return i;
			}
		}

		return -1;
	}
	
	@Override
	public int getRenderPasses(int metadata) {
		return 3;
	}
	@Override
	public boolean requiresMultipleRenderPasses() {
		return true;
	}
	
	@Override
	public void getSubItems(Item item, CreativeTabs tab, List list) {
		for (Variant a : Variant.values()) {
			list.add(new ItemStack(item, 1, a.ordinal()));
		}
	}
	
	@Override
	public void registerIcons(IIconRegister register) {
		base = register.registerIcon("lanthanoid:rifle");
		for (int i = 0; i < overlays.length; i++) {
			overlays[i] = register.registerIcon("lanthanoid:rifle_overlay_"+i);
		}
		for (Variant a : Variant.values()) {
			variantOverlays[a.ordinal()] = register.registerIcon("lanthanoid:rifle_"+a.icon);
		}
	}
}
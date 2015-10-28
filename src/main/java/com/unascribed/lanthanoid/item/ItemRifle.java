package com.unascribed.lanthanoid.item;

import java.util.List;
import java.util.Set;

import com.google.common.collect.Sets;
import com.unascribed.lanthanoid.LItems;
import com.unascribed.lanthanoid.Lanthanoid;
import com.unascribed.lanthanoid.network.BeamParticleMessage;
import com.unascribed.lanthanoid.network.RifleChargingSoundRequest;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.network.NetworkRegistry.TargetPoint;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumAction;
import net.minecraft.item.ItemStack;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EntityDamageSource;
import net.minecraft.util.IIcon;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;

public class ItemRifle extends ItemBase {
	private IIcon base;
	private IIcon[] overlays = new IIcon[12];
	
	public static AxisAlignedBB latestAABB;
	
	public enum Mode {
		TRACTOR("dustRosasite", 0x00D9FF),
		DAMAGE("dustYtterbium", 0xFFEC00),
		CHAIN_DAMAGE("dustNeodymium", 0x8D8DFF),
		BOUNCE_DAMAGE("dustPraseodymium", 0x96FF8F),
		HEALING("dustErbium", 0x2C61FF),
		CHAIN_HEALING("dustGadolinium", 0x2CFFAD),
		MINE("dustHolmium", 0xFFF4D6),
		GROW("dustCerium", 0xFF004C),
		SHRINK("dustDysprosium", 0xE400FF),
		KNOCKBACK("dustYttrium", 0xA9F8FF),
		REPLICATE("dustActinolite", 0x83FFCF),
		WORMHOLE("dustDiaspore", 0x8762FF),
		LIGHT("dustThulite", 0xFF7768),
		;
		public final String type;
		public final String translationKey;
		public final int color;
		public final ItemStack stack;
		Mode(String type, int color) {
			this.translationKey = name().toLowerCase();
			this.type = type;
			this.color = color;
			this.stack = LItems.dust.getStackForName(type);
		}
		public boolean doesDamage() {
			return this == DAMAGE || this == CHAIN_DAMAGE || this == BOUNCE_DAMAGE;
		}
		public boolean doesHeal() {
			return this == HEALING || this == CHAIN_HEALING;
		}
		public boolean doesChain() {
			return this == CHAIN_DAMAGE || this == CHAIN_HEALING;
		}
		public boolean doesDeflect() {
			return this == BOUNCE_DAMAGE;
		}
	}
	
	public ItemRifle() {
		setUnlocalizedName("rifle");
		setMaxStackSize(1);
		setFull3D();
	}
	
	@Override
	public IIcon getIconFromDamage(int meta) {
		return base;
	}
	
	@Override
	public IIcon getIcon(ItemStack stack, int renderPass, EntityPlayer player, ItemStack usingItem, int useRemaining) {
		if (renderPass == 0) return base;
		if (useRemaining == 0) {
			return overlays[0];
		}
		int i = Math.round(((getMaxItemUseDuration(stack)-useRemaining)/20f)*overlays.length);
		if (i < 0) {
			return overlays[0];
		} else if (i >= overlays.length) {
			return (i % 2 == 0 ? overlays[overlays.length-1] : overlays[overlays.length-2]);
		}
		return overlays[i];
	}
	
	@Override
	public IIcon getIcon(ItemStack stack, int pass) {
		return pass == 0 ? base : overlays[0];
	}
	
	@Override
	public int getColorFromItemStack(ItemStack stack, int pass) {
		return pass == 1 ? getMode(stack).color : -1;
	}
	
	public Mode getMode(ItemStack stack) {
		if (!getCompound(stack).hasKey("mode", 99)) return Mode.DAMAGE;
		Mode[] val = Mode.values();
		return val[getCompound(stack).getInteger("mode")%val.length];
	}

	@Override
	public EnumAction getItemUseAction(ItemStack stack) {
		return EnumAction.bow;
	}
	
	@Override
	public int getMaxItemUseDuration(ItemStack stack) {
		return 70;
	}
	
	@Override
	public boolean showDurabilityBar(ItemStack stack) {
		return getCompound(stack).getInteger("cooldown") > 0;
	}
	
	@Override
	public double getDurabilityForDisplay(ItemStack stack) {
		return getCompound(stack).getInteger("cooldown") / 480f;
	}
	
	@Override
	public void onUpdate(ItemStack stack, World world, Entity entity, int slot, boolean selected) {
		int cool = getCompound(stack).getInteger("cooldown");
		if (cool > 0) {
			if (entity instanceof EntityPlayer && ((EntityPlayer)entity).capabilities.isCreativeMode) {
				cool = 0;
			} else {
				int speed = (selected ? (entity.isInWater() ? 9 : 5) : (slot < 9 ? 3 : 1));
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
	
	public void modifyMode(EntityPlayer player, ItemStack stack, boolean absolute, int i) {
		Mode mode;
		Mode[] vals = Mode.values();
		if (absolute) {
			if (i == getMode(stack).ordinal()) return;
			mode = vals[i];
		} else {
			if (i == 0) return;
			int idx = getMode(stack).ordinal()+i;
			if (idx < 0) {
				idx += vals.length;
			}
			mode = vals[idx%vals.length];
			int tries = 0;
			while (!hasAmmoFor(player, mode)) {
				if (tries++ >= vals.length) {
					idx = getMode(stack).ordinal()+i;
					break;
				}
				idx += i;
				if (idx < 0) {
					idx += vals.length;
				}
				mode = vals[idx%vals.length];
			}
		}
		player.worldObj.playSoundAtEntity(player, "lanthanoid:rifle_mode", 1.0f, (mode.ordinal()*0.05f)+1.0f);
		getCompound(stack).setInteger("mode", mode.ordinal());
	}
	
	public boolean hasAmmoFor(EntityPlayer player, Mode mode) {
		if (player.capabilities.isCreativeMode) return true;
		for (ItemStack is : player.inventory.mainInventory) {
			if (is == null) continue;
			if (mode.stack.isItemEqual(is)) {
				return true;
			}
		}
		return false;
	}
	
	@Override
	public ItemStack onEaten(ItemStack stack, World world, EntityPlayer player) {
		player.playSound("lanthanoid:rifle_overheat", 1.0f, 1.0f);
		getCompound(stack).setInteger("cooldown", 480);
		player.attackEntityFrom(DamageSource.inFire, 4);
		player.setFire(8);
		return stack;
	}
	
	@Override
	public void onPlayerStoppedUsing(ItemStack stack, World world, EntityPlayer player, int useRemaining) {
		if (world.isRemote) {
			Lanthanoid.inst.network.sendToAllAround(new RifleChargingSoundRequest(player.getEntityId(), false),
					new TargetPoint(player.worldObj.provider.dimensionId, player.posX, player.posY, player.posZ, 64));
		}
		if (useRemaining <= 50) {
			player.playSound("lanthanoid:rifle_fire", 1.0f, (itemRand.nextFloat()*0.2f)+1.0f);
			if (!world.isRemote) {
				Mode mode = getMode(stack);
				Vec3 start = Vec3.createVectorHelper(player.posX, player.boundingBox.maxY-0.2f, player.posZ);
				Vec3 look = player.getLookVec();
				float range = 150;
				Vec3 direction = Vec3.createVectorHelper(look.xCoord*range, look.yCoord*range, look.zCoord*range);
				Vec3 right = player.getLookVec();
				right.rotateAroundY(-90f);
				float rightAdj = 0.25f;
				start = start.addVector(right.xCoord*rightAdj, right.yCoord*rightAdj, right.zCoord*rightAdj);
				shootLaser(world, mode, start, direction, player);
			}
		}
	}
	
	private void shootLaser(World world, Mode mode, Vec3 start, Vec3 direction, EntityPlayer shooter) {
		if (mode == Mode.KNOCKBACK) {
			
		} else if (mode == Mode.WORMHOLE) {
			
		} else {
			Vec3 end = start.addVector(direction.xCoord, direction.yCoord, direction.zCoord);
			MovingObjectPosition mop = rayTrace(world, shooter, start, direction);
			if (mop != null) {
				end = mop.hitVec;
			}
			spawnParticles(world, mode, start.xCoord, start.yCoord, start.zCoord, end.xCoord, end.yCoord, end.zCoord);
			if (mop != null) {
				if (mop.entityHit instanceof EntityLivingBase) {
					if (mode.doesDamage()) {
						((EntityLivingBase)mop.entityHit).attackEntityFrom(new EntityDamageSource("laser", shooter), 7);
					}
					if (mode.doesHeal()) {
						((EntityLivingBase)mop.entityHit).heal(10);
					}
				}
				if (mode.doesChain()) {
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
							spawnParticles(world, mode, vec3.xCoord, vec3.yCoord, vec3.zCoord, particleVec.xCoord, particleVec.yCoord, particleVec.zCoord);
							if (check == null) {
								hit = (EntityLivingBase)nxt;
								vec3 = Vec3.createVectorHelper(hit.posX, hit.posY+hit.height/2, hit.posZ);
								if (mode.doesDamage()) {
									hit.attackEntityFrom(new EntityDamageSource("laser", shooter), 6-i);
								}
								if (mode.doesHeal()) {
									hit.heal(9-i);
								}
							}
						}
					}
				}
				if (mode.doesDeflect() && mop.entityHit == null) {
					AxisAlignedBB aabb = AxisAlignedBB.getBoundingBox(end.xCoord-5, end.yCoord-5, end.zCoord-5,
							end.xCoord+5, end.yCoord+5, end.zCoord+5);
					latestAABB = aabb;
					EntityLivingBase nxt = (EntityLivingBase)world.findNearestEntityWithinAABB(EntityLivingBase.class, aabb, shooter);
					if (nxt != null) {
						Vec3 nxtVec = Vec3.createVectorHelper(nxt.posX, nxt.posY+(nxt.height/2), nxt.posZ);
						//MovingObjectPosition check = world.rayTraceBlocks(end, nxtVec);
						Vec3 vec = /*check == null ? */nxtVec/* : check.hitVec*/;
						spawnParticles(world, mode, end.xCoord, end.yCoord, end.zCoord, vec.xCoord, vec.yCoord, vec.zCoord);
						//if (check == null) {
							if (mode.doesDamage()) {
								nxt.attackEntityFrom(new EntityDamageSource("laser", shooter), 5);
							}
							if (mode.doesHeal()) {
								nxt.heal(7);
							}
						//}
					}
				}
				switch (mode) {
					case GROW:
						// TODO
						break;
					case SHRINK:
						// TODO
						break;
					case LIGHT:
						// TODO
						break;
					case MINE:
						// TODO
						break;
					case REPLICATE:
						// TODO
						break;
					default:
						break;
				}
			}
		}
	}
	
	private void spawnParticles(World world, Mode mode, double startX, double startY, double startZ, double endX, double endY, double endZ) {
		Lanthanoid.inst.network.sendToAllAround(new BeamParticleMessage(startX, startY, startZ, endX, endY, endZ, mode.color), new TargetPoint(
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
		player.setItemInUse(stack, getMaxItemUseDuration(stack));
		if (world.isRemote) {
			Lanthanoid.inst.network.sendToAllAround(new RifleChargingSoundRequest(player.getEntityId(), true),
					new TargetPoint(player.worldObj.provider.dimensionId, player.posX, player.posY, player.posZ, 64));
		}
		return stack;
	}
	
	@Override
	public int getRenderPasses(int metadata) {
		return 2;
	}
	@Override
	public boolean requiresMultipleRenderPasses() {
		return true;
	}
	
	@Override
	public void registerIcons(IIconRegister register) {
		base = register.registerIcon("lanthanoid_compositor:rifle");
		for (int i = 0; i < overlays.length; i++) {
			overlays[i] = register.registerIcon("lanthanoid:rifle_overlay_"+i);
		}
	}
}

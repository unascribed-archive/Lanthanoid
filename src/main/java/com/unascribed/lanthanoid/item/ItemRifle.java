package com.unascribed.lanthanoid.item;

import java.util.List;

import com.unascribed.lanthanoid.LItems;
import com.unascribed.lanthanoid.Lanthanoid;
import com.unascribed.lanthanoid.effect.EntityRifleFX;
import com.unascribed.lanthanoid.network.RifleChargingSoundRequest;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.network.NetworkRegistry.TargetPoint;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.item.EnumAction;
import net.minecraft.item.ItemStack;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EntityDamageSource;
import net.minecraft.util.IIcon;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;

public class ItemRifle extends ItemBase {
	private IIcon base;
	private IIcon[] overlays = new IIcon[12];
	
	public static AxisAlignedBB latestAABB;
	
	public enum Mode {
		DAMAGE("dustYtterbium", 0xFFEC00),
		MINE("dustNeodymium", 0x8D8DFF),
		GROW("dustCerium", 0xFF004C),
		SHRINK("dustDysprosium", 0xE400FF),
		CHAIN_DAMAGE("dustHolmium", 0xFFF4D6),
		HEALING("dustErbium", 0x2C61FF),
		CHAIN_HEALING("dustGadolinium", 0x2CFFAD),
		BOUNCE_DAMAGE("dustPraseodymium", 0x96FF8F),
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
			Mode mode = getMode(stack);
			Vec3 start = Vec3.createVectorHelper(player.posX, player.boundingBox.maxY-0.12f, player.posZ);
			Vec3 look = player.getLookVec();
			float range = 50;
			Vec3 end = start.addVector(look.xCoord*range, look.yCoord*range, look.zCoord*range);
			Vec3 right = player.getLookVec();
			right.rotateAroundY(-90f);
			float rightAdj = 0.25f;
			start = start.addVector(right.xCoord*rightAdj, right.yCoord*rightAdj, right.zCoord*rightAdj);
			System.out.println(start);
			shootLaser(world, mode, start, end, player, 0);
		}
	}
	
	private void shootLaser(World world, Mode mode, Vec3 start, Vec3 end, EntityLivingBase shooter, int depth) {
		MovingObjectPosition mop = rayTrace(world, shooter, start, end);
		if (FMLCommonHandler.instance().getSide().isClient() && FMLCommonHandler.instance().getEffectiveSide().isClient()) {
			Vec3 vec = (mop == null ? end : mop.hitVec);
			spawnParticles(world, mode, start.xCoord, start.yCoord, start.zCoord, vec.xCoord, vec.yCoord, vec.zCoord);
		}
		System.out.println(mop);
		if (mop != null) {
			if (mop.entityHit instanceof EntityLivingBase && !world.isRemote) {
				if (mode.doesDamage()) {
					((EntityLivingBase)mop.entityHit).attackEntityFrom(new EntityDamageSource("laser", shooter), 7);
				}
				if (mode.doesHeal()) {
					((EntityLivingBase)mop.entityHit).heal(10);
				}
			}
			switch (mode) {
				case BOUNCE_DAMAGE:
					// TODO
					break;
				case CHAIN_HEALING:
				case CHAIN_DAMAGE:
					if (mop.entityHit instanceof EntityLivingBase && depth < 4) {
						EntityLivingBase nearest = findNearestEntityWithinAABB(world, EntityLivingBase.class,
								AxisAlignedBB.getBoundingBox(end.xCoord-5, end.yCoord-5, end.zCoord-5,
										end.xCoord+5, end.yCoord+5, end.zCoord+5), end);
						if (nearest != null) {
							shootLaser(world, mode, end, Vec3.createVectorHelper(nearest.posX, nearest.posY, nearest.posZ), shooter, depth + 1);
						}
					}
					break;
				case GROW:
					// TODO
					break;
				case KNOCKBACK:
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
				case SHRINK:
					// TODO
					break;
				case WORMHOLE:
					// TODO
					break;
				default:
					break;
			}
		}
	}
	
	private MovingObjectPosition rayTrace(World world, EntityLivingBase shooter, Vec3 start, Vec3 end) {
		Vec3 vec31 = Vec3.createVectorHelper(start.xCoord, start.yCoord, start.zCoord);
		Vec3 vec3 = Vec3.createVectorHelper(end.xCoord, end.yCoord, end.zCoord);
		MovingObjectPosition movingobjectposition = world.func_147447_a(vec31, vec3, false, false, false);
		vec31 = Vec3.createVectorHelper(start.xCoord, start.yCoord, start.zCoord);
		vec3 = Vec3.createVectorHelper(end.xCoord, end.yCoord, end.zCoord);

		if (movingobjectposition != null) {
			vec3 = Vec3.createVectorHelper(movingobjectposition.hitVec.xCoord, movingobjectposition.hitVec.yCoord, movingobjectposition.hitVec.zCoord);
		}

		Entity entity = null;
		AxisAlignedBB aabb = AxisAlignedBB.getBoundingBox(start.xCoord, start.yCoord, start.zCoord, end.xCoord, end.yCoord, end.zCoord).expand(4, 4, 4);
		latestAABB = aabb;
		List list = world.getEntitiesWithinAABBExcludingEntity(null, aabb);
		double d0 = 0.0D;
		int i;
		float f1;

		Vec3 hit = null;
		
		for (i = 0; i < list.size(); ++i) {
			Entity entity1 = (Entity) list.get(i);
			System.out.println(entity1);
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

	public <T extends Entity> T findNearestEntityWithinAABB(World world, Class<T> clazz, AxisAlignedBB aabb, Vec3 position) {
		List<Entity> list = world.getEntitiesWithinAABB(clazz, aabb);
		Entity entity1 = null;
		double d0 = Double.MAX_VALUE;

		for (int i = 0; i < list.size(); ++i) {
			Entity entity2 = list.get(i);

			double d1 = position.squareDistanceTo(entity2.posX, entity2.posY, entity2.posZ);

			if (d1 <= d0) {
				entity1 = entity2;
				d0 = d1;
			}
		}

		return (T)entity1;
	}

	@SideOnly(Side.CLIENT)
	private void spawnParticles(World world, Mode mode, double startX, double startY, double startZ, double endX, double endY, double endZ) {
		double stepSize = 0.1;
		if (Minecraft.getMinecraft().gameSettings.particleSetting == 1) {
			// Decreased
			stepSize = 1;
		} else if (Minecraft.getMinecraft().gameSettings.particleSetting == 2) {
			// Minimal
			stepSize = 2.5;
		}
		double steps = (int)(distance(startX, startY, startZ, endX, endY, endZ)/stepSize);
		for (int i = 0; i < steps; i++) {
			double[] end = interpolate(startX, startY, startZ, endX, endY, endZ, i/steps);
			EntityRifleFX fx = new EntityRifleFX(world, end[0], end[1], end[2], 1.0f, 0, 0, 0);
			fx.motionX = fx.motionY = fx.motionZ = 0;
			float r = ((mode.color >> 16)&0xFF)/255f;
			float g = ((mode.color >> 8)&0xFF)/255f;
			float b = (mode.color&0xFF)/255f;
			fx.setRBGColorF(r, g, b);
			Minecraft.getMinecraft().effectRenderer.addEffect(fx);
		}
	}
	
	private double distance(double x1, double y1, double z1, double x2, double y2, double z2) {
		double dX = x2 - x1;
		double dY = y2 - y1;
		double dZ = z2 - z1;
		return (double) MathHelper.sqrt_double(dX * dX + dY * dY + dZ * dZ);
	}
	
	private double[] interpolate(double x1, double y1, double z1, double x2, double y2, double z2, double factor) {
		return new double[] { 
				((1.0D - factor) * x1 + factor * x2),
				((1.0D - factor) * y1 + factor * y2),
				((1.0D - factor) * z1 + factor * z2)
		};
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

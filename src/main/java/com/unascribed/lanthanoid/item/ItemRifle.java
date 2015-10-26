package com.unascribed.lanthanoid.item;

import com.unascribed.lanthanoid.LItems;
import com.unascribed.lanthanoid.Lanthanoid;
import com.unascribed.lanthanoid.network.RifleChargingSoundRequest;

import cpw.mods.fml.common.network.NetworkRegistry.TargetPoint;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumAction;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;

public class ItemRifle extends ItemBase {
	private IIcon base;
	private IIcon[] overlays = new IIcon[12];
	
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
			int speed = (selected ? (entity.isInWater() ? 9 : 5) : (slot < 9 ? 3 : 1));
			cool -= speed;
			if (cool % 4 == 0) {
				if (entity.worldObj instanceof WorldServer) {
					((WorldServer)entity.worldObj).func_147487_a("explode", entity.posX, entity.posY, entity.posZ, speed*2, entity.width/2, entity.height/2, entity.width/2, 0);
				}
				entity.playSound("random.fizz", 0.5f, (speed/9f)+0.5f);
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
		if (useRemaining < 50) {
			player.playSound("lanthanoid:rifle_fire", 1.0f, (itemRand.nextFloat()*0.2f)+1.0f);
		}
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

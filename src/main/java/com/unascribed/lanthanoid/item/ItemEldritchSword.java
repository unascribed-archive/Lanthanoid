package com.unascribed.lanthanoid.item;

import java.util.List;

import com.google.common.base.Strings;
import com.google.common.collect.Multimap;
import com.unascribed.lanthanoid.Lanthanoid;
import com.unascribed.lanthanoid.glyph.IGlyphHolderItem;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.item.EnumAction;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraft.util.IIcon;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.MinecraftForge;

public class ItemEldritchSword extends ItemSword implements IGlyphHolderItem {

	private IIcon glyphs;
	
	public ItemEldritchSword(ToolMaterial mat) {
		super(mat);
		setCreativeTab(Lanthanoid.inst.creativeTabEquipment);
		setTextureName("lanthanoid:eldritch_sword");
		setUnlocalizedName("eldritch_sword");
		MinecraftForge.EVENT_BUS.register(this);
	}
	
	@Override
	public int getMaxMilliglyphs(ItemStack stack) {
		return 500_000;
	}
	
	@Override
	public float getStrVsBlock(ItemStack stack, Block block) {
		return getMilliglyphs(stack) > 0 ? super.getStrVsBlock(stack, block) : super.getStrVsBlock(stack, block)/3;
	}
	
	@Override
	public void addInformation(ItemStack stack, EntityPlayer player, List list, boolean advanced) {
		GlyphToolHelper.doAddInformation(this, stack, player, list, advanced);
	}
	
	@Override
	public Multimap getAttributeModifiers(ItemStack stack) {
		Multimap multimap = super.getAttributeModifiers(stack);
		return multimap;
	}
	
	@Override
	public float getDigSpeed(ItemStack stack, Block block, int meta) {
		return getStrVsBlock(stack, block);
	}
	
	@Override
	public void onUpdate(ItemStack stack, World world, Entity entity, int slot, boolean equipped) {
		GlyphToolHelper.doUpdate(this, stack, world, entity, slot, equipped);
		if (world.isRemote) return;
		if (isCharging(stack) && entity instanceof EntityPlayer) {
			if (getMilliglyphs(stack) == 0 || (entity.onGround && getTicksUntilReady(stack) < 90) || getTicksUntilReady(stack) <= 50) {
				setCharging(stack, false);
			} else {
				if (getMilliglyphs(stack) > 0) {
					setMilliglyphs(stack, getMilliglyphs(stack)-1);
					if (world instanceof WorldServer) {
						((WorldServer) world).func_147487_a("enchantmenttable", entity.posX, entity.posY+(entity.height/2), entity.posZ, 2, entity.width/2, entity.height/2, entity.width/2, 0);
					}
				}
				for (Entity ent : (List<Entity>)world.getEntitiesWithinAABB(Entity.class, entity.boundingBox.expand(2, 2, 2))) {
					if (ent instanceof EntityLivingBase) {
						EntityLivingBase elb = (EntityLivingBase)ent;
						if (elb == entity) continue;
						elb.attackEntityFrom(DamageSource.causePlayerDamage((EntityPlayer)entity), 4);
						elb.hurtResistantTime = 4;
						stack.damageItem(1, (EntityLivingBase)entity);
						setMilliglyphs(stack, getMilliglyphs(stack)-Math.min(100, getMilliglyphs(stack)));
						if (world instanceof WorldServer) {
							((WorldServer) world).func_147487_a("enchantmenttable", entity.posX, entity.posY+(entity.height/2), entity.posZ, 8, entity.width/2, entity.height/2, entity.width/2, 0);
						}
					} else if (ent instanceof EntityArrow) {
						EntityArrow arr = (EntityArrow)ent;
						if (!arr.inGround) {
							ent.motionX = ent.motionY = ent.motionZ = 0;
							setMilliglyphs(stack, getMilliglyphs(stack)-Math.min(250, getMilliglyphs(stack)));
							if (world instanceof WorldServer) {
								((WorldServer) world).func_147487_a("enchantmenttable", ent.posX, ent.posY+(ent.height/2), ent.posZ, 8, ent.width/2, ent.height/2, ent.width/2, 0);
							}
						}
					}
				}
			}
		}
		if (getTicksUntilReady(stack) > 0) {
			setTicksUntilReady(stack, getTicksUntilReady(stack)-1);
			if (getTicksUntilReady(stack) == 0) {
				entity.worldObj.playSoundAtEntity(entity, "mob.zombie.unfect", 0.5f, 2.0f);
			}
		}
	}
	
	@Override
	public EnumAction getItemUseAction(ItemStack stack) {
		return EnumAction.bow;
	}
	
	@Override
	public int getMaxItemUseDuration(ItemStack p_77626_1_) {
		return 30;
	}
	
	@Override
	public ItemStack onItemUseFinish(ItemStack p_77654_1_, World p_77654_2_, EntityPlayer player) {
		Vec3 look = player.getLookVec();
		player.motionX = look.xCoord*2;
		player.motionY = look.yCoord*2+0.2;
		player.motionZ = look.zCoord*2;
		player.isAirBorne = true;
		setCharging(p_77654_1_, true);
		player.playSound("lanthanoid:launch", 0.5f, 0.75f);
		setTicksUntilReady(p_77654_1_, 100);
		return p_77654_1_;
	}
	
	@Override
	public ItemStack onItemRightClick(ItemStack itemStackIn, World worldIn, EntityPlayer player) {
		if (getTicksUntilReady(itemStackIn) <= 0 && getMilliglyphs(itemStackIn) > 2000 && !player.isUsingItem()) {
			if (worldIn instanceof WorldServer) {
				((WorldServer) worldIn).func_147487_a("enchantmenttable", player.posX, player.posY+(player.height/2), player.posZ, 20, player.width/2, player.height/2, player.width/2, 0);
			}
			setMilliglyphs(itemStackIn, getMilliglyphs(itemStackIn)-2000);
			player.setItemInUse(itemStackIn, getMaxItemUseDuration(itemStackIn));
		}
		return itemStackIn;
	}
	
	public boolean isCharging(ItemStack stack) {
		return stack.hasTagCompound() ? stack.getTagCompound().getBoolean("charging") : false;
	}
	
	public void setCharging(ItemStack stack, boolean charging) {
		if (!stack.hasTagCompound()) {
			stack.setTagCompound(new NBTTagCompound());
		}
		stack.getTagCompound().setBoolean("charging", charging);
	}
	
	public int getTicksUntilReady(ItemStack stack) {
		return stack.hasTagCompound() ? stack.getTagCompound().getInteger("readyTicks") : 0;
	}
	
	public void setTicksUntilReady(ItemStack stack, int readyTicks) {
		if (!stack.hasTagCompound()) {
			stack.setTagCompound(new NBTTagCompound());
		}
		stack.getTagCompound().setInteger("readyTicks", readyTicks);
	}
	
	@Override
	public String getUnlocalizedNameInefficiently(ItemStack p_77657_1_) {
		return Strings.nullToEmpty(getUnlocalizedName(p_77657_1_));
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public void registerIcons(IIconRegister register) {
		super.registerIcons(register);
		glyphs = register.registerIcon("lanthanoid:eldritch_glyph_kill");
	}
	
	public IIcon getGlyphs() {
		return glyphs;
	}
	
}

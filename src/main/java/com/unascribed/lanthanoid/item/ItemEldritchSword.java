package com.unascribed.lanthanoid.item;

import java.util.List;

import com.google.common.base.Strings;
import com.google.common.collect.Multimap;
import com.unascribed.lanthanoid.Lanthanoid;
import com.unascribed.lanthanoid.glyph.IGlyphHolderItem;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingAttackEvent;

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
		if (entity instanceof EntityPlayer && getHeldDamage(stack) > 0 && !((EntityPlayer) entity).isBlocking()) {
			List<EntityLivingBase> nearby = world.getEntitiesWithinAABBExcludingEntity(entity, entity.boundingBox.expand(2, 2, 2), e -> e instanceof EntityLivingBase);
			float heldDamage = getHeldDamage(stack);
			for (EntityLivingBase elb : nearby) {
				elb.attackEntityFrom(DamageSource.causeThornsDamage(entity), heldDamage/nearby.size());
			}
			setHeldDamage(stack, 0);
			world.playSoundAtEntity(entity, "random.anvil_land", 0.5f, 0.5f);
		}
	}
	
	@SubscribeEvent
	public void onAttacked(LivingAttackEvent e) {
		ItemStack heldItem = e.entityLiving.getHeldItem();
		if (heldItem != null && heldItem.getItem() == this) {
			if (e.entityLiving instanceof EntityPlayer) {
				EntityPlayer p = (EntityPlayer) e.entityLiving;
				if (p.isBlocking() && !e.source.isUnblockable() && p.hurtResistantTime <= p.maxHurtResistantTime/2f) {
					int cost = (int)(e.ammount * 1000);
					if (getMilliglyphs(heldItem) >= cost && getHeldDamage(heldItem) < 40) {
						setMilliglyphs(heldItem, getMilliglyphs(heldItem)-cost);
						setHeldDamage(heldItem, getHeldDamage(heldItem)+(e.ammount/3));
						p.worldObj.playSoundAtEntity(p, "random.anvil_land", 0.5f, (float)(1.75+(Math.random()/4)));
					}
				}
			}
		}
	}
	
	public float getHeldDamage(ItemStack stack) {
		return stack.hasTagCompound() ? stack.getTagCompound().getFloat("heldDamage") : 0;
	}
	
	public void setHeldDamage(ItemStack stack, float heldDamage) {
		if (!stack.hasTagCompound()) {
			stack.setTagCompound(new NBTTagCompound());
		}
		stack.getTagCompound().setFloat("heldDamage", heldDamage);
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

package com.unascribed.lanthanoid.item;

import java.util.List;

import org.lwjgl.opengl.GL11;

import com.unascribed.lanthanoid.Lanthanoid;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.util.StatCollector;

public class ItemGlasses extends ItemArmor {
	private String[] types = {
		"glasses",
		"goggles",
		"bifocals",
		"spectacles",
		"visor"
	};
	public ItemGlasses() {
		super(ArmorMaterial.IRON, 0, 0);
		setCreativeTab(Lanthanoid.inst.creativeTab);
	}
	private IIcon[] icons = new IIcon[types.length];
	@Override
	public boolean getIsRepairable(ItemStack p_82789_1_, ItemStack p_82789_2_) {
		return false;
	}
	@Override
	public boolean isDamaged(ItemStack stack) {
		return false;
	}
	@Override
	public boolean showDurabilityBar(ItemStack stack) {
		return false;
	}
	@Override
	public boolean requiresMultipleRenderPasses() {
		return false;
	}
	@Override
	public int getItemEnchantability() {
		return 0;
	}
	@Override
	public void addInformation(ItemStack stack, EntityPlayer player, List list, boolean advanced) {
		list.add(StatCollector.translateToLocal("item.rcg_"+types[stack.getMetadata()%types.length]+".design"));
		super.addInformation(stack, player, list, advanced);
	}
	@Override
	public boolean isValidArmor(ItemStack stack, int armorType, Entity entity) {
		return armorType == 0;
	}
	@Override
	public boolean isDamageable() {
		return false;
	}
	@Override
	@SideOnly(Side.CLIENT)
	public String getArmorTexture(ItemStack stack, Entity entity, int slot, String type) {
		GL11.glEnable(GL11.GL_BLEND);
		return type == null ? "lanthanoid:textures/models/rcg_"+types[stack.getMetadata()%types.length]+".png" : null;
	}
	@Override
	public void registerIcons(IIconRegister p_94581_1_) {
		for (int i = 0; i < types.length; i++) {
			icons[i] = p_94581_1_.registerIcon("lanthanoid:rcg_"+types[i]);
		}
	}
	@Override
	public void getSubItems(Item item, CreativeTabs tab, List li) {
		for (int i = 0; i < types.length; i++) {
			li.add(new ItemStack(item, 1, i));
		}
	}
	@Override
	public boolean getHasSubtypes() {
		return true;
	}
	@Override
	public IIcon getIconFromDamage(int meta) {
		return icons[meta%icons.length];
	}
	@Override
	public String getUnlocalizedName(ItemStack stack) {
		return "item.rcg_"+types[stack.getMetadata()%types.length];
	}
}

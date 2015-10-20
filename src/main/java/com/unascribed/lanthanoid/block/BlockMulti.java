package com.unascribed.lanthanoid.block;

import java.util.Arrays;
import java.util.List;

import com.google.common.collect.ImmutableList;
import com.unascribed.lanthanoid.MultiHelper;
import com.unascribed.lanthanoid.util.NameDelegate;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.oredict.OreDictionary;

public class BlockMulti extends BlockBase implements NameDelegate {
	protected MultiHelper helper;
	protected Block[] templates;
	protected IIcon errorIcon;
	protected IIcon[] icons;
	
	protected boolean useCompositor = true;
	
	public BlockMulti(Material materialIn, Block defaultTemplate, String... names) {
		super(materialIn);
		helper = new MultiHelper("tile", names);
		icons = new IIcon[names.length];
		templates = new Block[names.length];
		Arrays.fill(templates, defaultTemplate);
		setStepSound(defaultTemplate.stepSound);
	}
	
	public int getMetaForName(String name) {
		return helper.getMetaForName(name);
	}
	
	public ItemStack getStackForName(String name) {
		return new ItemStack(this, 1, getMetaForName(name));
	}
	
	public String getNameForMeta(int meta) {
		return helper.getNameForMeta(meta);
	}
	
	public BlockMulti disableCompositor() {
		useCompositor = false;
		return this;
	}
	
	public BlockMulti setTemplate(String name, Block template) {
		int idx = -1;
		ImmutableList<String> names = helper.getNames();
		for (int i = 0; i < names.size(); i++) {
			if (names.get(i).equals(name)) {
				idx = i;
				break;
			}
		}
		if (idx == -1) throw new IllegalArgumentException("No such name '"+name+"'");
		templates[idx] = template;
		return this;
	}
	
	@Override
	public float getExplosionResistance(Entity par1Entity, World world, int x, int y, int z, double explosionX, double explosionY, double explosionZ) {
		int meta = world.getBlockMetadata(x, y, z);
		if (meta < 0 || meta >= templates.length) return 0;
		return templates[meta].getExplosionResistance(par1Entity, world, x, y, z, explosionX, explosionY, explosionZ);
	}
	
	@Override
	public boolean canEntityDestroy(IBlockAccess world, int x, int y, int z, Entity entity) {
		int meta = world.getBlockMetadata(x, y, z);
		if (meta < 0 || meta >= templates.length) return true;
		return templates[meta].canEntityDestroy(world, x, y, z, entity);
	}
	
	@Override
	public String getHarvestTool(int meta) {
		if (meta < 0 || meta >= templates.length) return null;
		return templates[meta].getHarvestTool(meta);
	}
	
	@Override
	public int getHarvestLevel(int meta) {
		if (meta < 0 || meta >= templates.length) return 0;
		return templates[meta].getHarvestLevel(meta)+2;
	}
	
	@Override
	public float getBlockHardness(World worldIn, int x, int y, int z) {
		int meta = worldIn.getBlockMetadata(x, y, z);
		if (meta < 0 || meta >= templates.length) return 0;
		return templates[meta].getBlockHardness(worldIn, x, y, z);
	}
	
	@Override
	public IIcon getIcon(int side, int meta) {
		if (meta < 0 || meta >= icons.length) {
			return errorIcon;
		}
		return icons[meta];
	}
	
	@Override
	public void getSubBlocks(Item itemIn, CreativeTabs tab, List list) {
		for (int i = 0; i < templates.length; i++) {
			list.add(new ItemStack(itemIn, 1, i));
		}
	}
	
	
	@Override
	@SideOnly(Side.CLIENT)
	public void registerBlockIcons(IIconRegister reg) {
		errorIcon = reg.registerIcon("lanthanoid:error");
		ImmutableList<String> names = helper.getNames();
		for (int i = 0; i < names.size(); i++) {
			String domain;
			if (useCompositor) {
				domain = "lanthanoid_compositor";
			} else {
				domain = "lanthanoid";
			}
			icons[i] = reg.registerIcon(domain+":"+names.get(i));
		}
	}
	
	@Override
	public int damageDropped(int meta) {
		return meta;
	}
	
	public void registerOres() {
		ImmutableList<String> names = helper.getNames();
		for (int i = 0; i < names.size(); i++) {
			OreDictionary.registerOre(names.get(i), new ItemStack(this, 1, i));
		}
	}
	
	@Override
	public String getItemStackDisplayName(ItemStack stack) {
		return helper.getDisplayNameForMeta(stack.getItemDamage());
	}

}

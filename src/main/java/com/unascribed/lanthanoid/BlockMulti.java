package com.unascribed.lanthanoid;

import java.util.Arrays;
import java.util.List;

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
	private String[] names;
	private Block[] templates;
	private IIcon errorIcon;
	private IIcon[] icons;
	
	private boolean useCompositor = true;
	
	protected BlockMulti(Material materialIn, Block defaultTemplate, String... names) {
		super(materialIn);
		this.names = names;
		icons = new IIcon[names.length];
		templates = new Block[names.length];
		Arrays.fill(templates, defaultTemplate);
		setStepSound(defaultTemplate.stepSound);
	}
	
	public BlockMulti disableCompositor() {
		useCompositor = false;
		return this;
	}
	
	public BlockMulti setTemplate(String name, Block template) {
		int idx = -1;
		for (int i = 0; i < names.length; i++) {
			if (names[i].equals(name)) {
				idx = i;
			}
		}
		if (idx == -1) throw new IllegalArgumentException("No such name '"+name+"'");
		templates[idx] = template;
		return this;
	}
	
	@Override
	public float getExplosionResistance(Entity par1Entity, World world, int x, int y, int z, double explosionX, double explosionY, double explosionZ) {
		int meta = world.getBlockMetadata(x, y, z);
		if (meta < 0 || meta >= names.length) return 0;
		return templates[meta].getExplosionResistance(par1Entity, world, x, y, z, explosionX, explosionY, explosionZ);
	}
	
	@Override
	public boolean canEntityDestroy(IBlockAccess world, int x, int y, int z, Entity entity) {
		int meta = world.getBlockMetadata(x, y, z);
		if (meta < 0 || meta >= names.length) return true;
		return templates[meta].canEntityDestroy(world, x, y, z, entity);
	}
	
	@Override
	public String getHarvestTool(int meta) {
		if (meta < 0 || meta >= names.length) return null;
		return templates[meta].getHarvestTool(meta);
	}
	
	@Override
	public int getHarvestLevel(int meta) {
		if (meta < 0 || meta >= names.length) return 0;
		return templates[meta].getHarvestLevel(meta)+2;
	}
	
	@Override
	public float getBlockHardness(World worldIn, int x, int y, int z) {
		int meta = worldIn.getBlockMetadata(x, y, z);
		if (meta < 0 || meta >= names.length) return 0;
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
		for (int i = 0; i < names.length; i++) {
			list.add(new ItemStack(itemIn, 1, i));
		}
	}
	
	@Override
	public void registerIcons(IIconRegister reg) {
		errorIcon = reg.registerIcon("lanthanoid:error");
		for (int i = 0; i < names.length; i++) {
			String domain;
			if (useCompositor) {
				domain = "lanthanoid_compositor";
			} else {
				domain = "lanthanoid";
			}
			icons[i] = reg.registerIcon(domain+":"+names[i]);
		}
	}

	@Override
	public String getUnlocalizedName(int meta) {
		if (meta < 0 || meta >= names.length) return "tile.error";
		return "tile."+names[meta];
	}
	
	@Override
	public int damageDropped(int meta) {
		return meta;
	}
	
	public void registerOres() {
		for (int i = 0; i < names.length; i++) {
			OreDictionary.registerOre(names[i], new ItemStack(this, 1, i));
		}
	}

}

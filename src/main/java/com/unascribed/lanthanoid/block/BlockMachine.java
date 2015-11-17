package com.unascribed.lanthanoid.block;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Random;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.unascribed.lanthanoid.Lanthanoid;
import com.unascribed.lanthanoid.init.LItems;
import com.unascribed.lanthanoid.proxy.ClientProxy;
import com.unascribed.lanthanoid.tile.TileEntityEldritch;
import com.unascribed.lanthanoid.tile.TileEntityWaypoint;
import com.unascribed.lanthanoid.util.NameDelegate;
import com.unascribed.lanthanoid.waypoint.Waypoint;
import com.unascribed.lanthanoid.waypoint.Waypoint.Type;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.block.BlockFurnace;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.EntityReddustFX;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.command.ICommandSender;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.passive.EntitySheep;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.RecipesArmorDyes;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.IIcon;
import net.minecraft.util.StatCollector;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.oredict.OreDictionary;

public class BlockMachine extends BlockBase implements NameDelegate {

	private IIcon[] bottoms = new IIcon[16];
	private IIcon[] tops = new IIcon[16];
	private IIcon[] sides = new IIcon[16];
	
	public IIcon collectorGlyphs, distributorGlyphs, chargerGlyphs, coil;
	
	public BlockMachine() {
		super(Material.iron);
		setHarvestLevel("pickaxe", 2);
		setResistance(5000);
		setHardness(2);
	}
	
	@Override
	public IIcon getIcon(int side, int meta) {
		if (side == 0) {
			return bottoms[meta];
		}
		if (side == 1) {
			return tops[meta];
		}
		return sides[meta];
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public void randomDisplayTick(World world, int x, int y, int z, Random rand) {
		Waypoint w = Lanthanoid.inst.waypointManager.getWaypoint(world, x, y, z);
		if (w != null && w.type == Type.MARKER) {
			float r = ((w.color>>16)&0xFF)/255f;
			float g = ((w.color>>8)&0xFF)/255f;
			float b = (w.color&0xFF)/255f;
			for (int i = 0; i < 5; i++) {
				EntityReddustFX fx = new EntityReddustFX(world, x+0.5, y+1+(i/30f), z+0.5, 0, 0, 0);
				float rn = rand.nextFloat()/4;
				fx.setRBGColorF((r-0.25f)+rn, (g-0.25f)+rn, (b-0.25f)+rn);
				Minecraft.getMinecraft().effectRenderer.addEffect(fx);
			}
		}
	}
	
	@Override
	public int getRenderColor(int meta) {
		if (meta == 3) return 0x00AAFF;
		if (meta == 4) return 0xFFAA00;
		if (meta == 5) return 0x00FF00;
		return super.getRenderColor(meta);
	}
	
	@Override
	public int colorMultiplier(IBlockAccess world, int x, int y, int z) {
		return getRenderColor(world.getBlockMetadata(x, y, z));
	}
	
	@Override
	public boolean isOpaqueCube() {
		return false;
	}
	
	@Override
	public boolean hasTileEntity(int metadata) {
		return metadata >= 0 && metadata <= 5;
	}
	
	@Override
	public TileEntity createTileEntity(World world, int metadata) {
		return (metadata >= 0 && metadata <= 2) ? new TileEntityWaypoint() : (metadata >= 3 && metadata <= 5) ? new TileEntityEldritch() : null;
	}
	
	@Override
	public void onBlockPlacedBy(World world, int x, int y, int z, EntityLivingBase placer, ItemStack stack) {
		if (!world.isRemote && (stack.getItemDamage() == 0 || stack.getItemDamage() == 1 || stack.getItemDamage() == 2)) {
			Waypoint waypoint = new Waypoint();
			waypoint.setId();
			waypoint.x = x;
			waypoint.y = y;
			waypoint.z = z;
			waypoint.name = stack.getDisplayName();
			waypoint.ownerName = placer.getCommandSenderName();
			waypoint.type = Waypoint.Type.values()[stack.getItemDamage()];
			waypoint.color = stack.hasTagCompound() && stack.getTagCompound().hasKey("Color", 99) ?
					stack.getTagCompound().getInteger("Color") :
					0xFFFFFF;
			waypoint.owner = placer instanceof EntityPlayer ? ((EntityPlayer)placer).getGameProfile().getId() : placer.getPersistentID();
			waypoint.nameDistance = 20;
			Lanthanoid.inst.waypointManager.setWaypoint(world, x, y, z, waypoint);
		}
	}
	
	private static final Map<String, float[]> dyes = ImmutableMap.<String, float[]>builder()
			.put("dyeBlack", new float[] { 0f, 0f, 0f })
			.put("dyeRed", new float[] { 1f, 0f, 0f })
			.put("dyeGreen", new float[] { 0f, 1f, 0f })
			.put("dyeBrown", new float[] { 0.75f, 0.5f, 0f })
			.put("dyeBlue", new float[] { 0f, 0f, 1f })
			.put("dyePurple", new float[] { 1f, 0f, 0.5f })
			.put("dyeCyan", new float[] { 0f, 1f, 1f })
			.put("dyeLightGray", new float[] { 0.6f, 0.6f, 0.6f })
			.put("dyeGray", new float[] { 0.35f, 0.35f, 0.35f })
			.put("dyePink", new float[] { 1f, 0.5f, 0.65f })
			.put("dyeLime", new float[] { 0.75f, 1f, 0f })
			.put("dyeYellow", new float[] { 1f, 1f, 0f })
			.put("dyeLightBlue", new float[] { 0.5f, 0.5f, 1f })
			.put("dyeMagenta", new float[] { 1f, 0f, 1f })
			.put("dyeOrange", new float[] { 1f, 0.5f, 0f })
			.put("dyeWhite", new float[] { 1f, 1f, 1f })
			.build();
	
	@Override
	public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side, float subX, float subY, float subZ) {
		int meta = world.getBlockMetadata(x, y, z);
		if (meta == 0 || meta == 1 || meta == 2) {
			Waypoint w = Lanthanoid.inst.waypointManager.getWaypoint(world, x, y, z);
			if (w != null) {
				ItemStack held = player.getHeldItem();
				if (held != null) {
					if (held.getItem() == LItems.spanner) {
						if (player.isSneaking()) {
							w.nameDistance = Math.max(w.nameDistance-5, 5);
						} else {
							w.nameDistance = Math.min(w.nameDistance+5, 260);
						}
						Lanthanoid.inst.waypointManager.setWaypoint(world, x, y, z, w);
						return true;
					} else {
						float times = 3;
						float[] resultColor = new float[] {
							((w.color>>16)&255)/255f,
							((w.color>>8)&255)/255f,
							(w.color&255)/255f,
						};
						resultColor[0] *= times;
						resultColor[1] *= times;
						resultColor[2] *= times;
						int[] ids = OreDictionary.getOreIDs(held);
						boolean use = false;
						for (int id : ids) {
							String name = OreDictionary.getOreName(id);
							if (dyes.containsKey(name)) {
								float[] color = dyes.get(name);
								resultColor[0] += color[0];
								resultColor[1] += color[1];
								resultColor[2] += color[2];
								times++;
								use = true;
							}
						}
						if (use) {
							resultColor[0] /= times;
							resultColor[1] /= times;
							resultColor[2] /= times;
							int packed = 0;
							packed |= (((int)((resultColor[0]*255))&255)<<16);
							packed |= (((int)((resultColor[1]*255))&255)<<8);
							packed |= ((int)((resultColor[2]*255))&255);
							if (packed == w.color) {
								return false;
							}
							if (!player.capabilities.isCreativeMode) {
								held.stackSize--;
							}
							w.color = packed;
							Lanthanoid.inst.waypointManager.setWaypoint(world, x, y, z, w);
							return true;
						}
					}
				}
			}
		}
		return false;
	}
	
	@Override
	public void breakBlock(World world, int x, int y, int z, Block block, int meta) {
		super.breakBlock(world, x, y, z, block, meta);
		if (!world.isRemote && FMLCommonHandler.instance().getEffectiveSide() == Side.SERVER) {
			Lanthanoid.inst.waypointManager.removeWaypointLater(world, x, y, z);
		}
	}

	@Override
	public ArrayList<ItemStack> getDrops(World world, int x, int y, int z, int metadata, int fortune) {
		if (metadata == 0 || metadata == 1 || metadata == 2) {
			ItemStack stack = new ItemStack(this, 1, metadata);
			stack.setTagCompound(new NBTTagCompound());
			Waypoint w = Lanthanoid.inst.waypointManager.getWaypoint(world, x, y, z);
			if (w != null) {
				stack.setStackDisplayName(w.name);
				stack.getTagCompound().setInteger("Color", w.color);
			}
			return Lists.newArrayList(stack);
		} else {
			return super.getDrops(world, x, y, z, metadata, fortune);
		}
	}
	
	
	@Override
	public void registerBlockIcons(IIconRegister register) {
		IIcon holmiumBottom = register.registerIcon("lanthanoid_compositor:machineWaypointBottomHolmium");
		IIcon yttriumBottom = register.registerIcon("lanthanoid_compositor:machineWaypointBottomYttrium");
		IIcon holmiumTop = register.registerIcon("lanthanoid_compositor:machineWaypointTopHolmium");
		IIcon yttriumTop = register.registerIcon("lanthanoid_compositor:machineWaypointTopYttrium");
		
		IIcon raspiteSide = register.registerIcon("lanthanoid_compositor:machineWaypointSideRaspite");
		IIcon diamondSide = register.registerIcon("lanthanoid_compositor:machineWaypointSideDiamond");
		IIcon diasporeSide = register.registerIcon("lanthanoid_compositor:machineWaypointSideDiaspore");
		
		IIcon eldritch = register.registerIcon("lanthanoid:eldritch");
		IIcon charger = register.registerIcon("lanthanoid:eldritch_charger");
		IIcon distributor = register.registerIcon("lanthanoid:eldritch_distributor");
		IIcon collector = register.registerIcon("lanthanoid:eldritch_collector");
		
		IIcon error = register.registerIcon("lanthanoid:error");
		
		Arrays.fill(bottoms, holmiumBottom);
		bottoms[2] = yttriumBottom;
		
		Arrays.fill(tops, holmiumTop);
		tops[2] = yttriumTop;
		
		Arrays.fill(sides, error);
		sides[0] = raspiteSide;
		sides[1] = diamondSide;
		sides[2] = diasporeSide;
		
		for (int i = 3; i <= 5; i++) { 
			bottoms[i] = tops[i] = eldritch;
		}
		tops[5] = charger;
		sides[3] = collector;
		sides[4] = distributor;
		sides[5] = eldritch;
		
		collectorGlyphs = register.registerIcon("lanthanoid:eldritch_glyph_take");
		distributorGlyphs = register.registerIcon("lanthanoid:eldritch_glyph_give");
		chargerGlyphs = register.registerIcon("lanthanoid:eldritch_glyph_charge");
		coil = register.registerIcon("lanthanoid:eldritch_charger_coil");
	}

	@Override
	public void getSubBlocks(Item item, CreativeTabs tab, List li) {
		for (int i = 0; i < 6; i++) {
			li.add(new ItemStack(item, 1, i));
		}
	}
	
	@Override
	public String getUnlocalizedName(ItemStack stack) {
		return "tile.machine."+stack.getItemDamage();
	}
	
	@Override
	public String getItemStackDisplayName(ItemStack stack) {
		return StatCollector.translateToLocal(getUnlocalizedName(stack)+".name");
	}
	
	@Override
	public int damageDropped(int meta) {
		return meta;
	}

}


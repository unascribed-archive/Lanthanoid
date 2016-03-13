package com.unascribed.lanthanoid.block;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import com.google.common.collect.Lists;
import com.unascribed.lanthanoid.Lanthanoid;
import com.unascribed.lanthanoid.tile.IActivatable;
import com.unascribed.lanthanoid.tile.IBounded;
import com.unascribed.lanthanoid.tile.IBreakable;
import com.unascribed.lanthanoid.tile.IFallable;
import com.unascribed.lanthanoid.tile.IIconProvider;
import com.unascribed.lanthanoid.tile.IPlaceable;
import com.unascribed.lanthanoid.tile.TileEntityEldritchBoostPad;
import com.unascribed.lanthanoid.tile.TileEntityEldritchCollector;
import com.unascribed.lanthanoid.tile.TileEntityEldritchDistributor;
import com.unascribed.lanthanoid.tile.TileEntityEldritchFaithPlate;
import com.unascribed.lanthanoid.tile.TileEntityEldritchInductor;
import com.unascribed.lanthanoid.tile.TileEntityEldritchInfiniteSource;
import com.unascribed.lanthanoid.tile.TileEntityInventoryGrate;
import com.unascribed.lanthanoid.tile.TileEntityWaypoint;
import com.unascribed.lanthanoid.util.NameDelegate;
import com.unascribed.lanthanoid.waypoint.Waypoint;
import com.unascribed.lanthanoid.waypoint.Waypoint.Type;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.EntityReddustFX;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.IIcon;
import net.minecraft.util.StatCollector;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockMachine extends BlockBase implements NameDelegate {

	public static boolean grateBounds = false;
	private IIcon[] bottoms = new IIcon[16];
	private IIcon[] tops = new IIcon[16];
	private IIcon[] sides = new IIcon[16];

	public IIcon collectorGlyphs, distributorGlyphs, chargerGlyphs, coil, faithPlateGlyphs, boostPadGlyphs;
	public IIcon boost_pad_noanim, boost_pad_90_noanim, boost_pad_180_noanim, boost_pad_270_noanim;
	public IIcon boost_pad, boost_pad_90, boost_pad_180, boost_pad_270;

	public BlockMachine() {
		super(Lanthanoid.inst.creativeTabMachines, Material.iron);
		setHarvestLevel("pickaxe", 2);
		setResistance(5000);
		setHardness(2);
	}

	@Override
	public boolean getUseNeighborBrightness() {
		return true;
	}
	
	@Override
	public IIcon getIcon(int side, int meta) {
		if (side == 0) {
			if (meta == 8) return boost_pad_noanim;
			return bottoms[meta];
		}
		if (side == 1) {
			if (meta == 8) return boost_pad_noanim;
			return tops[meta];
		}
		return sides[meta];
	}
	
	@Override
	public IIcon getIcon(IBlockAccess world, int x, int y, int z, int side) {
		TileEntity te = world.getTileEntity(x, y, z);
		if (te instanceof IIconProvider) {
			IIcon fromTe = ((IIconProvider)te).getIcon(side);
			if (fromTe != null) return fromTe;
		}
		return super.getIcon(world, x, y, z, side);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void randomDisplayTick(World world, int x, int y, int z, Random rand) {
		Waypoint w = Lanthanoid.inst.waypointManager.getWaypoint(world, x, y, z);
		if (w != null && w.type == Type.MARKER) {
			float r = ((w.color >> 16) & 0xFF) / 255f;
			float g = ((w.color >> 8) & 0xFF) / 255f;
			float b = (w.color & 0xFF) / 255f;
			for (int i = 0; i < 5; i++) {
				EntityReddustFX fx = new EntityReddustFX(world, x + 0.5, y + 1 + (i / 30f), z + 0.5, 0, 0, 0);
				float rn = rand.nextFloat() / 4;
				fx.setRBGColorF((r - 0.25f) + rn, (g - 0.25f) + rn, (b - 0.25f) + rn);
				Minecraft.getMinecraft().effectRenderer.addEffect(fx);
			}
		}
	}

	@Override
	public int getRenderColor(int meta) {
		if (meta == 3) {
			return 0x00AAFF;
		}
		if (meta == 4) {
			return 0xFFAA00;
		}
		if (meta == 5) {
			return 0x00FF00;
		}
		if (meta == 6) {
			return 0xFFFFFF;
		}
		if (meta == 7) {
			return 0xFF00FF;
		}
		if (meta == 8) {
			return 0x00FFAA;
		}
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
		return metadata >= 0 && metadata <= 9;
	}

	@Override
	public TileEntity createTileEntity(World world, int metadata) {
		switch (metadata) {
			case 0:
			case 1:
			case 2:
				return new TileEntityWaypoint();
			case 3:
				return new TileEntityEldritchCollector();
			case 4:
				return new TileEntityEldritchDistributor();
			case 5:
				return new TileEntityEldritchInductor();
			case 6:
				return new TileEntityEldritchFaithPlate();
			case 7:
				return new TileEntityEldritchInfiniteSource();
			case 8:
				return new TileEntityEldritchBoostPad();
			case 9:
				return new TileEntityInventoryGrate();
			default:
				return null;
		}
	}

	@Override
	public void onBlockPlacedBy(World world, int x, int y, int z, EntityLivingBase placer, ItemStack stack) {
		if (!world.isRemote) {
			if (stack.getMetadata() == 0 || stack.getMetadata() == 1 || stack.getMetadata() == 2) {
				Waypoint waypoint = new Waypoint();
				waypoint.setId();
				waypoint.x = x;
				waypoint.y = y;
				waypoint.z = z;
				waypoint.name = stack.getDisplayName();
				waypoint.ownerName = placer.getCommandSenderName();
				waypoint.type = Waypoint.Type.values()[stack.getMetadata()];
				waypoint.color = stack.hasTagCompound() && stack.getTagCompound().hasKey("Color", 99) ? stack.getTagCompound().getInteger("Color") : 0xFFFFFF;
				waypoint.owner = placer instanceof EntityPlayer ? ((EntityPlayer) placer).getGameProfile().getId() : placer.getPersistentID();
				waypoint.nameDistance = 20;
				Lanthanoid.inst.waypointManager.setWaypoint(world, x, y, z, waypoint);
			} else {
				TileEntity te = world.getTileEntity(x, y, z);
				if (te instanceof IPlaceable) {
					((IPlaceable) te).onBlockPlacedBy(placer, stack);
				}
			}
		}
	}

	@Override
	public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side, float subX, float subY, float subZ) {
		TileEntity te = world.getTileEntity(x, y, z);
		if (te instanceof IActivatable) {
			return ((IActivatable)te).onBlockActivated(player, side, subX, subY, subZ);
		}
		return false;
	}
	
	@Override
	public AxisAlignedBB getCollisionBoundingBoxFromPool(World world, int x, int y, int z) {
		TileEntity te = world.getTileEntity(x, y, z);
		if (te instanceof IBounded) {
			return ((IBounded)te).getBoundingBox().getOffsetBoundingBox(x, y, z);
		} else {
			setBlockBoundsBasedOnState(world, x, y, z);
			return AxisAlignedBB.getBoundingBox(x+minX, y+minY, z+minZ, x+maxX, y+maxY, z+maxZ);
		}
	}
	
	@Override
	public AxisAlignedBB getSelectedBoundingBoxFromPool(World world, int x, int y, int z) {
		return getCollisionBoundingBoxFromPool(world, x, y, z);
	}
	
	@Override
	public void setBlockBoundsBasedOnState(IBlockAccess world, int x, int y, int z) {
		TileEntity te = world.getTileEntity(x, y, z);
		if (te instanceof IBounded) {
			AxisAlignedBB aabb = ((IBounded)te).getBoundingBox();
			setBlockBounds((float)aabb.minX, (float)aabb.minY, (float)aabb.minZ, (float)aabb.maxX, (float)aabb.maxY, (float)aabb.maxZ);
		} else {
			int meta = world.getBlockMetadata(x, y, z);
			if (meta == 5) {
				setBlockBounds(0, 0, 0, 1, 31/32f, 1);
			} else if (meta == 9) {
				setBlockBounds(0, 0, 0, 1, 0.0625f, 1);
			} else {
				setBlockBounds(0, 0, 0, 1, 1, 1);
			}
		}
	}
	
	@Override
	public void setBlockBoundsForItemRender() {
		if (grateBounds) {
			setBlockBounds(0, 0, 0, 1, 0.0625f, 1);
		} else {
			setBlockBounds(0, 0, 0, 1, 1, 1);
		}
	}
	
	@Override
	public void onFallenUpon(World world, int x, int y, int z, Entity entity, float fallDistance) {
		TileEntity te = world.getTileEntity(x, y, z);
		if (te instanceof IFallable) {
			((IFallable)te).onFallenUpon(world, x, y, z, entity, fallDistance);
		}
	}
	
	@Override
	public void breakBlock(World world, int x, int y, int z, Block block, int meta) {
		TileEntity teRaw = world.getTileEntity(x, y, z);
		if (teRaw instanceof IBreakable) {
			((IBreakable)teRaw).breakBlock();
		}
		super.breakBlock(world, x, y, z, block, meta);
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
		}
		return super.getDrops(world, x, y, z, metadata, fortune);
	}
	
	@Override
	public boolean isNormalCube(IBlockAccess world, int x, int y, int z) {
		return world.getBlockMetadata(x, y, z) != 9;
	}

	
	@Override
	public void registerIcons(IIconRegister register) {
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
		IIcon faith_plate = register.registerIcon("lanthanoid:eldritch_faith");
		IIcon grate_top = register.registerIcon("lanthanoid:inventory_grate");
		
		boost_pad = register.registerIcon("lanthanoid:eldritch_boost_pad");
		boost_pad_90 = register.registerIcon("lanthanoid:eldritch_boost_pad_90");
		boost_pad_180 = register.registerIcon("lanthanoid:eldritch_boost_pad_180");
		boost_pad_270 = register.registerIcon("lanthanoid:eldritch_boost_pad_270");
		boost_pad_noanim = register.registerIcon("lanthanoid:eldritch_boost_pad_noanim");
		boost_pad_90_noanim = register.registerIcon("lanthanoid:eldritch_boost_pad_90_noanim");
		boost_pad_180_noanim = register.registerIcon("lanthanoid:eldritch_boost_pad_180_noanim");
		boost_pad_270_noanim = register.registerIcon("lanthanoid:eldritch_boost_pad_270_noanim");
		
		IIcon chargerTop = register.registerIcon("lanthanoid:eldritch_charger_top");

		IIcon error = register.registerIcon("lanthanoid:error");

		Arrays.fill(bottoms, holmiumBottom);
		bottoms[2] = yttriumBottom;

		Arrays.fill(tops, holmiumTop);
		tops[2] = yttriumTop;

		Arrays.fill(sides, error);
		sides[0] = raspiteSide;
		sides[1] = diamondSide;
		sides[2] = diasporeSide;

		for (int i = 3; i <= 8; i++) {
			bottoms[i] = tops[i] = eldritch;
		}
		tops[5] = chargerTop;
		tops[9] = grate_top;
		bottoms[9] = grate_top;
		sides[3] = collector;
		sides[4] = distributor;
		sides[5] = charger;
		sides[6] = faith_plate;
		sides[7] = eldritch;
		sides[8] = eldritch;
		sides[9] = eldritch;

		collectorGlyphs = register.registerIcon("lanthanoid:eldritch_glyph_take");
		distributorGlyphs = register.registerIcon("lanthanoid:eldritch_glyph_give");
		chargerGlyphs = register.registerIcon("lanthanoid:eldritch_glyph_charge");
		coil = register.registerIcon("lanthanoid:eldritch_charger_coil");
		faithPlateGlyphs = register.registerIcon("lanthanoid:eldritch_glyph_jump");
		boostPadGlyphs = register.registerIcon("lanthanoid:eldritch_glyph_fast");
	}

	@Override
	public boolean onBlockEventReceived(World world, int x, int y, int z, int event, int arg) {
		if (!world.isRemote) {
			return true;
		}
		return world.getTileEntity(x, y, z) == null ? false : world.getTileEntity(x, y, z).receiveClientEvent(event, arg);
	}

	@Override
	public void getSubBlocks(Item item, CreativeTabs tab, List li) {
		for (int i = 0; i <= 9; i++) {
			li.add(new ItemStack(item, 1, i));
		}
	}

	@Override
	public String getUnlocalizedName(ItemStack stack) {
		return "tile.machine." + stack.getMetadata();
	}

	@Override
	public String getItemStackDisplayName(ItemStack stack) {
		return StatCollector.translateToLocal(getUnlocalizedName(stack) + ".name");
	}

	@Override
	public int damageDropped(int meta) {
		return meta;
	}

}

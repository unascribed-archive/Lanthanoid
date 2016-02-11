package com.unascribed.lanthanoid.item.eldritch.tool;

import java.util.List;
import java.util.Set;

import com.google.common.base.Strings;
import com.google.common.collect.Sets;
import com.unascribed.lanthanoid.Lanthanoid;
import com.unascribed.lanthanoid.glyph.IGlyphHolderItem;
import com.unascribed.lanthanoid.item.GlyphToolHelper;
import com.unascribed.lanthanoid.util.LUtil;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent.Phase;
import cpw.mods.fml.common.gameevent.TickEvent.ServerTickEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemAxe;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;

public class ItemEldritchAxe extends ItemAxe implements IGlyphHolderItem {

	public class BreakTask {
		public World world;
		public int x, y, z;
		public ItemStack stack;
		public EntityPlayerMP player;
		public Block expected;
		public int expectedMeta;
		public Block initial;
		
		public BreakTask(World world, int x, int y, int z, ItemStack stack, EntityPlayerMP player, Block expected, int expectedMeta, Block initial) {
			this.world = world;
			this.x = x;
			this.y = y;
			this.z = z;
			this.stack = stack;
			this.player = player;
			this.expected = expected;
			this.expectedMeta = expectedMeta;
			this.initial = initial;
		}

		public void execute() {
			if (player.isDead) return;
			breaking = true;
			Block block = world.getBlock(x, y, z);
			int meta = world.getBlockMetadata(x, y, z);
			if (block == expected && meta == expectedMeta && GlyphToolHelper.doBlockDestroyed(getOuterType(), stack, world, block, x, y, z, player)) {
				stack.damageItem(1, player);
				LUtil.harvest(player, world, x, y, z, true, true, false);
				if (stack.getMetadata() >= stack.getMaxDurability()) {
					player.destroyCurrentEquippedItem();
				} else {
					addSurroundings(world, x, y, z, stack, player, initial);
				}
			}
			breaking = false;
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + ((expected == null) ? 0 : expected.hashCode());
			result = prime * result + expectedMeta;
			result = prime * result + ((initial == null) ? 0 : initial.hashCode());
			result = prime * result + ((player == null) ? 0 : player.hashCode());
			result = prime * result + ((stack == null) ? 0 : stack.hashCode());
			result = prime * result + ((world == null) ? 0 : world.hashCode());
			result = prime * result + x;
			result = prime * result + y;
			result = prime * result + z;
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj) {
				return true;
			}
			if (obj == null) {
				return false;
			}
			if (getClass() != obj.getClass()) {
				return false;
			}
			BreakTask other = (BreakTask) obj;
			if (getOuterType() != other.getOuterType()) {
				return false;
			}
			if (expected == null) {
				if (other.expected != null) {
					return false;
				}
			} else if (!expected.equals(other.expected)) {
				return false;
			}
			if (expectedMeta != other.expectedMeta) {
				return false;
			}
			if (initial == null) {
				if (other.initial != null) {
					return false;
				}
			} else if (!initial.equals(other.initial)) {
				return false;
			}
			if (player == null) {
				if (other.player != null) {
					return false;
				}
			} else if (!player.equals(other.player)) {
				return false;
			}
			if (stack == null) {
				if (other.stack != null) {
					return false;
				}
			} else if (!stack.equals(other.stack)) {
				return false;
			}
			if (world == null) {
				if (other.world != null) {
					return false;
				}
			} else if (!world.equals(other.world)) {
				return false;
			}
			if (x != other.x) {
				return false;
			}
			if (y != other.y) {
				return false;
			}
			if (z != other.z) {
				return false;
			}
			return true;
		}

		private ItemEldritchAxe getOuterType() {
			return ItemEldritchAxe.this;
		}
		
		
	}

	private IIcon glyphs;
	
	private Set<BreakTask> breakTasks = Sets.newHashSet();
	
	public ItemEldritchAxe(ToolMaterial mat) {
		super(mat);
		setCreativeTab(Lanthanoid.inst.creativeTabEquipment);
		setTextureName("lanthanoid:eldritch_axe");
		setUnlocalizedName("eldritch_axe");
		FMLCommonHandler.instance().bus().register(this);
	}
	
	public void addSurroundings(World world, int x, int y, int z, ItemStack stack, EntityPlayerMP player, Block initial) {
		for (int oX = -1; oX <= 1; oX ++) {
			for (int oY = -1; oY <= 1; oY ++) {
				for (int oZ = -1; oZ <= 1; oZ ++) {
					if (oX == 0 && oY == 0 && oZ == 0) continue;
					int cX = x+oX;
					int cY = y+oY;
					int cZ = z+oZ;
					Block b = world.getBlock(cX, cY, cZ);
					int meta = world.getBlockMetadata(cX, cY, cZ);
					if (b == initial && b.getMaterial() == Material.wood && b.isToolEffective("axe", meta)) {
						breakTasks.add(new BreakTask(world, cX, cY, cZ, stack, player, b, meta, initial));
					}
				}
			}
		}
	}

	@SubscribeEvent
	public void onTick(ServerTickEvent e) {
		if (e.phase == Phase.END) {
			BreakTask[] tasks = breakTasks.toArray(new BreakTask[breakTasks.size()]);
			breakTasks.clear();
			for (BreakTask bt : tasks) {
				bt.execute();
			}
		}
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
	public float getDigSpeed(ItemStack stack, Block block, int meta) {
		return getStrVsBlock(stack, block);
	}
	
	@Override
	public void onUpdate(ItemStack stack, World world, Entity entity, int slot, boolean equipped) {
		GlyphToolHelper.doUpdate(this, stack, world, entity, slot, equipped);
	}
	
	private boolean breaking = false;
	
	@Override
	public boolean onBlockDestroyed(ItemStack stack, World world, Block block, int x, int y, int z, EntityLivingBase ent) {
		if (GlyphToolHelper.doBlockDestroyed(this, stack, world, block, x, y, z, ent) && !breaking && ent instanceof EntityPlayerMP && !ent.isSneaking()) {
			addSurroundings(world, x, y, z, stack, (EntityPlayerMP)ent, block);
		}
		return true;
	}
	
	@Override
	public String getUnlocalizedNameInefficiently(ItemStack p_77657_1_) {
		return Strings.nullToEmpty(getUnlocalizedName(p_77657_1_));
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public void registerIcons(IIconRegister register) {
		super.registerIcons(register);
		glyphs = register.registerIcon("lanthanoid:eldritch_glyph_chop");
	}
	
	public IIcon getGlyphs() {
		return glyphs;
	}
	
}

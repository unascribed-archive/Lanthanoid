package com.unascribed.lanthanoid.init;

import java.util.List;

import com.google.common.collect.Lists;
import com.unascribed.lanthanoid.item.rifle.Variant;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.Achievement;
import net.minecraftforge.common.AchievementPage;

public class LAchievements {
	public static Achievement
							craftTeleporter,
							selfTelefrag,
							telefrag,
							telefragMount,
							telesnipe,
							
							craftRifle,
							modifyRifle,
							modifyRifle2,
							scope,
							ironsights,
							cornerDeflect,
							fullChain,
							fullChainKill,
							snipe
	;
	
	
	private static List<Achievement> li = Lists.newArrayList();
	public static AchievementPage page;
	
	public static void init() {
		addAchievement("craftTeleporter", 0, 0, LItems.teleporter, null);
		addAchievement("selfTelefrag", 2, -1, Blocks.stone, craftTeleporter);
		
		addAchievement("telefrag", 1, 2, LItems.dust.getStackForName("dustYttrium"), craftTeleporter);
		addAchievement("telefragMount", 3, 3, Items.saddle, 0, craftTeleporter);
		
		addAchievement("telesnipe", 3, 1, LItems.teleporter, 7, telefrag).setSpecial();
		
		
		
		addAchievement("craftRifle", -2, 0, LItems.rifle, null);
		
		addAchievement("modifyRifle", -3, -2, LItems.rifle, Variant.OVERCLOCK.ordinal(), craftRifle);
		addAchievement("modifyRifle2", -4, -3, LItems.rifle, Variant.SUPEREFFICIENCY.ordinal(), modifyRifle);
		
		addAchievement("scope", -5, 2, LItems.rifle, Variant.ZOOM.ordinal(), craftRifle);
		
		addAchievement("ironsights", -4, 1, Items.iron_ingot, craftRifle);
		
		addAchievement("cornerDeflect", -6, 0, LItems.dust.getStackForName("dustPraseodymium"), craftRifle);
		
		addAchievement("fullChain", -5, -1, LItems.dust.getStackForName("dustNeodymium"), craftRifle);
		addAchievement("fullChainKill", -7, -2, LItems.dust.getStackForName("dustErbium"), fullChain).setSpecial();
		
		addAchievement("snipe", -8, 1, LItems.gem.getStackForName("gemRaspite"), scope).setSpecial();
		
		for (Achievement a : li) {
			a.registerStat();
		}
		
		page = new AchievementPage("Lanthanoid", li.toArray(new Achievement[li.size()]));
	}
	/*
	private static Achievement addAchievement(String name, int x, int y, Block block, int meta, Achievement dependency) {
		return addAchievement(name, x, y, new ItemStack(block, 1, meta), dependency);
	}
	*/
	private static Achievement addAchievement(String name, int x, int y, Item item, int meta, Achievement dependency) {
		return addAchievement(name, x, y, new ItemStack(item, 1, meta), dependency);
	}
	
	private static Achievement addAchievement(String name, int x, int y, Block block, Achievement dependency) {
		return addAchievement(name, x, y, new ItemStack(block), dependency);
	}
	
	private static Achievement addAchievement(String name, int x, int y, Item item, Achievement dependency) {
		return addAchievement(name, x, y, new ItemStack(item), dependency);
	}
	
	private static Achievement addAchievement(String name, int x, int y, ItemStack stack, Achievement dependency) {
		Achievement a = new Achievement(name, name, x, y, stack, dependency);
		if (dependency == null) {
			a.initIndependentStat();
		}
		try {
			LAchievements.class.getDeclaredField(name).set(null, a);
		} catch (Exception e) {
			e.printStackTrace();
		}
		li.add(a);
		return a;
	}
}

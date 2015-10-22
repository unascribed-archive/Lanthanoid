package com.unascribed.lanthanoid;

import java.util.List;

import com.google.common.collect.Lists;

import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.Achievement;
import net.minecraftforge.common.AchievementPage;

public class LAchievements {
	public static Achievement craftTeleporter;
	public static Achievement selfTelefrag;
	public static Achievement telefrag;
	public static Achievement telefragMount;
	public static Achievement telesnipe;
	
	public static AchievementPage page;
	
	public static void init() {
		List<Achievement> li = Lists.newArrayList();
		li.add(craftTeleporter = new Achievement("craftTeleporter", "craftTeleporter", 0, 0, new ItemStack(LItems.teleporter, 1, 0), null).registerStat());
		
		li.add(selfTelefrag = new Achievement("selfTelefrag", "selfTelefrag", 2, 0, Blocks.stone, craftTeleporter).registerStat());
		
		li.add(telefrag = new Achievement("telefrag", "telefrag", 1, 1, LItems.dust.getStackForName("dustYttrium"), craftTeleporter).registerStat());
		li.add(telefragMount = new Achievement("telefragMount", "telefragMount", 1, 2, Items.saddle, craftTeleporter).registerStat());
		
		li.add(telesnipe = new Achievement("telesnipe", "telesnipe", 3, 1, new ItemStack(LItems.teleporter, 1, 7), telefrag).setSpecial().registerStat());
		
		page = new AchievementPage("Lanthanoid", li.toArray(new Achievement[li.size()]));
	}
}

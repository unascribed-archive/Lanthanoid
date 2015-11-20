package com.unascribed.lanthanoid.init;

import com.unascribed.lanthanoid.item.ItemDisabled;
import com.unascribed.lanthanoid.item.ItemMulti;
import com.unascribed.lanthanoid.item.ItemSpanner;
import com.unascribed.lanthanoid.item.ItemGlasses;
import com.unascribed.lanthanoid.item.ItemTeleporter;
import com.unascribed.lanthanoid.item.ItemWreckingBall;
import com.unascribed.lanthanoid.item.rifle.ItemRifle;
import com.unascribed.lanthanoid.util.LArrays;

import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.item.Item.ToolMaterial;
import net.minecraft.item.ItemArmor.ArmorMaterial;
import net.minecraftforge.common.util.EnumHelper;

public class LItems {
	public static ItemMulti ingot, stick, dust, nugget, gem;
	public static ItemTeleporter teleporter;
	public static ItemRifle rifle;
	public static ItemWreckingBall ytterbium_wrecking_ball, erbium_wrecking_ball, dysprosium_wrecking_ball;
	public static ItemGlasses glasses;
	public static ItemSpanner spanner;
	
	public static void init() {
		GameRegistry.registerItem(ingot = new ItemMulti(LArrays.all(LMaterials.metals, "ingot")), "ingot");
		GameRegistry.registerItem(stick = new ItemMulti(LArrays.all(LMaterials.metalsPlusVanilla, "stick")), "stick");
		GameRegistry.registerItem(nugget = new ItemMulti(LArrays.exclude(LArrays.all(LMaterials.metalsPlusVanilla, "nugget"), "nuggetGold")), "nugget");
		GameRegistry.registerItem(dust = new ItemMulti(LArrays.all(LMaterials.gemsAndMetalPlusVanilla, "dust")), "dust");
		GameRegistry.registerItem(gem = new ItemMulti(LArrays.all(LMaterials.gems, "gem")), "gem");
		
		teleporter = new ItemTeleporter();
		if (LConfig.item_teleporter) {
			GameRegistry.registerItem(teleporter, "teleporter");
		} else {
			GameRegistry.registerItem(new ItemDisabled().setUnlocalizedName("teleporter"), "teleporter");
		}
		
		rifle = new ItemRifle();
		if (LConfig.item_rifle) {
			GameRegistry.registerItem(rifle, "rifle");
		} else {
			GameRegistry.registerItem(new ItemDisabled().setUnlocalizedName("rifle"), "rifle");
		}
		
		ytterbium_wrecking_ball = new ItemWreckingBall(ItemWreckingBall.Material.YTTERBIUM);
		erbium_wrecking_ball = new ItemWreckingBall(ItemWreckingBall.Material.ERBIUM);
		dysprosium_wrecking_ball = new ItemWreckingBall(ItemWreckingBall.Material.DYSPROSIUM);
		if (LConfig.item_wreckingBall) {
			GameRegistry.registerItem(ytterbium_wrecking_ball, "ytterbium_wrecking_ball");
			GameRegistry.registerItem(erbium_wrecking_ball, "erbium_wrecking_ball");
			GameRegistry.registerItem(dysprosium_wrecking_ball, "dysprosium_wrecking_ball");
		} else {
			GameRegistry.registerItem(new ItemDisabled().setUnlocalizedName("ytterbium_wrecking_ball"), "ytterbium_wrecking_ball");
			GameRegistry.registerItem(new ItemDisabled().setUnlocalizedName("erbium_wrecking_ball"), "erbium_wrecking_ball");
			GameRegistry.registerItem(new ItemDisabled().setUnlocalizedName("dysprosium_wrecking_ball"), "dysprosium_wrecking_ball");
		}
		
		glasses = new ItemGlasses();
		GameRegistry.registerItem(glasses, "glasses");
		
		spanner = new ItemSpanner();
		GameRegistry.registerItem(spanner, "spanner");
		
		ToolMaterial eldritchTool = EnumHelper.addToolMaterial("ELDRITCH", 3, 1561, 9.0F, 3.0F, 24);
		ArmorMaterial eldritchArmor = EnumHelper.addArmorMaterial("ELDRITCH", 33, new int[]{3, 8, 6, 3}, 24);
	}
}

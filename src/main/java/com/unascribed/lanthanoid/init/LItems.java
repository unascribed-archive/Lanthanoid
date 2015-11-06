package com.unascribed.lanthanoid.init;

import com.unascribed.lanthanoid.item.ItemMulti;
import com.unascribed.lanthanoid.item.ItemTeleporter;
import com.unascribed.lanthanoid.item.rifle.ItemRifle;
import com.unascribed.lanthanoid.util.LArrays;

import cpw.mods.fml.common.registry.GameRegistry;

public class LItems {
	public static ItemMulti ingot, stick, dust, nugget, gem;
	public static ItemTeleporter teleporter;
	public static ItemRifle rifle;
	
	public static void init() {
		GameRegistry.registerItem(LItems.ingot = new ItemMulti(LArrays.all(LMaterials.metals, "ingot")), "ingot");
		GameRegistry.registerItem(LItems.stick = new ItemMulti(LArrays.all(LMaterials.metalsPlusVanilla, "stick")), "stick");
		GameRegistry.registerItem(LItems.nugget = new ItemMulti(LArrays.exclude(LArrays.all(LMaterials.metalsPlusVanilla, "nugget"), "nuggetGold")), "nugget");
		GameRegistry.registerItem(LItems.dust = new ItemMulti(LArrays.all(LMaterials.gemsAndMetalPlusVanilla, "dust")), "dust");
		GameRegistry.registerItem(LItems.gem = new ItemMulti(LArrays.all(LMaterials.gems, "gem")), "gem");
		
		GameRegistry.registerItem(LItems.teleporter = new ItemTeleporter(), "teleporter");
		GameRegistry.registerItem(LItems.rifle = new ItemRifle(), "rifle");
	}
}

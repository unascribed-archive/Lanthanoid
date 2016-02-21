package com.unascribed.lanthanoid.tile;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.util.IIcon;

public interface IIconProvider {
	@SideOnly(Side.CLIENT)
	IIcon getIcon(int side);
}

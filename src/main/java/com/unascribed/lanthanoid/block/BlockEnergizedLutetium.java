package com.unascribed.lanthanoid.block;

import java.util.Random;

import com.unascribed.lanthanoid.effect.LightningFX;

import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.world.World;

public class BlockEnergizedLutetium extends BlockBase {
	public BlockEnergizedLutetium() {
		super(Material.iron);
	}

	@Override
	public void randomDisplayTick(World w, int x, int y, int z, Random r) {
		double xOff = 0;
		double yOff = 0;
		double zOff = 0;
		for (int bolts = 0; bolts < 3; bolts++) {
			int setting = Minecraft.getMinecraft().gameSettings.particleSetting;
			if (setting == 2) {
				break;
			}
			if (setting == 1) {
				if (r.nextInt(2 * (setting + 1)) != 0) continue;
			}
			
			LightningFX fx = new LightningFX(w, xOff + 0.5 + x, yOff + 0.5 + y, zOff + 0.5 + z, 0.0D, 0.0D, 0.0D);

			Minecraft.getMinecraft().effectRenderer.addEffect(fx);
		}
	}
}

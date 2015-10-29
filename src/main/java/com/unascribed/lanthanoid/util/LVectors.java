package com.unascribed.lanthanoid.util;

import net.minecraft.util.MathHelper;

public class LVectors {

	public static double distance(double x1, double y1, double z1, double x2, double y2, double z2) {
		double dX = x2 - x1;
		double dY = y2 - y1;
		double dZ = z2 - z1;
		return (double) MathHelper.sqrt_double(dX * dX + dY * dY + dZ * dZ);
	}

	public static double[] interpolate(double x1, double y1, double z1, double x2, double y2, double z2, double factor) {
		return new double[] { 
				((1.0D - factor) * x1 + factor * x2),
				((1.0D - factor) * y1 + factor * y2),
				((1.0D - factor) * z1 + factor * z2)
		};
	}

}

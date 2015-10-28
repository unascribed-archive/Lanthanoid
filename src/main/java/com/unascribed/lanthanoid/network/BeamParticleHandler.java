package com.unascribed.lanthanoid.network;

import net.minecraft.client.Minecraft;
import net.minecraft.util.MathHelper;

import com.unascribed.lanthanoid.effect.EntityRifleFX;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class BeamParticleHandler implements IMessageHandler<BeamParticleMessage, IMessage> {

	@Override
	@SideOnly(Side.CLIENT)
	public IMessage onMessage(BeamParticleMessage msg, MessageContext ctx) {
		double stepSize = 0.1;
		if (Minecraft.getMinecraft().gameSettings.particleSetting == 1) {
			// Decreased
			stepSize = 1;
		} else if (Minecraft.getMinecraft().gameSettings.particleSetting == 2) {
			// Minimal
			stepSize = 2.5;
		}
		double steps = (int)(distance(msg.startX, msg.startY, msg.startZ, msg.endX, msg.endY, msg.endZ)/stepSize);
		for (int i = 0; i < steps; i++) {
			double[] end = interpolate(msg.startX, msg.startY, msg.startZ, msg.endX, msg.endY, msg.endZ, i/steps);
			EntityRifleFX fx = new EntityRifleFX(Minecraft.getMinecraft().theWorld, end[0], end[1], end[2], 1.0f, 0, 0, 0);
			fx.motionX = fx.motionY = fx.motionZ = 0;
			float r = ((msg.color >> 16)&0xFF)/255f;
			float g = ((msg.color >> 8)&0xFF)/255f;
			float b = (msg.color&0xFF)/255f;
			fx.setRBGColorF(r, g, b);
			Minecraft.getMinecraft().effectRenderer.addEffect(fx);
		}
		return null;
	}
	
	private double distance(double x1, double y1, double z1, double x2, double y2, double z2) {
		double dX = x2 - x1;
		double dY = y2 - y1;
		double dZ = z2 - z1;
		return (double) MathHelper.sqrt_double(dX * dX + dY * dY + dZ * dZ);
	}
	
	private double[] interpolate(double x1, double y1, double z1, double x2, double y2, double z2, double factor) {
		return new double[] { 
				((1.0D - factor) * x1 + factor * x2),
				((1.0D - factor) * y1 + factor * y2),
				((1.0D - factor) * z1 + factor * z2)
		};
	}

}

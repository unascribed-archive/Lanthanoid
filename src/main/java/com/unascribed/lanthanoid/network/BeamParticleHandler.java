package com.unascribed.lanthanoid.network;

import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.EntityFlameFX;

import java.util.Random;

import com.unascribed.lanthanoid.effect.EntityRifleFX;
import com.unascribed.lanthanoid.util.LVectors;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class BeamParticleHandler implements IMessageHandler<BeamParticleMessage, IMessage> {
	private Random rand = new Random((System.currentTimeMillis()*31)^(System.nanoTime()*967));
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
		float r = ((msg.color >> 16)&0xFF)/255f;
		float g = ((msg.color >> 8)&0xFF)/255f;
		float b = (msg.color&0xFF)/255f;
		double steps = (int)(LVectors.distance(msg.startX, msg.startY, msg.startZ, msg.endX, msg.endY, msg.endZ)/stepSize);
		for (int i = 0; i < steps; i++) {
			double[] end = LVectors.interpolate(msg.startX, msg.startY, msg.startZ, msg.endX, msg.endY, msg.endZ, i/steps);
			EntityRifleFX fx = new EntityRifleFX(Minecraft.getMinecraft().theWorld, end[0], end[1], end[2], 1.0f, 0, 0, 0);
			fx.motionX = fx.motionY = fx.motionZ = 0;
			fx.setRBGColorF(r, g, b);
			Minecraft.getMinecraft().effectRenderer.addEffect(fx);
			if (msg.fire) {
				EntityFlameFX fire = new EntityFlameFX(Minecraft.getMinecraft().theWorld, end[0], end[1], end[2], 0, 0, 0);
				fire.flameScale /= 2;
				Minecraft.getMinecraft().effectRenderer.addEffect(fire);
			}
		}
		if (msg.poof) {
			for (int i = 0; i < 100; i++) {
				EntityRifleFX fx = new EntityRifleFX(Minecraft.getMinecraft().theWorld, msg.endX+(rand.nextGaussian()/2), msg.endY+(rand.nextGaussian()/2), msg.endZ+(rand.nextGaussian()/2), 1.0f, 0, 0, 0);
				fx.motionX = fx.motionY = fx.motionZ = 0;
				fx.setRBGColorF(r, g, b);
				Minecraft.getMinecraft().effectRenderer.addEffect(fx);
			}
		}
		return null;
	}

}

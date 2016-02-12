package com.unascribed.lanthanoid.network;

import java.util.Random;

import com.unascribed.lanthanoid.effect.EntityRifleFX;
import com.unascribed.lanthanoid.util.LVectors;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.EntityFlameFX;

public final class BeamParticle {

	public static class Handler implements IMessageHandler<Message, IMessage> {
		private Random rand = new Random((System.currentTimeMillis()*31)^(System.nanoTime()*967));
		@Override
		@SideOnly(Side.CLIENT)
		public IMessage onMessage(Message msg, MessageContext ctx) {
			double stepSize = 0.1;
			if (Minecraft.getMinecraft().gameSettings.particleSetting == 1) {
				// Decreased
				stepSize = 1;
			} else if (Minecraft.getMinecraft().gameSettings.particleSetting == 2) {
				// Minimal
				stepSize = 2.5;
			}
			float r = ((msg.color>>16)&0xFF)/255f;
			float g = ((msg.color>>8 )&0xFF)/255f;
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
	
	public static class Message implements IMessage {
		public float startX, startY, startZ;
		public float endX, endY, endZ;
		public int color;
		public boolean fire;
		public boolean poof;
		
		public Message() {} // Required constructor for Forge
		public Message(boolean fire, boolean poof, double startX, double startY, double startZ, double endX, double endY, double endZ, int color) {
			this(fire, poof, (float)startX, (float)startY, (float)startZ, (float)endX, (float)endY, (float)endZ, color);
		}
		public Message(boolean fire, boolean poof, float startX, float startY, float startZ, float endX, float endY, float endZ, int color) {
			this.fire = fire;
			this.poof = poof;
			this.startX = startX;
			this.startY = startY;
			this.startZ = startZ;
			this.endX = endX;
			this.endY = endY;
			this.endZ = endZ;
			this.color = color;
		}

		@Override
		public void fromBytes(ByteBuf buf) {
			int flags = buf.readUnsignedByte();
			fire = (flags & 0b00000001) != 0;
			poof = (flags & 0b00000010) != 0;
			
			startX = buf.readFloat();
			startY = buf.readFloat();
			startZ = buf.readFloat();
			endX = buf.readFloat();
			endY = buf.readFloat();
			endZ = buf.readFloat();
			color = buf.readUnsignedMedium();
		}

		@Override
		public void toBytes(ByteBuf buf) {
			int flags = 0;
			if (fire) {
				flags |= 0b00000001;
			}
			if (poof) {
				flags |= 0b00000010;
			}
			buf.writeByte(flags);
			buf.writeFloat(startX);
			buf.writeFloat(startY);
			buf.writeFloat(startZ);
			buf.writeFloat(endX);
			buf.writeFloat(endY);
			buf.writeFloat(endZ);
			buf.writeMedium(color);
		}

	}
	
	private BeamParticle() {}
	
}

package com.unascribed.lanthanoid.network;

import java.util.Random;

import com.unascribed.lanthanoid.effect.EntityGlyphFX;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;

public final class SpawnGlyphParticles {

	public static class Handler implements IMessageHandler<Message, IMessage> {

		private final Random rand = new Random(System.currentTimeMillis()^System.nanoTime()^0xDEADBEEF);
		
		@Override
		@SideOnly(Side.CLIENT)
		public IMessage onMessage(Message msg, MessageContext ctx) {
			for (int i = 0; i < msg.count; i++) {
				EntityGlyphFX fx = new EntityGlyphFX(Minecraft.getMinecraft().theWorld, msg.endX, msg.endY, msg.endZ,
						(msg.startX-msg.endX)+(rand.nextGaussian()*msg.fuzzX),
						(msg.startY-msg.endY)+(rand.nextGaussian()*msg.fuzzY),
						(msg.startZ-msg.endZ)+(rand.nextGaussian()*msg.fuzzZ));
				Minecraft.getMinecraft().effectRenderer.addEffect(fx);
			}
			return null;
		}
		
	}
	
	public static class Message implements IMessage {

		public float startX;
		public float startY;
		public float startZ;
		
		public float endX;
		public float endY;
		public float endZ;
		
		public float fuzzX;
		public float fuzzY;
		public float fuzzZ;
		
		public int count;
		
		public Message() {}
		public Message(double startX, double startY, double startZ, double endX, double endY, double endZ, double fuzzX, double fuzzY, double fuzzZ, int count) {
			this((float)startX, (float)startY, (float)startZ, (float)endX, (float)endY, (float)endZ, (float)fuzzX, (float)fuzzY, (float)fuzzZ, count);
		}
		public Message(float startX, float startY, float startZ, float endX, float endY, float endZ, float fuzzX, float fuzzY, float fuzzZ, int count) {
			this.startX = startX;
			this.startY = startY;
			this.startZ = startZ;
			this.endX = endX;
			this.endY = endY;
			this.endZ = endZ;
			this.fuzzX = fuzzX;
			this.fuzzY = fuzzY;
			this.fuzzZ = fuzzZ;
			this.count = count;
		}
		
		@Override
		public void fromBytes(ByteBuf buf) {
			startX = buf.readFloat();
			startY = buf.readFloat();
			startZ = buf.readFloat();
			endX = buf.readFloat();
			endY = buf.readFloat();
			endZ = buf.readFloat();
			fuzzX = buf.readFloat();
			fuzzY = buf.readFloat();
			fuzzZ = buf.readFloat();
			count = buf.readUnsignedShort();
		}

		@Override
		public void toBytes(ByteBuf buf) {
			buf.writeFloat(startX);
			buf.writeFloat(startY);
			buf.writeFloat(startZ);
			buf.writeFloat(endX);
			buf.writeFloat(endY);
			buf.writeFloat(endZ);
			buf.writeFloat(fuzzX);
			buf.writeFloat(fuzzY);
			buf.writeFloat(fuzzZ);
			buf.writeShort(count);
		}

	}
	
	private SpawnGlyphParticles() {}
}

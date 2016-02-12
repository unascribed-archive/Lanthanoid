package com.unascribed.lanthanoid.network;

import java.util.Random;

import com.unascribed.lanthanoid.effect.LightningFX;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.util.ResourceLocation;

public final class BootZap {

	public static class Handler implements IMessageHandler<Message, IMessage> {

		private Random r = new Random(System.currentTimeMillis()^System.nanoTime()^0xCAFEF00D);
		
		@Override
		@SideOnly(Side.CLIENT)
		public IMessage onMessage(Message message, MessageContext ctx) {
			Minecraft.getMinecraft().getSoundHandler().playSound(new PositionedSoundRecord(new ResourceLocation("lanthanoid", "spark"),
					message.magnitude, 1.0f+(r.nextFloat()/4),
					message.x+0.5f, message.y+0.5f, message.z+0.5f));
			for (int i = 0; i < Math.ceil(message.magnitude*20); i++) {
				LightningFX fx = new LightningFX(Minecraft.getMinecraft().theWorld, message.x+0.5, message.y+0.5, message.z+0.5, 64/255D, 255/255D, 128/255D);
				Minecraft.getMinecraft().effectRenderer.addEffect(fx);
			}
			return null;
		}
		
	}
	
	public static class Message implements IMessage {

		public Message() {}
		public Message(double x, double y, double z, float magnitude) {
			this((float)x, (float)y, (float)z, magnitude);
		}
		public Message(float x, float y, float z, float magnitude) {
			this.x = x;
			this.y = y;
			this.z = z;
			this.magnitude = magnitude;
		}
		
		public float x;
		public float y;
		public float z;
		public float magnitude;
		
		@Override
		public void fromBytes(ByteBuf buf) {
			x = buf.readFloat();
			y = buf.readFloat();
			z = buf.readFloat();
			magnitude = buf.readFloat();
		}

		@Override
		public void toBytes(ByteBuf buf) {
			buf.writeFloat(x);
			buf.writeFloat(y);
			buf.writeFloat(z);
			buf.writeFloat(magnitude);
		}
		
	}
	
	private BootZap() {}
}

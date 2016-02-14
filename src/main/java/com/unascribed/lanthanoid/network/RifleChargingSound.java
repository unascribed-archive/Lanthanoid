package com.unascribed.lanthanoid.network;

import com.unascribed.lanthanoid.client.sound.MovingSoundEntity;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;

public class RifleChargingSound {
	
	public static class Handler implements IMessageHandler<Message, IMessage> {

		@Override
		@SideOnly(Side.CLIENT)
		public IMessage onMessage(Message message, MessageContext ctx) {
			Entity ent = Minecraft.getMinecraft().theWorld.getEntityByID(message.entity);
			if (message.start) {
				MovingSoundEntity sound = new MovingSoundEntity(new ResourceLocation("lanthanoid", "rifle_charge"), ent, message.speed);
				Minecraft.getMinecraft().getSoundHandler().playSound(sound);
			} else {
				MovingSoundEntity sound = MovingSoundEntity.get(ent);
				if (sound != null) {
					sound.stop();
				}
			}
			return null;
		}

	}
	
	public static class Message implements IMessage {
		public boolean start;
		public float speed;
		public int entity;
		
		public Message() {}
		public Message(int entity, float speed, boolean start) {
			this.start = start;
			this.speed = speed;
			this.entity = entity;
		}
		
		@Override
		public void fromBytes(ByteBuf buf) {
			int flags = buf.readUnsignedByte();
			start = (flags & 0b00000001) != 0;
			
			speed = ((buf.readUnsignedByte()/255f)/(2f/3f))+0.5f;
			entity = buf.readInt();
		}

		@Override
		public void toBytes(ByteBuf buf) {
			int flags = 0;
			if (start) {
				flags |= 0b00000001;
			}
			buf.writeByte(flags);
			
			buf.writeByte((int)(((speed-0.5f)*(2f/3f))*255));
			buf.writeInt(entity);
		}

	}
	
	private RifleChargingSound() {}
	
}

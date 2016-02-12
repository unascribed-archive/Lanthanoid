package com.unascribed.lanthanoid.network;

import com.unascribed.lanthanoid.client.SoundEldritchBootNoise;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.ResourceLocation;

public final class BootNoise {

	public static class Handler implements IMessageHandler<Message, IMessage> {

		@Override
		@SideOnly(Side.CLIENT)
		public IMessage onMessage(Message message, MessageContext ctx) {
			Entity ent = Minecraft.getMinecraft().theWorld.getEntityByID(message.entityId);
			if (ent instanceof EntityLivingBase) {
				SoundEldritchBootNoise snd = new SoundEldritchBootNoise(new ResourceLocation("lanthanoid", "whoosh"), (EntityLivingBase)ent);
				Minecraft.getMinecraft().getSoundHandler().playSound(snd);
			}
			return null;
		}
		
	}
	
	public static class Message implements IMessage {

		public int entityId;
		
		public Message() {}
		public Message(int entityId) {
			this.entityId = entityId;
		}
		
		@Override
		public void fromBytes(ByteBuf buf) {
			entityId = buf.readInt();
		}

		@Override
		public void toBytes(ByteBuf buf) {
			buf.writeInt(entityId);
		}

	}
	
	private BootNoise() {}
}

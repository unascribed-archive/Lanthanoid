package com.unascribed.lanthanoid.network;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;

public final class BlockEvent {

	public static class Handler implements IMessageHandler<Message, IMessage> {

		@Override
		@SideOnly(Side.CLIENT)
		public IMessage onMessage(Message message, MessageContext ctx) {
			Minecraft.getMinecraft().theWorld.addBlockEvent(message.x, message.y, message.z, Minecraft.getMinecraft().theWorld.getBlock(message.x, message.y, message.z), message.event, message.arg);
			return null;
		}

	}
	
	public static class Message implements IMessage {
		public int x, y, z;
		public int event;
		public int arg;
		
		
		@Override
		public void fromBytes(ByteBuf buf) {
			this.x = buf.readInt();
			this.y = buf.readUnsignedByte();
			this.z = buf.readInt();
			this.event = buf.readUnsignedByte();
			this.arg = buf.readInt();
		}

		@Override
		public void toBytes(ByteBuf buf) {
			buf.writeInt(x);
			buf.writeByte(y);
			buf.writeInt(z);
			buf.writeByte(event);
			buf.writeInt(arg);
		}

	}
	
	private BlockEvent() {}
}

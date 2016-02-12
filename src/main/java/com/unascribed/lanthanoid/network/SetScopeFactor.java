package com.unascribed.lanthanoid.network;

import com.unascribed.lanthanoid.client.LClientEventHandler;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;

public final class SetScopeFactor {

	public static class Handler implements IMessageHandler<Message, IMessage> {

		@Override
		public IMessage onMessage(Message message, MessageContext ctx) {
			LClientEventHandler.scopeFactor = message.factor;
			return null;
		}

	}
	
	public static class Message implements IMessage {
		public int factor;
		public Message() {}
		public Message(int factor) {
			this.factor = factor;
		}
		
		@Override
		public void fromBytes(ByteBuf buf) {
			factor = buf.readUnsignedByte();
		}
		
		@Override
		public void toBytes(ByteBuf buf) {
			buf.writeByte(factor);
		}
	}
	
	private SetScopeFactor() {}
}

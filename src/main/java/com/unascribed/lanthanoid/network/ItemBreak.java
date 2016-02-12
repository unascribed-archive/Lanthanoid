package com.unascribed.lanthanoid.network;

import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;

public final class ItemBreak {

	public static class Handler implements IMessageHandler<Message, IMessage> {

		@SideOnly(Side.CLIENT)
		@Override
		public IMessage onMessage(Message message, MessageContext ctx) {
			Entity e = Minecraft.getMinecraft().theWorld.getEntityByID(message.entity);
			if (e instanceof EntityLivingBase) {
				((EntityLivingBase) e).renderBrokenItemStack(message.stack);
			}
			return null;
		}

	}
	
	public static class Message implements IMessage {
		public int entity;
		public ItemStack stack;
		public Message() {}
		public Message(int entity, ItemStack stack) {
			this.entity = entity;
			this.stack = stack;
		}
		
		
		@Override
		public void fromBytes(ByteBuf buf) {
			entity = buf.readInt();
			stack = ByteBufUtils.readItemStack(buf);
		}

		@Override
		public void toBytes(ByteBuf buf) {
			buf.writeInt(entity);
			ByteBufUtils.writeItemStack(buf, stack);
		}

	}
	
	private ItemBreak() {}
}

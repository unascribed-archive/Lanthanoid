package com.unascribed.lanthanoid.network;

import com.unascribed.lanthanoid.init.LItems;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;

public final class ToggleRifleBlazeMode {

	public static class Handler implements IMessageHandler<Message, IMessage> {

		@Override
		public IMessage onMessage(Message message, MessageContext ctx) {
			EntityPlayerMP player = ctx.getServerHandler().playerEntity;
			if (player.getHeldItem() != null) {
				if (player.getHeldItem().getItem() == LItems.rifle) {
					LItems.rifle.setBlazeEnabled(player.getHeldItem(), !LItems.rifle.isBlazeEnabled(player.getHeldItem()));
					player.worldObj.playSoundAtEntity(player, "lanthanoid:rifle_mode", 1.0f, LItems.rifle.isBlazeEnabled(player.getHeldItem()) ? 1.5f : 1.0f);
				}
			}
			return null;
		}

	}
	
	public static class Message implements IMessage {
		@Override public void fromBytes(ByteBuf buf) {}
		@Override public void toBytes(ByteBuf buf) {}
	}

	
	private ToggleRifleBlazeMode() {}
}

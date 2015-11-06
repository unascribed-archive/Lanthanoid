package com.unascribed.lanthanoid.network;

import net.minecraft.entity.player.EntityPlayerMP;

import com.unascribed.lanthanoid.init.LItems;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;

public class ToggleRifleBlazeModeHandler implements IMessageHandler<ToggleRifleBlazeModeMessage, IMessage> {

	@Override
	public IMessage onMessage(ToggleRifleBlazeModeMessage message, MessageContext ctx) {
		EntityPlayerMP player = ctx.getServerHandler().playerEntity;
		if (player.getHeldItem() != null) {
			if (player.getHeldItem().getItem() == LItems.rifle) {
				LItems.rifle.setBlazeEnabled(player.getHeldItem(), !LItems.rifle.isBlazeEnabled(player.getHeldItem()));
				System.out.println(LItems.rifle.isBlazeEnabled(player.getHeldItem()));
			}
		}
		return null;
	}

}

package com.unascribed.lanthanoid.network;

import net.minecraft.entity.player.EntityPlayerMP;

import com.unascribed.lanthanoid.Lanthanoid;
import com.unascribed.lanthanoid.init.LItems;
import com.unascribed.lanthanoid.item.rifle.PrimaryMode;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;

public class ModifyRifleModeHandler implements IMessageHandler<ModifyRifleModeMessage, IMessage> {

	@Override
	public IMessage onMessage(ModifyRifleModeMessage message, MessageContext ctx) {
		EntityPlayerMP player = ctx.getServerHandler().playerEntity;
		if (player.getHeldItem() != null) {
			if (player.getHeldItem().getItem() == LItems.rifle) {
				int value = message.value;
				if (message.set) {
					if (value < 0) {
						Lanthanoid.log.warn(player.getCommandSenderName()+" sent an illegal value for ModifyRifleMode (message is absolute and value is less than zero), trying to crash the server?");
						value = 0;
					}
					if (value >= PrimaryMode.values().length) {
						Lanthanoid.log.warn(player.getCommandSenderName()+" sent an illegal value for ModifyRifleMode (message is absolute and value is greater than the limit), trying to crash the server?");
						value = PrimaryMode.values().length-1;
					}
				} else {
					if ((Math.abs(value) != 1 || value == 0)) {
						Lanthanoid.log.warn(player.getCommandSenderName()+" sent an illegal value for ModifyRifleMode (message is relative but absolute value is not 1), trying to crash the server?");
						if (value < 0) value = -1;
						if (value > 0) value = 1;
						if (value == 0) return null;
					}
				}
				LItems.rifle.modifyMode(player, player.getHeldItem(), message.set, value*(message.primary?1:-1), message.primary);
			}
		}
		return null;
	}

}

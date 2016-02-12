package com.unascribed.lanthanoid.network;

import com.unascribed.lanthanoid.Lanthanoid;
import com.unascribed.lanthanoid.init.LItems;
import com.unascribed.lanthanoid.item.rifle.Mode;
import com.unascribed.lanthanoid.item.rifle.PrimaryMode;
import com.unascribed.lanthanoid.item.rifle.SecondaryMode;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;

public final class ModifyRifleMode {

	public static class Handler implements IMessageHandler<Message, IMessage> {

		@Override
		public IMessage onMessage(Message message, MessageContext ctx) {
			EntityPlayerMP player = ctx.getServerHandler().playerEntity;
			if (player.getHeldItem() != null) {
				if (player.getHeldItem().getItem() == LItems.rifle) {
					int value = message.value;
					Mode[] vals = (message.primary ? PrimaryMode.values() : SecondaryMode.values());
					if (message.set) {
						if (value < 0) {
							Lanthanoid.log.warn(player.getCommandSenderName()+" sent an illegal value for ModifyRifleMode (message is absolute and value is less than zero), trying to crash the server?");
							value = 0;
						}
						if (value >= vals.length) {
							Lanthanoid.log.warn(player.getCommandSenderName()+" sent an illegal value for ModifyRifleMode (message is absolute and value is greater than the limit), trying to crash the server?");
							value = vals.length-1;
						}
					} else {
						if ((Math.abs(value) != 1 || value == 0)) {
							Lanthanoid.log.warn(player.getCommandSenderName()+" sent an illegal value for ModifyRifleMode (message is relative but absolute value is not 1), trying to crash the server?");
							if (value < 0) {
								value = -1;
							}
							if (value > 0) {
								value = 1;
							}
							if (value == 0) {
								return null;
							}
						}
					}
					LItems.rifle.modifyMode(player, player.getHeldItem(), message.set, value*(message.primary?1:-1), message.primary);
				}
			}
			return null;
		}

	}
	
	public static class Message implements IMessage {
		public boolean set;
		public boolean primary;
		public int value;
		public Message() {} // Required constructor for Forge
		public Message(boolean set, boolean primary, int value) {
			this.set = set;
			this.primary = primary;
			this.value = value;
		}
		
		@Override
		public void fromBytes(ByteBuf buf) {
			int flags = buf.readUnsignedByte();
			set = (flags & 0b00000001) != 0;
			primary = (flags & 0b00000010) != 0;
			value = buf.readByte();
		}

		@Override
		public void toBytes(ByteBuf buf) {
			int flags = 0;
			if (set) {
				flags |= 0b00000001;
			}
			if (primary) {
				flags |= 0b00000010;
			}
			buf.writeByte(flags);
			buf.writeByte(value);
		}

	}
	
	private ModifyRifleMode() {}
}

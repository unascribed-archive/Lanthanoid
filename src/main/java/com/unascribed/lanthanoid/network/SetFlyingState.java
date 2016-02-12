package com.unascribed.lanthanoid.network;

import com.unascribed.lanthanoid.LanthanoidProperties;
import com.unascribed.lanthanoid.item.eldritch.armor.ItemEldritchArmor;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;

public final class SetFlyingState {

	public enum State {
		NONE,
		HOVER,
		FLYING
	}
	
	public static class Handler implements IMessageHandler<Message, IMessage> {

		@Override
		public IMessage onMessage(Message message, MessageContext ctx) {
			EntityPlayerMP player = ctx.getServerHandler().playerEntity;
			State state = message.state;
			if (ItemEldritchArmor.hasSetBonus(player)) {
				int totalGlyphs = 0;
				for (ItemStack is : player.inventory.armorInventory) {
					totalGlyphs += ((ItemEldritchArmor)is.getItem()).getMilliglyphs(is);
				}
				if (totalGlyphs < 1000) {
					if (state != State.NONE) {
						state = State.NONE;
					}
				}
			} else {
				if (state != State.NONE) {
					state = State.NONE;
				}
			}
			LanthanoidProperties props = (LanthanoidProperties) player.getExtendedProperties("lanthanoid");
			props.flyingState = state;
			return null;
		}
		
	}
	
	public static class Message implements IMessage {
		public State state;
		
		public Message() {}
		public Message(State state) {
			this.state = state;
		}
		
		@Override
		public void fromBytes(ByteBuf buf) {
			state = State.values()[buf.readUnsignedByte()%State.values().length];
		}

		@Override
		public void toBytes(ByteBuf buf) {
			buf.writeByte(state.ordinal());
		}
		
	}
	
	private SetFlyingState() {}
}

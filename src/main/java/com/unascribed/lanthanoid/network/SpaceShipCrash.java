package com.unascribed.lanthanoid.network;

import com.unascribed.lanthanoid.client.LClientEventHandler;
import com.unascribed.lanthanoid.client.LClientEventHandler.SkyFlash;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Vec3;

public final class SpaceShipCrash {
	public enum Type {
		CERIUM(208, 0, 62),
		DYSPROSIUM(134, 0, 150);
		public final Vec3 color;
		Type(int r, int g, int b) {
			color = Vec3.createVectorHelper(r/255D, g/255D, b/255D);
		}
	}
	
	public static class Handler implements IMessageHandler<Message, IMessage> {

		@Override
		@SideOnly(Side.CLIENT)
		public IMessage onMessage(Message message, MessageContext ctx) {
			PositionedSoundRecord s = PositionedSoundRecord.createPositionedSoundRecord(new ResourceLocation("lanthanoid", "spaceship_crash"));
			Minecraft.getMinecraft().getSoundHandler().playSound(s);
			LClientEventHandler.flashes.add(new SkyFlash(message.type.color));
			return null;
		}

	}
	
	public static class Message implements IMessage {
		public Type type;
		
		public Message() {}
		public Message(Type type) {
			this.type = type;
		}
		@Override
		public void fromBytes(ByteBuf buf) {
			type = Type.values()[buf.readUnsignedByte()%Type.values().length];
		}
		@Override
		public void toBytes(ByteBuf buf) {
			buf.writeByte(type.ordinal());
		}
	}
	
	
	private SpaceShipCrash() {}
}

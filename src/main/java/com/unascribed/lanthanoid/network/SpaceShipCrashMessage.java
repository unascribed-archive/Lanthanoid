package com.unascribed.lanthanoid.network;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import io.netty.buffer.ByteBuf;
import net.minecraft.util.Vec3;

public class SpaceShipCrashMessage implements IMessage {
	public enum Type {
		CERIUM(208, 0, 62),
		DYSPROSIUM(134, 0, 150);
		public final Vec3 color;
		Type(int r, int g, int b) {
			color = Vec3.createVectorHelper(r/255D, g/255D, b/255D);
		}
	}
	
	public Type type;
	
	public SpaceShipCrashMessage() {}
	public SpaceShipCrashMessage(Type type) {
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

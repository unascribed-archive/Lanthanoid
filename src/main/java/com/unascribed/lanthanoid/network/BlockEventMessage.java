package com.unascribed.lanthanoid.network;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import io.netty.buffer.ByteBuf;

public class BlockEventMessage implements IMessage {
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

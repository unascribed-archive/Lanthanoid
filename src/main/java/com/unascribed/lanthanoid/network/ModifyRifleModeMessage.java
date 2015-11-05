package com.unascribed.lanthanoid.network;

import io.netty.buffer.ByteBuf;
import cpw.mods.fml.common.network.simpleimpl.IMessage;

public class ModifyRifleModeMessage implements IMessage {
	public boolean set;
	public boolean primary;
	public int value;
	public ModifyRifleModeMessage() {} // Required constructor for Forge
	public ModifyRifleModeMessage(boolean set, boolean primary, int value) {
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

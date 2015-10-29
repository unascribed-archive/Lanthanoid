package com.unascribed.lanthanoid.network;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import io.netty.buffer.ByteBuf;

public class SetScopeFactorMessage implements IMessage {
	public int factor;
	public SetScopeFactorMessage() {}
	public SetScopeFactorMessage(int factor) {
		this.factor = factor;
	}
	
	@Override
	public void fromBytes(ByteBuf buf) {
		factor = buf.readUnsignedByte();
	}
	
	@Override
	public void toBytes(ByteBuf buf) {
		buf.writeByte(factor);
	}
}

package com.unascribed.lanthanoid.network;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import io.netty.buffer.ByteBuf;

public class RifleChargingSoundRequest implements IMessage {
	public boolean start;
	public int entity;
	
	public RifleChargingSoundRequest() {}
	public RifleChargingSoundRequest(int entity, boolean start) {
		this.start = start;
		this.entity = entity;
	}
	
	@Override
	public void fromBytes(ByteBuf buf) {
		start = buf.readBoolean();
		entity = buf.readInt();
	}

	@Override
	public void toBytes(ByteBuf buf) {
		buf.writeBoolean(start);
		buf.writeInt(entity);
	}

}

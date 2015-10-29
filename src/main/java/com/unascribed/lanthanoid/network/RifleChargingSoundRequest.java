package com.unascribed.lanthanoid.network;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import io.netty.buffer.ByteBuf;

public class RifleChargingSoundRequest implements IMessage {
	public boolean start;
	public float speed;
	public int entity;
	
	public RifleChargingSoundRequest() {}
	public RifleChargingSoundRequest(int entity, float speed, boolean start) {
		this.start = start;
		this.speed = speed;
		this.entity = entity;
	}
	
	@Override
	public void fromBytes(ByteBuf buf) {
		int flags = buf.readUnsignedByte();
		start = (flags & 0b00000001) != 0;
		
		speed = ((buf.readUnsignedByte()/255f)/(2f/3f))+0.5f;
		entity = buf.readInt();
	}

	@Override
	public void toBytes(ByteBuf buf) {
		int flags = 0;
		if (start) {
			flags |= 0b00000001;
		}
		buf.writeByte(flags);
		
		buf.writeByte((int)(((speed-0.5f)*(2f/3f))*255));
		buf.writeInt(entity);
	}

}

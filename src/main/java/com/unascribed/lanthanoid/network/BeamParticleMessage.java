package com.unascribed.lanthanoid.network;

import io.netty.buffer.ByteBuf;
import cpw.mods.fml.common.network.simpleimpl.IMessage;

public class BeamParticleMessage implements IMessage {
	public float startX, startY, startZ;
	public float endX, endY, endZ;
	public int color;
	public boolean fire;
	public boolean poof;
	
	public BeamParticleMessage() {} // Required constructor for Forge
	public BeamParticleMessage(boolean fire, boolean poof, double startX, double startY, double startZ, double endX, double endY, double endZ, int color) {
		this(fire, poof, (float)startX, (float)startY, (float)startZ, (float)endX, (float)endY, (float)endZ, color);
	}
	public BeamParticleMessage(boolean fire, boolean poof, float startX, float startY, float startZ, float endX, float endY, float endZ, int color) {
		this.fire = fire;
		this.poof = poof;
		this.startX = startX;
		this.startY = startY;
		this.startZ = startZ;
		this.endX = endX;
		this.endY = endY;
		this.endZ = endZ;
		this.color = color;
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		int flags = buf.readUnsignedByte();
		fire = (flags & 0b00000001) != 0;
		poof = (flags & 0b00000010) != 0;
		
		startX = buf.readFloat();
		startY = buf.readFloat();
		startZ = buf.readFloat();
		endX = buf.readFloat();
		endY = buf.readFloat();
		endZ = buf.readFloat();
		color = buf.readUnsignedMedium();
	}

	@Override
	public void toBytes(ByteBuf buf) {
		int flags = 0;
		if (fire) {
			flags |= 0b00000001;
		}
		if (poof) {
			flags |= 0b00000010;
		}
		buf.writeByte(flags);
		buf.writeFloat(startX);
		buf.writeFloat(startY);
		buf.writeFloat(startZ);
		buf.writeFloat(endX);
		buf.writeFloat(endY);
		buf.writeFloat(endZ);
		buf.writeMedium(color);
	}

}

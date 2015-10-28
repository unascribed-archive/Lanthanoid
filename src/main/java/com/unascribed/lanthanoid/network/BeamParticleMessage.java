package com.unascribed.lanthanoid.network;

import io.netty.buffer.ByteBuf;
import cpw.mods.fml.common.network.simpleimpl.IMessage;

public class BeamParticleMessage implements IMessage {
	public float startX, startY, startZ;
	public float endX, endY, endZ;
	public int color;
	
	public BeamParticleMessage() {} // Required constructor for Forge
	public BeamParticleMessage(double startX, double startY, double startZ, double endX, double endY, double endZ, int color) {
		this((float)startX, (float)startY, (float)startZ, (float)endX, (float)endY, (float)endZ, color);
	}
	public BeamParticleMessage(float startX, float startY, float startZ, float endX, float endY, float endZ, int color) {
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
		buf.writeFloat(startX);
		buf.writeFloat(startY);
		buf.writeFloat(startZ);
		buf.writeFloat(endX);
		buf.writeFloat(endY);
		buf.writeFloat(endZ);
		buf.writeMedium(color);
	}

}

package com.unascribed.lanthanoid.waypoint;

import java.util.UUID;

import com.google.common.base.Strings;

import cpw.mods.fml.common.network.ByteBufUtils;
import io.netty.buffer.ByteBuf;
import net.minecraft.nbt.NBTTagCompound;

public class Waypoint {
	public enum Type {
		PERSONAL,
		GLOBAL
	}
	private static int nextId;
	
	public int id;
	public int x, y, z;
	public int color;
	public String name;
	public String ownerName;
	public UUID owner;
	public Type type;
	
	
	public void setId() {
		id = nextId++;
	}
	
	@Override
	public String toString() {
		return "Waypoint{x=" + x + ", y=" + y + ", z=" + z + ", color=" + color + ", name=" + name + ", ownerName=" + ownerName + ", type=" + type + ", owner=" + owner + "}";
	}

	
	
	public void fromBytes(ByteBuf buf) {
		id = buf.readInt();
		x = buf.readInt();
		y = buf.readUnsignedByte();
		z = buf.readInt();
		color = buf.readUnsignedMedium();
		name = Strings.emptyToNull(ByteBufUtils.readUTF8String(buf));
		ownerName = ByteBufUtils.readUTF8String(buf);
		Type[] vals = Type.values();
		type = vals[buf.readUnsignedByte()%vals.length];
		owner = new UUID(buf.readLong(), buf.readLong());
	}
	
	public void toBytes(ByteBuf buf) {
		buf.writeInt(id);
		buf.writeInt(x);
		buf.writeByte(y);
		buf.writeInt(z);
		buf.writeMedium(color);
		ByteBufUtils.writeUTF8String(buf, Strings.nullToEmpty(name));
		ByteBufUtils.writeUTF8String(buf, ownerName);
		buf.writeByte(type.ordinal());
		buf.writeLong(owner.getMostSignificantBits());
		buf.writeLong(owner.getLeastSignificantBits());
	}

	public void writeToNBT(NBTTagCompound nbt) {
		nbt.setIntArray("Position", new int[] { x, y, z });
		nbt.setInteger("Color", color);
		nbt.setString("OwnerName", ownerName);
		nbt.setString("Name", name);
		nbt.setLong("OwnerUUIDMost", owner.getMostSignificantBits());
		nbt.setLong("OwnerUUIDLeast", owner.getLeastSignificantBits());
		nbt.setByte("Type", (byte)type.ordinal());
	}
	
	public void readFromNBT(NBTTagCompound nbt) {
		int[] pos = nbt.getIntArray("Position");
		x = pos[0];
		y = pos[1];
		z = pos[2];
		color = nbt.getInteger("Color");
		ownerName = nbt.getString("OwnerName");
		name = nbt.getString("Name");
		owner = new UUID(nbt.getLong("OwnerUUIDMost"), nbt.getLong("OwnerUUIDLeast"));
		Type[] vals = Type.values();
		type = vals[nbt.getByte("Type") % vals.length];
	}
}

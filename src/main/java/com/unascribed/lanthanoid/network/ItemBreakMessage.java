package com.unascribed.lanthanoid.network;

import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import io.netty.buffer.ByteBuf;
import net.minecraft.item.ItemStack;

public class ItemBreakMessage implements IMessage {
	public int entity;
	public ItemStack stack;
	public ItemBreakMessage() {}
	public ItemBreakMessage(int entity, ItemStack stack) {
		this.entity = entity;
		this.stack = stack;
	}
	
	
	@Override
	public void fromBytes(ByteBuf buf) {
		entity = buf.readInt();
		stack = ByteBufUtils.readItemStack(buf);
	}

	@Override
	public void toBytes(ByteBuf buf) {
		buf.writeInt(entity);
		ByteBufUtils.writeItemStack(buf, stack);
	}

}

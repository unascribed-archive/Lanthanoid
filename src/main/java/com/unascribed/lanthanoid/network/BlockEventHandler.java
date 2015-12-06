package com.unascribed.lanthanoid.network;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;

public class BlockEventHandler implements IMessageHandler<BlockEventMessage, IMessage> {

	@Override
	@SideOnly(Side.CLIENT)
	public IMessage onMessage(BlockEventMessage message, MessageContext ctx) {
		Minecraft.getMinecraft().theWorld.addBlockEvent(message.x, message.y, message.z, Minecraft.getMinecraft().theWorld.getBlock(message.x, message.y, message.z), message.event, message.arg);
		return null;
	}

}

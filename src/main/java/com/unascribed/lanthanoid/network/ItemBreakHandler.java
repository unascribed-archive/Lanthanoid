package com.unascribed.lanthanoid.network;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;

public class ItemBreakHandler implements IMessageHandler<ItemBreakMessage, IMessage> {

	@SideOnly(Side.CLIENT)
	@Override
	public IMessage onMessage(ItemBreakMessage message, MessageContext ctx) {
		Entity e = Minecraft.getMinecraft().theWorld.getEntityByID(message.entity);
		if (e instanceof EntityLivingBase) {
			((EntityLivingBase) e).renderBrokenItemStack(message.stack);
		}
		return null;
	}

}

package com.unascribed.lanthanoid.network;

import com.unascribed.lanthanoid.client.MovingSoundEntity;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;

public class RifleChargingSoundHandler implements IMessageHandler<RifleChargingSoundRequest, IMessage> {

	@Override
	@SideOnly(Side.CLIENT)
	public IMessage onMessage(RifleChargingSoundRequest message, MessageContext ctx) {
		Entity ent = Minecraft.getMinecraft().theWorld.getEntityByID(message.entity);
		if (message.start) {
			MovingSoundEntity sound = new MovingSoundEntity(new ResourceLocation("lanthanoid", "rifle_charge"), ent, message.speed);
			Minecraft.getMinecraft().getSoundHandler().playSound(sound);
		} else {
			MovingSoundEntity.get(ent).stop();
		}
		return null;
	}

}

package com.unascribed.lanthanoid.network;

import com.unascribed.lanthanoid.LClientEventHandler;
import com.unascribed.lanthanoid.LClientEventHandler.SkyFlash;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.util.ResourceLocation;

public class SpaceShipCrashHandler implements IMessageHandler<SpaceShipCrashMessage, IMessage> {

	@Override
	public IMessage onMessage(SpaceShipCrashMessage message, MessageContext ctx) {
		PositionedSoundRecord s = PositionedSoundRecord.func_147673_a(new ResourceLocation("lanthanoid", "spaceship_crash"));
		Minecraft.getMinecraft().getSoundHandler().playSound(s);
		LClientEventHandler.flashes.add(new SkyFlash(message.type.color));
		return null;
	}

}

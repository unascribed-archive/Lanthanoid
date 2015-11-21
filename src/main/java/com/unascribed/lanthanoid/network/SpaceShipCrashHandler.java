package com.unascribed.lanthanoid.network;

import com.unascribed.lanthanoid.client.LClientEventHandler;
import com.unascribed.lanthanoid.client.LClientEventHandler.SkyFlash;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.util.ResourceLocation;

public class SpaceShipCrashHandler implements IMessageHandler<SpaceShipCrashMessage, IMessage> {

	@Override
	@SideOnly(Side.CLIENT)
	public IMessage onMessage(SpaceShipCrashMessage message, MessageContext ctx) {
		PositionedSoundRecord s = PositionedSoundRecord.createPositionedSoundRecord(new ResourceLocation("lanthanoid", "spaceship_crash"));
		Minecraft.getMinecraft().getSoundHandler().playSound(s);
		LClientEventHandler.flashes.add(new SkyFlash(message.type.color));
		return null;
	}

}

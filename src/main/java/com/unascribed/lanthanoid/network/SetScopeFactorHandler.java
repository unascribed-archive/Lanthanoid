package com.unascribed.lanthanoid.network;

import java.util.List;

import com.unascribed.lanthanoid.LClientEventHandler;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityClientPlayerMP;
import net.minecraft.entity.Entity;

public class SetScopeFactorHandler implements IMessageHandler<SetScopeFactorMessage, IMessage> {

	@Override
	public IMessage onMessage(SetScopeFactorMessage message, MessageContext ctx) {
		LClientEventHandler.scopeFactor = message.factor;
		/*EntityClientPlayerMP player = Minecraft.getMinecraft().thePlayer;
		for (Entity e : (List<Entity>)Minecraft.getMinecraft().theWorld.loadedEntityList) {
			if (message.factor > 1) {
				if (e.isInRangeToRender3d(player.posX, player.posY, player.posZ)) {
					e.ignoreFrustumCheck = true;
				} else {
					e.ignoreFrustumCheck = false;
				}
			} else {
				e.ignoreFrustumCheck = false;
			}
		}*/
		return null;
	}

}

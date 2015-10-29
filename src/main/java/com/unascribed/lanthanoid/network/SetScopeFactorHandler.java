package com.unascribed.lanthanoid.network;

import com.unascribed.lanthanoid.LClientEventHandler;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;

public class SetScopeFactorHandler implements IMessageHandler<SetScopeFactorMessage, IMessage> {

	@Override
	public IMessage onMessage(SetScopeFactorMessage message, MessageContext ctx) {
		LClientEventHandler.scopeFactor = message.factor;
		return null;
	}

}

package com.unascribed.lanthanoid.init;

import com.google.common.base.Throwables;
import com.unascribed.lanthanoid.Lanthanoid;
import com.unascribed.lanthanoid.network.BeamParticle;
import com.unascribed.lanthanoid.network.BlockEvent;
import com.unascribed.lanthanoid.network.BootNoise;
import com.unascribed.lanthanoid.network.BootZap;
import com.unascribed.lanthanoid.network.ItemBreak;
import com.unascribed.lanthanoid.network.ModifyRifleMode;
import com.unascribed.lanthanoid.network.ModifyWaypointList;
import com.unascribed.lanthanoid.network.RifleChargingSound;
import com.unascribed.lanthanoid.network.SetFlyingState;
import com.unascribed.lanthanoid.network.SetScopeFactor;
import com.unascribed.lanthanoid.network.SpaceShipCrash;
import com.unascribed.lanthanoid.network.SpawnGlyphParticles;
import com.unascribed.lanthanoid.network.ToggleRifleBlazeMode;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import cpw.mods.fml.relauncher.Side;

public class LNetwork {
	private static int discriminator = 0;
	public static void init() {
		SimpleNetworkWrapper network = new SimpleNetworkWrapper("Lanthanoid");
		Lanthanoid.inst.network = network;
		registerMessage(Side.CLIENT, RifleChargingSound.class);
		registerMessage(Side.SERVER, ModifyRifleMode.class);
		registerMessage(Side.CLIENT, BeamParticle.class);
		registerMessage(Side.CLIENT, SetScopeFactor.class);
		registerMessage(Side.CLIENT, SpaceShipCrash.class);
		registerMessage(Side.SERVER, ToggleRifleBlazeMode.class);
		registerMessage(Side.CLIENT, ModifyWaypointList.class);
		registerMessage(Side.CLIENT, ItemBreak.class);
		registerMessage(Side.CLIENT, BlockEvent.class);
		registerMessage(Side.CLIENT, BootNoise.class);
		registerMessage(Side.CLIENT, SpawnGlyphParticles.class);
		registerMessage(Side.CLIENT, BootZap.class);
		registerMessage(Side.SERVER, SetFlyingState.class);
	}

	private static <REQ extends IMessage> void registerMessage(Side side, Class<?> clazz) {
		try {
			Lanthanoid.inst.network.registerMessage(
					(Class<? extends IMessageHandler<REQ, ? extends IMessage>>)Class.forName(clazz.getName()+"$Handler"),
					(Class<REQ>)Class.forName(clazz.getName()+"$Message"), discriminator, side);
			discriminator++;
		} catch (ClassNotFoundException e) {
			Throwables.propagate(e);
		}
	}
}

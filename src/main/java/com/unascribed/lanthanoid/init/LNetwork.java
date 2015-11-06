package com.unascribed.lanthanoid.init;

import com.unascribed.lanthanoid.Lanthanoid;
import com.unascribed.lanthanoid.network.BeamParticleHandler;
import com.unascribed.lanthanoid.network.BeamParticleMessage;
import com.unascribed.lanthanoid.network.ModifyRifleModeHandler;
import com.unascribed.lanthanoid.network.ModifyRifleModeMessage;
import com.unascribed.lanthanoid.network.RifleChargingSoundHandler;
import com.unascribed.lanthanoid.network.RifleChargingSoundRequest;
import com.unascribed.lanthanoid.network.SetScopeFactorHandler;
import com.unascribed.lanthanoid.network.SetScopeFactorMessage;
import com.unascribed.lanthanoid.network.SpaceShipCrashHandler;
import com.unascribed.lanthanoid.network.SpaceShipCrashMessage;
import com.unascribed.lanthanoid.network.ToggleRifleBlazeModeHandler;
import com.unascribed.lanthanoid.network.ToggleRifleBlazeModeMessage;

import cpw.mods.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import cpw.mods.fml.relauncher.Side;

public class LNetwork {
	public static void init() {
		SimpleNetworkWrapper network = new SimpleNetworkWrapper("Lanthanoid");
		network.registerMessage(RifleChargingSoundHandler.class, RifleChargingSoundRequest.class, 0, Side.CLIENT);
		network.registerMessage(ModifyRifleModeHandler.class, ModifyRifleModeMessage.class, 1, Side.SERVER);
		network.registerMessage(BeamParticleHandler.class, BeamParticleMessage.class, 2, Side.CLIENT);
		network.registerMessage(SetScopeFactorHandler.class, SetScopeFactorMessage.class, 3, Side.CLIENT);
		network.registerMessage(SpaceShipCrashHandler.class, SpaceShipCrashMessage.class, 4, Side.CLIENT);
		network.registerMessage(ToggleRifleBlazeModeHandler.class, ToggleRifleBlazeModeMessage.class, 5, Side.SERVER);
		Lanthanoid.inst.network = network;
	}
}

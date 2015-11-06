package com.unascribed.lanthanoid.init;

import com.unascribed.lanthanoid.Lanthanoid;
import com.unascribed.lanthanoid.client.TextureCompositorImpl.BlockBackdrop;
import com.unascribed.lanthanoid.client.TextureCompositorImpl.BlockType;
import com.unascribed.lanthanoid.util.TextureCompositor;

public class LMachines {
	public static void init() {
		TextureCompositor compositor = Lanthanoid.inst.compositor;
		compositor.addBlock("machineCobbleSide", 0xFFFFFF, BlockType.MACHINE_BLOCK, BlockBackdrop.COBBLESTONE);
		compositor.addBlock("machineCobbleTop", 0xFFFFFF, BlockType.MACHINE_BLOCK_TOP, BlockBackdrop.COBBLESTONE);
		compositor.addBlock("machineCobbleBottom", 0xFFFFFF, BlockType.MACHINE_BLOCK_BOTTOM, BlockBackdrop.COBBLESTONE);
		
		compositor.addBlock("machineCombustorFrontWorking", 0xFFFFFF, BlockType.MACHINE_COMBUSTOR_WORKING, BlockBackdrop.COBBLESTONE);
		compositor.addBlock("machineCombustorFrontIdle", 0xFFFFFF, BlockType.MACHINE_COMBUSTOR_IDLE, BlockBackdrop.COBBLESTONE);
	}
}

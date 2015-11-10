package com.unascribed.lanthanoid.init;

import com.unascribed.lanthanoid.Lanthanoid;
import com.unascribed.lanthanoid.client.TextureCompositorImpl.BlockBackdrop;
import com.unascribed.lanthanoid.client.TextureCompositorImpl.BlockType;
import com.unascribed.lanthanoid.util.TextureCompositor;

public class LMachines {
	public static void init() {
		TextureCompositor compositor = Lanthanoid.inst.compositor;
		if (compositor != null) {
			compositor.addBlock("machineCobbleSide", 0xFFFFFF, BlockType.MACHINE_BLOCK, BlockBackdrop.COBBLESTONE);
			compositor.addBlock("machineCobbleTop", 0xFFFFFF, BlockType.MACHINE_BLOCK_TOP, BlockBackdrop.COBBLESTONE);
			compositor.addBlock("machineCobbleBottom", 0xFFFFFF, BlockType.MACHINE_BLOCK_BOTTOM, BlockBackdrop.COBBLESTONE);
			
			compositor.addBlock("machineCombustorFrontWorking", 0xFFFFFF, BlockType.MACHINE_COMBUSTOR_WORKING, BlockBackdrop.COBBLESTONE);
			compositor.addBlock("machineCombustorFrontIdle", 0xFFFFFF, BlockType.MACHINE_COMBUSTOR_IDLE, BlockBackdrop.COBBLESTONE);
			
			int holmium = LMaterials.colors.get("Holmium");
			
			compositor.addBlock("machineWaypointTop", holmium, BlockType.WAYPOINT_TOP, BlockBackdrop.NONE);
			compositor.addBlock("machineWaypointBottom", holmium, BlockType.WAYPOINT_BOTTOM, BlockBackdrop.NONE);
			compositor.addBlock("machineWaypointSide", holmium, BlockType.WAYPOINT_SIDE, BlockBackdrop.NONE);
			compositor.addBlock("machineWaypointSideDiamond", LMaterials.colors.get("Diamond"), BlockType.WAYPOINT_SIDE_DIAMOND, BlockBackdrop.WAYPOINT_SIDE);
			compositor.addBlock("machineWaypointSideRaspite", LMaterials.colors.get("Raspite"), BlockType.WAYPOINT_SIDE_TRIANGLE, BlockBackdrop.WAYPOINT_SIDE);
		}
	}
}

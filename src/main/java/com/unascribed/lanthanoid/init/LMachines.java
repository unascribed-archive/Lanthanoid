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
			int yttrium = LMaterials.colors.get("Yttrium");
			
			compositor.addBlock("machineWaypointTopHolmium", holmium, BlockType.WAYPOINT_TOP, BlockBackdrop.NONE);
			compositor.addBlock("machineWaypointBottomHolmium", holmium, BlockType.WAYPOINT_BOTTOM, BlockBackdrop.NONE);
			compositor.addBlock("machineWaypointSideHolmium", holmium, BlockType.WAYPOINT_SIDE, BlockBackdrop.NONE);
			
			compositor.addBlock("machineWaypointTopYttrium", yttrium, BlockType.WAYPOINT_TOP, BlockBackdrop.NONE);
			compositor.addBlock("machineWaypointBottomYttrium", yttrium, BlockType.WAYPOINT_BOTTOM, BlockBackdrop.NONE);
			compositor.addBlock("machineWaypointSideYttrium", yttrium, BlockType.WAYPOINT_SIDE, BlockBackdrop.NONE);
			
			compositor.addBlock("machineWaypointSideDiamond", LMaterials.colors.get("Diamond"), BlockType.WAYPOINT_SIDE_DIAMOND, BlockBackdrop.WAYPOINT_SIDE_HOLMIUM);
			compositor.addBlock("machineWaypointSideRaspite", LMaterials.colors.get("Raspite"), BlockType.WAYPOINT_SIDE_TRIANGLE, BlockBackdrop.WAYPOINT_SIDE_HOLMIUM);
			compositor.addBlock("machineWaypointSideDiaspore", LMaterials.colors.get("Diaspore"), BlockType.WAYPOINT_SIDE_CIRCLE, BlockBackdrop.WAYPOINT_SIDE_YTTRIUM);
		}
	}
}

package com.unascribed.lanthanoid.tile;

public class TileEntityEldritchInductor extends TileEntityEldritch {

	@Override
	protected void doTickLogic() {
		
	}
	
	@Override
	public boolean canReceiveGlyphs() {
		return true;
	}

	@Override
	public boolean canSendGlyphs() {
		return false;
	}

}

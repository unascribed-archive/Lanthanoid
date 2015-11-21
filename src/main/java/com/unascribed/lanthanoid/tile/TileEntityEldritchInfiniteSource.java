package com.unascribed.lanthanoid.tile;

public class TileEntityEldritchInfiniteSource extends TileEntityEldritchWithBooks {

	@Override
	public boolean canReceiveGlyphs() {
		return false;
	}

	@Override
	public boolean canSendGlyphs() {
		return true;
	}

	@Override
	protected void doTickLogic() {}
	
	@Override
	public int getMilliglyphs() {
		return getMaxMilliglyphs()/2;
	}
	
	@Override
	public int getMaxMilliglyphs() {
		int q = getBookCount()+1;
		return q*100000;
	}

}

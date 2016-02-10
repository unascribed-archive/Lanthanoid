package com.unascribed.lanthanoid.tile;

import java.util.List;

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
	
	@Override
	protected void addDebugText(List<String> li) {
		li.add("\u00A7d\u00A7l["+Integer.toHexString(getId())+"]");
		li.add(getGlyphString(getMilliglyphs())+"g");
	}

}

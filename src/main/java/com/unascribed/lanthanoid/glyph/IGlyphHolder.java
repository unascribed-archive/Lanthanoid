package com.unascribed.lanthanoid.glyph;

public interface IGlyphHolder {
	int getMilliglyphs();
	void setMilliglyphs(int milliglyphs);
	int getMaxMilliglyphs();
	
	boolean canReceiveGlyphs();
	boolean canSendGlyphs();
	
	boolean transferFrom(IGlyphHolder from, boolean force);
}

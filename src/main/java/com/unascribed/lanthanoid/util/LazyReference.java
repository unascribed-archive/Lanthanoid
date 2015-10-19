package com.unascribed.lanthanoid.util;

import com.google.common.base.Supplier;

public class LazyReference<T> {
	private final Supplier<T> supplier;
	
	// Use a boolean instead of checking for null so that the supplier
	// can return null without thrashing, in the event it's an expensive
	// operation that can legitimately return null
	private boolean populated = false;
	private T value;
	
	public LazyReference(Supplier<T> supplier) {
		this.supplier = supplier;
	}
	
	public T get() {
		if (!populated) {
			value = supplier.get();
			populated = true;
		}
		return value;
	}
	
	public void clear() {
		populated = false;
		// Just setting populated to false would suffice, but we'll clear
		// the reference too so we don't keep it from being GC'd
		value = null;
	}
}

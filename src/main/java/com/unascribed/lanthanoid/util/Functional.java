package com.unascribed.lanthanoid.util;

import com.google.common.base.Supplier;

public class Functional {

	public static <T> ThreadLocal<T> newThreadLocal(Supplier<T> supplier) {
		return new ThreadLocal<T>() {
			@Override
			protected T initialValue() {
				return supplier.get();
			}
		};
	}

}

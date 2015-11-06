package com.unascribed.lanthanoid.util;

import java.util.List;

import com.google.common.collect.Lists;

public class LArrays {
	public static String[] exclude(String[] arr, String... exclude) {
		List<String> li = Lists.newArrayList(arr);
		for (String s : exclude) {
			li.remove(s);
		}
		return li.toArray(new String[li.size()]);
	}
	public static String[] all(List<String> types, String... prefixes) {
		int count = prefixes.length;
		String[] res = new String[types.size()*count];
		int idx = 0;
		for (int j = 0; j < count; j++) {
			for (int i = 0; i < types.size(); i++) {
				res[idx] = prefixes[j]+types.get(i);
				idx++;
			}
		}
		return res;
	}
}

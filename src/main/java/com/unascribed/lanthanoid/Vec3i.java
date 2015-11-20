package com.unascribed.lanthanoid;

import java.util.regex.Pattern;

public final class Vec3i {
	public int x,y,z;

	public Vec3i() {}
	public Vec3i(int x, int y, int z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}
	
	@Override
	public Vec3i clone() {
		return new Vec3i(x, y, z);
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + x;
		result = prime * result + y;
		result = prime * result + z;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		Vec3i other = (Vec3i) obj;
		if (x != other.x) {
			return false;
		}
		if (y != other.y) {
			return false;
		}
		if (z != other.z) {
			return false;
		}
		return true;
	}
	
	@Override
	public String toString() {
		return x+","+y+","+z;
	}
	
	private static Pattern vecPattern = Pattern.compile("^-?[0-9]+,-?[0-9]+,-?[0-9]+$");
	private static Pattern comma = Pattern.compile("\\Q,\\E");
	
	public static Vec3i fromString(String s) {
		if (vecPattern.matcher(s).matches()) {
			String[] split = comma.split(s);
			return new Vec3i(Integer.parseInt(split[0]), Integer.parseInt(split[1]), Integer.parseInt(split[2]));
		} else {
			throw new IllegalArgumentException("String '"+s+"' is not a valid Vec3i");
		}
	}
	
}

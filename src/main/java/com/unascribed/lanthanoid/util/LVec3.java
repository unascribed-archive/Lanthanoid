package com.unascribed.lanthanoid.util;

import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;

/**
 * Alternate version of Vec3 that avoids allocating new objects
 * and has a few new methods.
 */
public class LVec3 extends Vec3 {

	public LVec3(double x, double y, double z) {
		super(x, y, z);
	}

	public LVec3 multiply(double mult) {
		return setComponents(xCoord * mult, yCoord * mult, zCoord * mult);
	}

	/**
	 * Sets the x,y,z components of the vector as specified.
	 */
	public LVec3 setComponents(double p_72439_1_, double p_72439_3_, double p_72439_5_) {
		this.xCoord = p_72439_1_;
		this.yCoord = p_72439_3_;
		this.zCoord = p_72439_5_;
		return this;
	}

	/**
	 * Returns a new vector with the result of the specified vector minus this.
	 */
	public LVec3 subtract(LVec3 p_72444_1_) {
		/**
		 * Static method for creating a setComponentsD given the three x,y,z values.
		 * This is only called from the other static method which creates and
		 * places it in the list.
		 */
		return setComponents(p_72444_1_.xCoord - this.xCoord, p_72444_1_.yCoord - this.yCoord, p_72444_1_.zCoord - this.zCoord);
	}

	/**
	 * Normalizes the vector to a length of 1 (except if it is the zero vector)
	 */
	public LVec3 normalize() {
		double d0 = (double) MathHelper.sqrt_double(this.xCoord * this.xCoord + this.yCoord * this.yCoord + this.zCoord * this.zCoord);
		return d0 < 1.0E-4D ? setComponents(0.0D, 0.0D, 0.0D) : setComponents(this.xCoord / d0, this.yCoord / d0, this.zCoord / d0);
	}

	public double dotProduct(LVec3 p_72430_1_) {
		return this.xCoord * p_72430_1_.xCoord + this.yCoord * p_72430_1_.yCoord + this.zCoord * p_72430_1_.zCoord;
	}

	/**
	 * Returns a new vector with the result of this vector x the specified
	 * vector.
	 */
	public LVec3 crossProduct(LVec3 p_72431_1_) {
		/**
		 * Static method for creating a setComponentsD given the three x,y,z values.
		 * This is only called from the other static method which creates and
		 * places it in the list.
		 */
		return setComponents(this.yCoord * p_72431_1_.zCoord - this.zCoord * p_72431_1_.yCoord, this.zCoord * p_72431_1_.xCoord - this.xCoord * p_72431_1_.zCoord, this.xCoord * p_72431_1_.yCoord - this.yCoord * p_72431_1_.xCoord);
	}

	/**
	 * Adds the specified x,y,z vector components to this vector and returns the
	 * resulting vector. Does not change this vector.
	 */
	public LVec3 addVector(double p_72441_1_, double p_72441_3_, double p_72441_5_) {
		/**
		 * Static method for creating a setComponentsD given the three x,y,z values.
		 * This is only called from the other static method which creates and
		 * places it in the list.
		 */
		return setComponents(this.xCoord + p_72441_1_, this.yCoord + p_72441_3_, this.zCoord + p_72441_5_);
	}

	/**
	 * Euclidean distance between this and the specified vector, returned as
	 * double.
	 */
	public double distanceTo(LVec3 p_72438_1_) {
		double d0 = p_72438_1_.xCoord - this.xCoord;
		double d1 = p_72438_1_.yCoord - this.yCoord;
		double d2 = p_72438_1_.zCoord - this.zCoord;
		return (double) MathHelper.sqrt_double(d0 * d0 + d1 * d1 + d2 * d2);
	}

	/**
	 * The square of the Euclidean distance between this and the specified
	 * vector.
	 */
	public double squareDistanceTo(LVec3 p_72436_1_) {
		double d0 = p_72436_1_.xCoord - this.xCoord;
		double d1 = p_72436_1_.yCoord - this.yCoord;
		double d2 = p_72436_1_.zCoord - this.zCoord;
		return d0 * d0 + d1 * d1 + d2 * d2;
	}

	/**
	 * The square of the Euclidean distance between this and the vector of x,y,z
	 * components passed in.
	 */
	public double squareDistanceTo(double p_72445_1_, double p_72445_3_, double p_72445_5_) {
		double d3 = p_72445_1_ - this.xCoord;
		double d4 = p_72445_3_ - this.yCoord;
		double d5 = p_72445_5_ - this.zCoord;
		return d3 * d3 + d4 * d4 + d5 * d5;
	}

	/**
	 * Returns the length of the vector.
	 */
	public double lengthVector() {
		return (double) MathHelper.sqrt_double(this.xCoord * this.xCoord + this.yCoord * this.yCoord + this.zCoord * this.zCoord);
	}

	/**
	 * Returns a new vector with x value equal to the second parameter, along
	 * the line between this vector and the passed in vector, or null if not
	 * possible.
	 */
	public LVec3 getIntermediateWithXValue(LVec3 p_72429_1_, double p_72429_2_) {
		double d1 = p_72429_1_.xCoord - this.xCoord;
		double d2 = p_72429_1_.yCoord - this.yCoord;
		double d3 = p_72429_1_.zCoord - this.zCoord;

		if (d1 * d1 < 1.0000000116860974E-7D) {
			return null;
		} else {
			double d4 = (p_72429_2_ - this.xCoord) / d1;
			return d4 >= 0.0D && d4 <= 1.0D ? setComponents(this.xCoord + d1 * d4, this.yCoord + d2 * d4, this.zCoord + d3 * d4) : null;
		}
	}

	/**
	 * Returns a new vector with y value equal to the second parameter, along
	 * the line between this vector and the passed in vector, or null if not
	 * possible.
	 */
	public LVec3 getIntermediateWithYValue(LVec3 p_72435_1_, double p_72435_2_) {
		double d1 = p_72435_1_.xCoord - this.xCoord;
		double d2 = p_72435_1_.yCoord - this.yCoord;
		double d3 = p_72435_1_.zCoord - this.zCoord;

		if (d2 * d2 < 1.0000000116860974E-7D) {
			return null;
		} else {
			double d4 = (p_72435_2_ - this.yCoord) / d2;
			return d4 >= 0.0D && d4 <= 1.0D ? setComponents(this.xCoord + d1 * d4, this.yCoord + d2 * d4, this.zCoord + d3 * d4) : null;
		}
	}

	/**
	 * Returns a new vector with z value equal to the second parameter, along
	 * the line between this vector and the passed in vector, or null if not
	 * possible.
	 */
	public LVec3 getIntermediateWithZValue(LVec3 p_72434_1_, double p_72434_2_) {
		double d1 = p_72434_1_.xCoord - this.xCoord;
		double d2 = p_72434_1_.yCoord - this.yCoord;
		double d3 = p_72434_1_.zCoord - this.zCoord;

		if (d3 * d3 < 1.0000000116860974E-7D) {
			return null;
		} else {
			double d4 = (p_72434_2_ - this.zCoord) / d3;
			return d4 >= 0.0D && d4 <= 1.0D ? setComponents(this.xCoord + d1 * d4, this.yCoord + d2 * d4, this.zCoord + d3 * d4) : null;
		}
	}

	public String toString() {
		return "(" + this.xCoord + ", " + this.yCoord + ", " + this.zCoord + ")";
	}

	/**
	 * Rotates the vector around the x axis by the specified angle.
	 */
	public void rotateAroundX(float p_72440_1_) {
		float f1 = MathHelper.cos(p_72440_1_);
		float f2 = MathHelper.sin(p_72440_1_);
		double d0 = this.xCoord;
		double d1 = this.yCoord * (double) f1 + this.zCoord * (double) f2;
		double d2 = this.zCoord * (double) f1 - this.yCoord * (double) f2;
		this.setComponents(d0, d1, d2);
	}

	/**
	 * Rotates the vector around the y axis by the specified angle.
	 */
	public void rotateAroundY(float p_72442_1_) {
		float f1 = MathHelper.cos(p_72442_1_);
		float f2 = MathHelper.sin(p_72442_1_);
		double d0 = this.xCoord * (double) f1 + this.zCoord * (double) f2;
		double d1 = this.yCoord;
		double d2 = this.zCoord * (double) f1 - this.xCoord * (double) f2;
		this.setComponents(d0, d1, d2);
	}

	/**
	 * Rotates the vector around the z axis by the specified angle.
	 */
	public void rotateAroundZ(float p_72446_1_) {
		float f1 = MathHelper.cos(p_72446_1_);
		float f2 = MathHelper.sin(p_72446_1_);
		double d0 = this.xCoord * (double) f1 + this.yCoord * (double) f2;
		double d1 = this.yCoord * (double) f1 - this.xCoord * (double) f2;
		double d2 = this.zCoord;
		this.setComponents(d0, d1, d2);
	}
	
	/**
	 * Copy this LVec3 into a Vec3, for passing into other code that expects Vec3's
	 * copy-on-write behavior. 
	 */
	public Vec3 toVec3() {
		return Vec3.createVectorHelper(xCoord, yCoord, zCoord);
	}

}

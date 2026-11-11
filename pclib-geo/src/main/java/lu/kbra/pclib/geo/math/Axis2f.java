package lu.kbra.pclib.geo.math;

import java.nio.*;

import org.joml.*;
import org.joml.Vector2f;
import org.joml.Vector2fc;

import lu.kbra.pclib.geo.Owned;

public class Axis2f extends Vector2f implements Axis2fc, Cloneable {
	@Override
	public Axis2f getToAddress(final long address) {
		super.getToAddress(address);
		return this;
	}

	public Axis2f() {
		super(1, 0);
	}

	public Axis2f(final float x, final float y) {
		super(x, y);
		this.validate();
	}

	public Axis2f(final Vector2fc v) {
		this(v.x(), v.y());
	}

	private void validate() {
		if (!(Float.isFinite(this.x) && Float.isFinite(this.y)) || this.lengthSquared() == 0) {
			throw new IllegalArgumentException("Invalid axis");
		}
		this.normalize();
	}

	public Axis2f setUnit(final Vector2fc other) {
		this.set(other);
		this.validate();
		return this;
	}

	@Override
	public @Owned Axis2f clone() {
		return new Axis2f(this);
	}

	@Override
	public Axis2f set(final float p0) {
		super.set(p0);
		return this;
	}

	@Override
	public Axis2f set(final float p0, final float p1) {
		super.set(p0, p1);
		return this;
	}

	@Override
	public Axis2f set(final double p0) {
		super.set(p0);
		return this;
	}

	@Override
	public Axis2f set(final double p0, final double p1) {
		super.set(p0, p1);
		return this;
	}

	@Override
	public Axis2f set(final Vector2fc p0) {
		super.set(p0);
		return this;
	}

	@Override
	public Axis2f set(final Vector2ic p0) {
		super.set(p0);
		return this;
	}

	@Override
	public Axis2f set(final Vector2dc p0) {
		super.set(p0);
		return this;
	}

	@Override
	public Axis2f set(final Vector3dc p0) {
		super.set(p0);
		return this;
	}

	@Override
	public Axis2f set(final Vector3fc p0) {
		super.set(p0);
		return this;
	}

	@Override
	public Axis2f set(final Vector3ic p0) {
		super.set(p0);
		return this;
	}

	@Override
	public Axis2f set(final float[] p0) {
		super.set(p0);
		return this;
	}

	@Override
	public Axis2f set(final ByteBuffer p0) {
		super.set(p0);
		return this;
	}

	@Override
	public Axis2f set(final int p0, final ByteBuffer p1) {
		super.set(p0, p1);
		return this;
	}

	@Override
	public Axis2f set(final FloatBuffer p0) {
		super.set(p0);
		return this;
	}

	@Override
	public Axis2f set(final int p0, final FloatBuffer p1) {
		super.set(p0, p1);
		return this;
	}

	@Override
	public Axis2f setFromAddress(final long p0) {
		super.setFromAddress(p0);
		return this;
	}

	@Override
	public Axis2f setComponent(final int p0, final float p1) {
		super.setComponent(p0, p1);
		return this;
	}

	@Override
	public Axis2f perpendicular() {
		super.perpendicular();
		return this;
	}

	@Override
	public Axis2f sub(final Vector2fc p0) {
		super.sub(p0);
		return this;
	}

	@Override
	public Axis2f sub(final float p0, final float p1) {
		super.sub(p0, p1);
		return this;
	}

	@Override
	public Axis2f normalize() {
		super.normalize();
		return this;
	}

	@Override
	public Axis2f normalize(final float p0) {
		super.normalize(p0);
		return this;
	}

	@Override
	public Axis2f add(final Vector2fc p0) {
		super.add(p0);
		return this;
	}

	@Override
	public Axis2f add(final float p0, final float p1) {
		super.add(p0, p1);
		return this;
	}

	@Override
	public Axis2f zero() {
		super.zero();
		return this;
	}

	@Override
	public Axis2f negate() {
		super.negate();
		return this;
	}

	@Override
	public Axis2f mul(final float p0) {
		super.mul(p0);
		return this;
	}

	@Override
	public Axis2f mul(final float p0, final float p1) {
		super.mul(p0, p1);
		return this;
	}

	@Override
	public Axis2f mul(final Vector2fc p0) {
		super.mul(p0);
		return this;
	}

	@Override
	public Axis2f div(final Vector2fc p0) {
		super.div(p0);
		return this;
	}

	@Override
	public Axis2f div(final float p0) {
		super.div(p0);
		return this;
	}

	@Override
	public Axis2f div(final float p0, final float p1) {
		super.div(p0, p1);
		return this;
	}

	@Override
	public Axis2f mul(final Matrix2fc p0) {
		super.mul(p0);
		return this;
	}

	@Override
	public Axis2f mul(final Matrix2dc p0) {
		super.mul(p0);
		return this;
	}

	@Override
	public Axis2f mulTranspose(final Matrix2fc p0) {
		super.mulTranspose(p0);
		return this;
	}

	@Override
	public Axis2f mulPosition(final Matrix3x2fc p0) {
		super.mulPosition(p0);
		return this;
	}

	@Override
	public Axis2f mulDirection(final Matrix3x2fc p0) {
		super.mulDirection(p0);
		return this;
	}

	@Override
	public Axis2f lerp(final Vector2fc p0, final float p1) {
		super.lerp(p0, p1);
		return this;
	}

	@Override
	public Axis2f fma(final Vector2fc p0, final Vector2fc p1) {
		super.fma(p0, p1);
		return this;
	}

	@Override
	public Axis2f fma(final float p0, final Vector2fc p1) {
		super.fma(p0, p1);
		return this;
	}

	@Override
	public Axis2f min(final Vector2fc p0) {
		super.min(p0);
		return this;
	}

	@Override
	public Axis2f max(final Vector2fc p0) {
		super.max(p0);
		return this;
	}

	@Override
	public Axis2f floor() {
		super.floor();
		return this;
	}

	@Override
	public Axis2f ceil() {
		super.ceil();
		return this;
	}

	@Override
	public Axis2f round() {
		super.round();
		return this;
	}

	@Override
	public Axis2f absolute() {
		super.absolute();
		return this;
	}
}

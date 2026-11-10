package lu.kbra.pclib.geo.math;

import java.nio.ByteBuffer;
import java.nio.DoubleBuffer;

import org.joml.Matrix2dc;
import org.joml.Matrix2fc;
import org.joml.Matrix3x2dc;
import org.joml.Vector2d;
import org.joml.Vector2dc;
import org.joml.Vector2fc;
import org.joml.Vector2ic;
import org.joml.Vector3dc;
import org.joml.Vector3fc;
import org.joml.Vector3ic;

import lu.kbra.pclib.geo.Owned;

public class Axis2d extends Vector2d implements Axis2dc, Cloneable {
	@Override
	public Axis2d getToAddress(final long address) {
		super.getToAddress(address);
		return this;
	}

	@Override
	public Axis2d set(final double p0) {
		super.set(p0);
		return this;
	}

	@Override
	public Axis2d set(final double p0, final double p1) {
		super.set(p0, p1);
		return this;
	}

	@Override
	public Axis2d set(final Vector2dc p0) {
		super.set(p0);
		return this;
	}

	@Override
	public Axis2d set(final Vector2fc p0) {
		super.set(p0);
		return this;
	}

	@Override
	public Axis2d set(final Vector2ic p0) {
		super.set(p0);
		return this;
	}

	@Override
	public Axis2d set(final Vector3dc p0) {
		super.set(p0);
		return this;
	}

	@Override
	public Axis2d set(final Vector3fc p0) {
		super.set(p0);
		return this;
	}

	@Override
	public Axis2d set(final Vector3ic p0) {
		super.set(p0);
		return this;
	}

	@Override
	public Axis2d set(final double[] p0) {
		super.set(p0);
		return this;
	}

	@Override
	public Axis2d set(final float[] p0) {
		super.set(p0);
		return this;
	}

	@Override
	public Axis2d set(final ByteBuffer p0) {
		super.set(p0);
		return this;
	}

	@Override
	public Axis2d set(final int p0, final ByteBuffer p1) {
		super.set(p0, p1);
		return this;
	}

	@Override
	public Axis2d set(final DoubleBuffer p0) {
		super.set(p0);
		return this;
	}

	@Override
	public Axis2d set(final int p0, final DoubleBuffer p1) {
		super.set(p0, p1);
		return this;
	}

	@Override
	public Axis2d setFromAddress(final long p0) {
		super.setFromAddress(p0);
		return this;
	}

	@Override
	public Axis2d get(final Vector2d p0) {
		super.get(p0);
		return this;
	}

	@Override
	public Axis2d setComponent(final int p0, final double p1) {
		super.setComponent(p0, p1);
		return this;
	}

	@Override
	public Axis2d perpendicular() {
		super.perpendicular();
		return this;
	}

	@Override
	public Axis2d sub(final Vector2dc p0) {
		super.sub(p0);
		return this;
	}

	@Override
	public Axis2d sub(final double p0, final double p1) {
		super.sub(p0, p1);
		return this;
	}

	@Override
	public Axis2d sub(final double p0, final double p1, final Vector2d p2) {
		super.sub(p0, p1, p2);
		return this;
	}

	@Override
	public Axis2d sub(final Vector2fc p0) {
		super.sub(p0);
		return this;
	}

	@Override
	public Axis2d sub(final Vector2dc p0, final Vector2d p1) {
		super.sub(p0, p1);
		return this;
	}

	@Override
	public Axis2d sub(final Vector2fc p0, final Vector2d p1) {
		super.sub(p0, p1);
		return this;
	}

	@Override
	public Axis2d mul(final double p0) {
		super.mul(p0);
		return this;
	}

	@Override
	public Axis2d mul(final double p0, final Vector2d p1) {
		super.mul(p0, p1);
		return this;
	}

	@Override
	public Axis2d mul(final double p0, final double p1) {
		super.mul(p0, p1);
		return this;
	}

	@Override
	public Axis2d mul(final double p0, final double p1, final Vector2d p2) {
		super.mul(p0, p1, p2);
		return this;
	}

	@Override
	public Axis2d mul(final Vector2dc p0) {
		super.mul(p0);
		return this;
	}

	@Override
	public Axis2d mul(final Vector2dc p0, final Vector2d p1) {
		super.mul(p0, p1);
		return this;
	}

	@Override
	public Axis2d div(final double p0) {
		super.div(p0);
		return this;
	}

	@Override
	public Axis2d div(final double p0, final Vector2d p1) {
		super.div(p0, p1);
		return this;
	}

	@Override
	public Axis2d div(final double p0, final double p1) {
		super.div(p0, p1);
		return this;
	}

	@Override
	public Axis2d div(final double p0, final double p1, final Vector2d p2) {
		super.div(p0, p1, p2);
		return this;
	}

	@Override
	public Axis2d div(final Vector2dc p0) {
		super.div(p0);
		return this;
	}

	@Override
	public Axis2d div(final Vector2fc p0) {
		super.div(p0);
		return this;
	}

	@Override
	public Axis2d div(final Vector2fc p0, final Vector2d p1) {
		super.div(p0, p1);
		return this;
	}

	@Override
	public Axis2d div(final Vector2dc p0, final Vector2d p1) {
		super.div(p0, p1);
		return this;
	}

	@Override
	public Axis2d mul(final Matrix2fc p0) {
		super.mul(p0);
		return this;
	}

	@Override
	public Axis2d mul(final Matrix2dc p0) {
		super.mul(p0);
		return this;
	}

	@Override
	public Axis2d mul(final Matrix2dc p0, final Vector2d p1) {
		super.mul(p0, p1);
		return this;
	}

	@Override
	public Axis2d mul(final Matrix2fc p0, final Vector2d p1) {
		super.mul(p0, p1);
		return this;
	}

	@Override
	public Axis2d mulTranspose(final Matrix2dc p0) {
		super.mulTranspose(p0);
		return this;
	}

	@Override
	public Axis2d mulTranspose(final Matrix2dc p0, final Vector2d p1) {
		super.mulTranspose(p0, p1);
		return this;
	}

	@Override
	public Axis2d mulTranspose(final Matrix2fc p0) {
		super.mulTranspose(p0);
		return this;
	}

	@Override
	public Axis2d mulTranspose(final Matrix2fc p0, final Vector2d p1) {
		super.mulTranspose(p0, p1);
		return this;
	}

	@Override
	public Axis2d mulPosition(final Matrix3x2dc p0) {
		super.mulPosition(p0);
		return this;
	}

	@Override
	public Axis2d mulPosition(final Matrix3x2dc p0, final Vector2d p1) {
		super.mulPosition(p0, p1);
		return this;
	}

	@Override
	public Axis2d mulDirection(final Matrix3x2dc p0) {
		super.mulDirection(p0);
		return this;
	}

	@Override
	public Axis2d mulDirection(final Matrix3x2dc p0, final Vector2d p1) {
		super.mulDirection(p0, p1);
		return this;
	}

	@Override
	public Axis2d normalize() {
		super.normalize();
		return this;
	}

	@Override
	public Axis2d normalize(final Vector2d p0) {
		super.normalize(p0);
		return this;
	}

	@Override
	public Axis2d normalize(final double p0) {
		super.normalize(p0);
		return this;
	}

	@Override
	public Axis2d normalize(final double p0, final Vector2d p1) {
		super.normalize(p0, p1);
		return this;
	}

	@Override
	public Axis2d add(final Vector2dc p0) {
		super.add(p0);
		return this;
	}

	@Override
	public Axis2d add(final double p0, final double p1) {
		super.add(p0, p1);
		return this;
	}

	@Override
	public Axis2d add(final double p0, final double p1, final Vector2d p2) {
		super.add(p0, p1, p2);
		return this;
	}

	@Override
	public Axis2d add(final Vector2fc p0) {
		super.add(p0);
		return this;
	}

	@Override
	public Axis2d add(final Vector2dc p0, final Vector2d p1) {
		super.add(p0, p1);
		return this;
	}

	@Override
	public Axis2d add(final Vector2fc p0, final Vector2d p1) {
		super.add(p0, p1);
		return this;
	}

	@Override
	public Axis2d zero() {
		super.zero();
		return this;
	}

	@Override
	public Axis2d negate() {
		super.negate();
		return this;
	}

	@Override
	public Axis2d negate(final Vector2d p0) {
		super.negate(p0);
		return this;
	}

	@Override
	public Axis2d lerp(final Vector2dc p0, final double p1) {
		super.lerp(p0, p1);
		return this;
	}

	@Override
	public Axis2d lerp(final Vector2dc p0, final double p1, final Vector2d p2) {
		super.lerp(p0, p1, p2);
		return this;
	}

	@Override
	public Axis2d fma(final Vector2dc p0, final Vector2dc p1) {
		super.fma(p0, p1);
		return this;
	}

	@Override
	public Axis2d fma(final double p0, final Vector2dc p1) {
		super.fma(p0, p1);
		return this;
	}

	@Override
	public Axis2d fma(final Vector2dc p0, final Vector2dc p1, final Vector2d p2) {
		super.fma(p0, p1, p2);
		return this;
	}

	@Override
	public Axis2d fma(final double p0, final Vector2dc p1, final Vector2d p2) {
		super.fma(p0, p1, p2);
		return this;
	}

	@Override
	public Axis2d min(final Vector2dc p0) {
		super.min(p0);
		return this;
	}

	@Override
	public Axis2d min(final Vector2dc p0, final Vector2d p1) {
		super.min(p0, p1);
		return this;
	}

	@Override
	public Axis2d max(final Vector2dc p0) {
		super.max(p0);
		return this;
	}

	@Override
	public Axis2d max(final Vector2dc p0, final Vector2d p1) {
		super.max(p0, p1);
		return this;
	}

	@Override
	public Axis2d floor() {
		super.floor();
		return this;
	}

	@Override
	public Axis2d floor(final Vector2d p0) {
		super.floor(p0);
		return this;
	}

	@Override
	public Axis2d ceil() {
		super.ceil();
		return this;
	}

	@Override
	public Axis2d ceil(final Vector2d p0) {
		super.ceil(p0);
		return this;
	}

	@Override
	public Axis2d round() {
		super.round();
		return this;
	}

	@Override
	public Axis2d round(final Vector2d p0) {
		super.round(p0);
		return this;
	}

	@Override
	public Axis2d absolute() {
		super.absolute();
		return this;
	}

	@Override
	public Axis2d absolute(final Vector2d p0) {
		super.absolute(p0);
		return this;
	}

	public Axis2d() {
		super(1, 0);
	}

	public Axis2d(final double x, final double y) {
		super(x, y);
		this.check();
	}

	public Axis2d(final Vector2dc v) {
		this(v.x(), v.y());
	}

	private void check() {
		if (!Double.isFinite(this.x) || !Double.isFinite(this.y) || !(this.lengthSquared() > 0)) {
			throw new IllegalArgumentException("Invalid axis");
		}
		this.normalize();
	}

	@Override
	public @Owned Axis2d clone() {
		return new Axis2d(this);
	}

}

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
import lu.kbra.pclib.geo.two.curve.Line2dc;

public class Vertex2d extends Vector2d implements Vertex2dc, Cloneable {
	@Override
	public Vertex2d getToAddress(final long address) {
		super.getToAddress(address);
		return this;
	}

	@Override
	public Vertex2d set(final double p0) {
		super.set(p0);
		return this;
	}

	@Override
	public Vertex2d set(final double p0, final double p1) {
		super.set(p0, p1);
		return this;
	}

	@Override
	public Vertex2d set(final Vector2dc p0) {
		super.set(p0);
		return this;
	}

	@Override
	public Vertex2d set(final Vector2fc p0) {
		super.set(p0);
		return this;
	}

	@Override
	public Vertex2d set(final Vector2ic p0) {
		super.set(p0);
		return this;
	}

	@Override
	public Vertex2d set(final Vector3dc p0) {
		super.set(p0);
		return this;
	}

	@Override
	public Vertex2d set(final Vector3fc p0) {
		super.set(p0);
		return this;
	}

	@Override
	public Vertex2d set(final Vector3ic p0) {
		super.set(p0);
		return this;
	}

	@Override
	public Vertex2d set(final double[] p0) {
		super.set(p0);
		return this;
	}

	@Override
	public Vertex2d set(final float[] p0) {
		super.set(p0);
		return this;
	}

	@Override
	public Vertex2d set(final ByteBuffer p0) {
		super.set(p0);
		return this;
	}

	@Override
	public Vertex2d set(final int p0, final ByteBuffer p1) {
		super.set(p0, p1);
		return this;
	}

	@Override
	public Vertex2d set(final DoubleBuffer p0) {
		super.set(p0);
		return this;
	}

	@Override
	public Vertex2d set(final int p0, final DoubleBuffer p1) {
		super.set(p0, p1);
		return this;
	}

	@Override
	public Vertex2d setFromAddress(final long p0) {
		super.setFromAddress(p0);
		return this;
	}

	@Override
	public Vertex2d get(final Vector2d p0) {
		super.get(p0);
		return this;
	}

	@Override
	public Vertex2d setComponent(final int p0, final double p1) {
		super.setComponent(p0, p1);
		return this;
	}

	@Override
	public Vertex2d perpendicular() {
		super.perpendicular();
		return this;
	}

	@Override
	public Vertex2d sub(final double p0, final double p1) {
		super.sub(p0, p1);
		return this;
	}

	@Override
	public Vertex2d sub(final double p0, final double p1, final Vector2d p2) {
		super.sub(p0, p1, p2);
		return this;
	}

	@Override
	public Vertex2d sub(final Vector2fc p0) {
		super.sub(p0);
		return this;
	}

	@Override
	public Vertex2d sub(final Vector2dc p0, final Vector2d p1) {
		super.sub(p0, p1);
		return this;
	}

	@Override
	public Vertex2d sub(final Vector2fc p0, final Vector2d p1) {
		super.sub(p0, p1);
		return this;
	}

	@Override
	public Vertex2d mul(final double p0) {
		super.mul(p0);
		return this;
	}

	@Override
	public Vertex2d mul(final double p0, final Vector2d p1) {
		super.mul(p0, p1);
		return this;
	}

	@Override
	public Vertex2d mul(final double p0, final double p1) {
		super.mul(p0, p1);
		return this;
	}

	@Override
	public Vertex2d mul(final double p0, final double p1, final Vector2d p2) {
		super.mul(p0, p1, p2);
		return this;
	}

	@Override
	public Vertex2d mul(final Vector2dc p0) {
		super.mul(p0);
		return this;
	}

	@Override
	public Vertex2d mul(final Vector2dc p0, final Vector2d p1) {
		super.mul(p0, p1);
		return this;
	}

	@Override
	public Vertex2d div(final double p0) {
		super.div(p0);
		return this;
	}

	@Override
	public Vertex2d div(final double p0, final Vector2d p1) {
		super.div(p0, p1);
		return this;
	}

	@Override
	public Vertex2d div(final double p0, final double p1) {
		super.div(p0, p1);
		return this;
	}

	@Override
	public Vertex2d div(final double p0, final double p1, final Vector2d p2) {
		super.div(p0, p1, p2);
		return this;
	}

	@Override
	public Vertex2d div(final Vector2dc p0) {
		super.div(p0);
		return this;
	}

	@Override
	public Vertex2d div(final Vector2fc p0) {
		super.div(p0);
		return this;
	}

	@Override
	public Vertex2d div(final Vector2fc p0, final Vector2d p1) {
		super.div(p0, p1);
		return this;
	}

	@Override
	public Vertex2d div(final Vector2dc p0, final Vector2d p1) {
		super.div(p0, p1);
		return this;
	}

	@Override
	public Vertex2d mul(final Matrix2fc p0) {
		super.mul(p0);
		return this;
	}

	@Override
	public Vertex2d mul(final Matrix2dc p0) {
		super.mul(p0);
		return this;
	}

	@Override
	public Vertex2d mul(final Matrix2dc p0, final Vector2d p1) {
		super.mul(p0, p1);
		return this;
	}

	@Override
	public Vertex2d mul(final Matrix2fc p0, final Vector2d p1) {
		super.mul(p0, p1);
		return this;
	}

	@Override
	public Vertex2d mulTranspose(final Matrix2dc p0) {
		super.mulTranspose(p0);
		return this;
	}

	@Override
	public Vertex2d mulTranspose(final Matrix2dc p0, final Vector2d p1) {
		super.mulTranspose(p0, p1);
		return this;
	}

	@Override
	public Vertex2d mulTranspose(final Matrix2fc p0) {
		super.mulTranspose(p0);
		return this;
	}

	@Override
	public Vertex2d mulTranspose(final Matrix2fc p0, final Vector2d p1) {
		super.mulTranspose(p0, p1);
		return this;
	}

	@Override
	public Vertex2d mulPosition(final Matrix3x2dc p0) {
		super.mulPosition(p0);
		return this;
	}

	@Override
	public Vertex2d mulPosition(final Matrix3x2dc p0, final Vector2d p1) {
		super.mulPosition(p0, p1);
		return this;
	}

	@Override
	public Vertex2d mulDirection(final Matrix3x2dc p0) {
		super.mulDirection(p0);
		return this;
	}

	@Override
	public Vertex2d mulDirection(final Matrix3x2dc p0, final Vector2d p1) {
		super.mulDirection(p0, p1);
		return this;
	}

	@Override
	public Vertex2d normalize() {
		super.normalize();
		return this;
	}

	@Override
	public Vertex2d normalize(final Vector2d p0) {
		super.normalize(p0);
		return this;
	}

	@Override
	public Vertex2d normalize(final double p0) {
		super.normalize(p0);
		return this;
	}

	@Override
	public Vertex2d normalize(final double p0, final Vector2d p1) {
		super.normalize(p0, p1);
		return this;
	}

	@Override
	public Vertex2d add(final double p0, final double p1) {
		super.add(p0, p1);
		return this;
	}

	@Override
	public Vertex2d add(final double p0, final double p1, final Vector2d p2) {
		super.add(p0, p1, p2);
		return this;
	}

	@Override
	public Vertex2d add(final Vector2fc p0) {
		super.add(p0);
		return this;
	}

	@Override
	public Vertex2d add(final Vector2dc p0, final Vector2d p1) {
		super.add(p0, p1);
		return this;
	}

	@Override
	public Vertex2d add(final Vector2fc p0, final Vector2d p1) {
		super.add(p0, p1);
		return this;
	}

	@Override
	public Vertex2d zero() {
		super.zero();
		return this;
	}

	@Override
	public Vertex2d negate() {
		super.negate();
		return this;
	}

	@Override
	public Vertex2d negate(final Vector2d p0) {
		super.negate(p0);
		return this;
	}

	@Override
	public Vertex2d lerp(final Vector2dc p0, final double p1) {
		super.lerp(p0, p1);
		return this;
	}

	@Override
	public Vertex2d lerp(final Vector2dc p0, final double p1, final Vector2d p2) {
		super.lerp(p0, p1, p2);
		return this;
	}

	@Override
	public Vertex2d fma(final Vector2dc p0, final Vector2dc p1) {
		super.fma(p0, p1);
		return this;
	}

	@Override
	public Vertex2d fma(final double p0, final Vector2dc p1) {
		super.fma(p0, p1);
		return this;
	}

	@Override
	public Vertex2d fma(final Vector2dc p0, final Vector2dc p1, final Vector2d p2) {
		super.fma(p0, p1, p2);
		return this;
	}

	@Override
	public Vertex2d fma(final double p0, final Vector2dc p1, final Vector2d p2) {
		super.fma(p0, p1, p2);
		return this;
	}

	@Override
	public Vertex2d min(final Vector2dc p0) {
		super.min(p0);
		return this;
	}

	@Override
	public Vertex2d min(final Vector2dc p0, final Vector2d p1) {
		super.min(p0, p1);
		return this;
	}

	@Override
	public Vertex2d max(final Vector2dc p0) {
		super.max(p0);
		return this;
	}

	@Override
	public Vertex2d max(final Vector2dc p0, final Vector2d p1) {
		super.max(p0, p1);
		return this;
	}

	@Override
	public Vertex2d floor() {
		super.floor();
		return this;
	}

	@Override
	public Vertex2d floor(final Vector2d p0) {
		super.floor(p0);
		return this;
	}

	@Override
	public Vertex2d ceil() {
		super.ceil();
		return this;
	}

	@Override
	public Vertex2d ceil(final Vector2d p0) {
		super.ceil(p0);
		return this;
	}

	@Override
	public Vertex2d round() {
		super.round();
		return this;
	}

	@Override
	public Vertex2d round(final Vector2d p0) {
		super.round(p0);
		return this;
	}

	@Override
	public Vertex2d absolute() {
		super.absolute();
		return this;
	}

	@Override
	public Vertex2d absolute(final Vector2d p0) {
		super.absolute(p0);
		return this;
	}

	public Vertex2d() {
	}

	public Vertex2d(final double x, final double y) {
		super(x, y);
	}

	public Vertex2d(final Vector2dc other) {
		super(other);
	}

	@Override
	public Vertex2d add(final Vector2dc v) {
		super.add(v);
		return this;
	}

	@Override
	public Vertex2d sub(final Vector2dc v) {
		super.sub(v);
		return this;
	}

	@Override
	public @Owned Vertex2d clone() {
		return new Vertex2d(this);
	}

	public Vertex2d reflectAround(final Vertex2dc center) {
		this.x = 2 * center.x() - this.x;
		this.y = 2 * center.y() - this.y;
		return this;
	}

	public @Owned Vertex2d reflectedAround(final Vertex2dc center) {
		return this.clone().reflectAround(center);
	}

	public Vertex2d reflectAround(final Line2dc line) {
		final double dx = this.x - line.origin().x(), dy = this.y - line.origin().y();
		final double nx = -line.direction().y(), ny = line.direction().x();
		final double signed = dx * nx + dy * ny;
		this.x -= 2 * signed * nx;
		this.y -= 2 * signed * ny;
		return this;
	}

	public @Owned Vertex2d reflectedAround(final Line2dc line) {
		return this.clone().reflectAround(line);
	}
}

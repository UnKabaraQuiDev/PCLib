package lu.kbra.pclib.geo.math;

import java.nio.*;

import org.joml.*;
import org.joml.Vector2f;
import org.joml.Vector2fc;

import lu.kbra.pclib.geo.Owned;
import lu.kbra.pclib.geo.two.curve.Line2fc;

public class Vertex2f extends Vector2f implements Vertex2fc, Cloneable {
	@Override
	public Vertex2f getToAddress(final long address) {
		super.getToAddress(address);
		return this;
	}

	public Vertex2f() {
		super(0, 0);
	}

	public Vertex2f(final float x, final float y) {
		super(x, y);
	}

	public Vertex2f(final Vector2fc v) {
		this(v.x(), v.y());
	}

	@Override
	public Vertex2f add(final Vector2fc v) {
		super.add(v);
		return this;
	}

	@Override
	public Vertex2f sub(final Vector2fc v) {
		super.sub(v);
		return this;
	}

	@Override
	public @Owned Vertex2f clone() {
		return new Vertex2f(this);
	}

	@Override
	public Vertex2f set(final float p0) {
		super.set(p0);
		return this;
	}

	@Override
	public Vertex2f set(final float p0, final float p1) {
		super.set(p0, p1);
		return this;
	}

	@Override
	public Vertex2f set(final double p0) {
		super.set(p0);
		return this;
	}

	@Override
	public Vertex2f set(final double p0, final double p1) {
		super.set(p0, p1);
		return this;
	}

	@Override
	public Vertex2f set(final Vector2fc p0) {
		super.set(p0);
		return this;
	}

	@Override
	public Vertex2f set(final Vector2ic p0) {
		super.set(p0);
		return this;
	}

	@Override
	public Vertex2f set(final Vector2dc p0) {
		super.set(p0);
		return this;
	}

	@Override
	public Vertex2f set(final Vector3dc p0) {
		super.set(p0);
		return this;
	}

	@Override
	public Vertex2f set(final Vector3fc p0) {
		super.set(p0);
		return this;
	}

	@Override
	public Vertex2f set(final Vector3ic p0) {
		super.set(p0);
		return this;
	}

	@Override
	public Vertex2f set(final float[] p0) {
		super.set(p0);
		return this;
	}

	@Override
	public Vertex2f set(final ByteBuffer p0) {
		super.set(p0);
		return this;
	}

	@Override
	public Vertex2f set(final int p0, final ByteBuffer p1) {
		super.set(p0, p1);
		return this;
	}

	@Override
	public Vertex2f set(final FloatBuffer p0) {
		super.set(p0);
		return this;
	}

	@Override
	public Vertex2f set(final int p0, final FloatBuffer p1) {
		super.set(p0, p1);
		return this;
	}

	@Override
	public Vertex2f setFromAddress(final long p0) {
		super.setFromAddress(p0);
		return this;
	}

	@Override
	public Vertex2f setComponent(final int p0, final float p1) {
		super.setComponent(p0, p1);
		return this;
	}

	@Override
	public Vertex2f perpendicular() {
		super.perpendicular();
		return this;
	}

	@Override
	public Vertex2f sub(final float p0, final float p1) {
		super.sub(p0, p1);
		return this;
	}

	@Override
	public Vertex2f normalize() {
		super.normalize();
		return this;
	}

	@Override
	public Vertex2f normalize(final float p0) {
		super.normalize(p0);
		return this;
	}

	@Override
	public Vertex2f add(final float p0, final float p1) {
		super.add(p0, p1);
		return this;
	}

	@Override
	public Vertex2f zero() {
		super.zero();
		return this;
	}

	@Override
	public Vertex2f negate() {
		super.negate();
		return this;
	}

	@Override
	public Vertex2f mul(final float p0) {
		super.mul(p0);
		return this;
	}

	@Override
	public Vertex2f mul(final float p0, final float p1) {
		super.mul(p0, p1);
		return this;
	}

	@Override
	public Vertex2f mul(final Vector2fc p0) {
		super.mul(p0);
		return this;
	}

	@Override
	public Vertex2f div(final Vector2fc p0) {
		super.div(p0);
		return this;
	}

	@Override
	public Vertex2f div(final float p0) {
		super.div(p0);
		return this;
	}

	@Override
	public Vertex2f div(final float p0, final float p1) {
		super.div(p0, p1);
		return this;
	}

	@Override
	public Vertex2f mul(final Matrix2fc p0) {
		super.mul(p0);
		return this;
	}

	@Override
	public Vertex2f mul(final Matrix2dc p0) {
		super.mul(p0);
		return this;
	}

	@Override
	public Vertex2f mulTranspose(final Matrix2fc p0) {
		super.mulTranspose(p0);
		return this;
	}

	@Override
	public Vertex2f mulPosition(final Matrix3x2fc p0) {
		super.mulPosition(p0);
		return this;
	}

	@Override
	public Vertex2f mulDirection(final Matrix3x2fc p0) {
		super.mulDirection(p0);
		return this;
	}

	@Override
	public Vertex2f lerp(final Vector2fc p0, final float p1) {
		super.lerp(p0, p1);
		return this;
	}

	@Override
	public Vertex2f fma(final Vector2fc p0, final Vector2fc p1) {
		super.fma(p0, p1);
		return this;
	}

	@Override
	public Vertex2f fma(final float p0, final Vector2fc p1) {
		super.fma(p0, p1);
		return this;
	}

	@Override
	public Vertex2f min(final Vector2fc p0) {
		super.min(p0);
		return this;
	}

	@Override
	public Vertex2f max(final Vector2fc p0) {
		super.max(p0);
		return this;
	}

	@Override
	public Vertex2f floor() {
		super.floor();
		return this;
	}

	@Override
	public Vertex2f ceil() {
		super.ceil();
		return this;
	}

	@Override
	public Vertex2f round() {
		super.round();
		return this;
	}

	@Override
	public Vertex2f absolute() {
		super.absolute();
		return this;
	}

	public Vertex2f reflectAround(final Vertex2fc center) {
		this.x = 2 * center.x() - this.x;
		this.y = 2 * center.y() - this.y;
		return this;
	}

	public @Owned Vertex2f reflectedAround(final Vertex2fc center) {
		return this.clone().reflectAround(center);
	}

	public Vertex2f reflectAround(final Line2fc line) {
		final float dx = this.x - line.origin().x(), dy = this.y - line.origin().y();
		final float nx = -line.direction().y(), ny = line.direction().x();
		final float signed = dx * nx + dy * ny;
		this.x -= 2 * signed * nx;
		this.y -= 2 * signed * ny;
		return this;
	}

	public @Owned Vertex2f reflectedAround(final Line2fc line) {
		return this.clone().reflectAround(line);
	}
}

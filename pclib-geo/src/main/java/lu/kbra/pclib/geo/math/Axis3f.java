package lu.kbra.pclib.geo.math;

import java.nio.*;

import org.joml.*;
import org.joml.Vector3f;
import org.joml.Vector3fc;

import lu.kbra.pclib.geo.Owned;

public class Axis3f extends Vector3f implements Axis3fc, Cloneable {
	@Override
	public Axis3f getToAddress(final long address) {
		super.getToAddress(address);
		return this;
	}

	public Axis3f() {
		super(1, 0, 0);
	}

	public Axis3f(final float x, final float y, final float z) {
		super(x, y, z);
		this.validate();
	}

	public Axis3f(final Vector3fc v) {
		this(v.x(), v.y(), v.z());
	}

	private void validate() {
		if (!(Float.isFinite(this.x) && Float.isFinite(this.y) && Float.isFinite(this.z)) || this.lengthSquared() == 0) {
			throw new IllegalArgumentException("Invalid axis");
		}
		this.normalize();
	}

	public Axis3f setUnit(final Vector3fc other) {
		this.set(other);
		this.validate();
		return this;
	}

	@Override
	public @Owned Axis3f clone() {
		return new Axis3f(this);
	}

	@Override
	public Axis3f set(final Vector4dc p0) {
		super.set(p0);
		return this;
	}

	@Override
	public Axis3f set(final Vector4fc p0) {
		super.set(p0);
		return this;
	}

	@Override
	public Axis3f set(final Vector4ic p0) {
		super.set(p0);
		return this;
	}

	@Override
	public Axis3f set(final Vector3fc p0) {
		super.set(p0);
		return this;
	}

	@Override
	public Axis3f set(final Vector3dc p0) {
		super.set(p0);
		return this;
	}

	@Override
	public Axis3f set(final Vector3ic p0) {
		super.set(p0);
		return this;
	}

	@Override
	public Axis3f set(final Vector2fc p0, final float p1) {
		super.set(p0, p1);
		return this;
	}

	@Override
	public Axis3f set(final Vector2dc p0, final float p1) {
		super.set(p0, p1);
		return this;
	}

	@Override
	public Axis3f set(final Vector2ic p0, final float p1) {
		super.set(p0, p1);
		return this;
	}

	@Override
	public Axis3f set(final float p0) {
		super.set(p0);
		return this;
	}

	@Override
	public Axis3f set(final float p0, final float p1, final float p2) {
		super.set(p0, p1, p2);
		return this;
	}

	@Override
	public Axis3f set(final double p0) {
		super.set(p0);
		return this;
	}

	@Override
	public Axis3f set(final double p0, final double p1, final double p2) {
		super.set(p0, p1, p2);
		return this;
	}

	@Override
	public Axis3f set(final float[] p0) {
		super.set(p0);
		return this;
	}

	@Override
	public Axis3f set(final ByteBuffer p0) {
		super.set(p0);
		return this;
	}

	@Override
	public Axis3f set(final int p0, final ByteBuffer p1) {
		super.set(p0, p1);
		return this;
	}

	@Override
	public Axis3f set(final FloatBuffer p0) {
		super.set(p0);
		return this;
	}

	@Override
	public Axis3f set(final int p0, final FloatBuffer p1) {
		super.set(p0, p1);
		return this;
	}

	@Override
	public Axis3f setFromAddress(final long p0) {
		super.setFromAddress(p0);
		return this;
	}

	@Override
	public Axis3f setComponent(final int p0, final float p1) {
		super.setComponent(p0, p1);
		return this;
	}

	@Override
	public Axis3f sub(final Vector3fc p0) {
		super.sub(p0);
		return this;
	}

	@Override
	public Axis3f sub(final float p0, final float p1, final float p2) {
		super.sub(p0, p1, p2);
		return this;
	}

	@Override
	public Axis3f add(final Vector3fc p0) {
		super.add(p0);
		return this;
	}

	@Override
	public Axis3f add(final float p0, final float p1, final float p2) {
		super.add(p0, p1, p2);
		return this;
	}

	@Override
	public Axis3f fma(final Vector3fc p0, final Vector3fc p1) {
		super.fma(p0, p1);
		return this;
	}

	@Override
	public Axis3f fma(final float p0, final Vector3fc p1) {
		super.fma(p0, p1);
		return this;
	}

	@Override
	public Axis3f mulAdd(final Vector3fc p0, final Vector3fc p1) {
		super.mulAdd(p0, p1);
		return this;
	}

	@Override
	public Axis3f mulAdd(final float p0, final Vector3fc p1) {
		super.mulAdd(p0, p1);
		return this;
	}

	@Override
	public Axis3f mul(final Vector3fc p0) {
		super.mul(p0);
		return this;
	}

	@Override
	public Axis3f div(final Vector3fc p0) {
		super.div(p0);
		return this;
	}

	@Override
	public Axis3f mulProject(final Matrix4fc p0) {
		super.mulProject(p0);
		return this;
	}

	@Override
	public Axis3f mulProjectTranslation(final Matrix4fc p0) {
		super.mulProjectTranslation(p0);
		return this;
	}

	@Override
	public Axis3f mulProjectTranslation(final Matrix4dc p0) {
		super.mulProjectTranslation(p0);
		return this;
	}

	@Override
	public Axis3f mulProjectAffine(final Matrix4fc p0) {
		super.mulProjectAffine(p0);
		return this;
	}

	@Override
	public Axis3f mulProjectAffine(final Matrix4dc p0) {
		super.mulProjectAffine(p0);
		return this;
	}

	@Override
	public Axis3f mulProjectGeneric(final Matrix4fc p0) {
		super.mulProjectGeneric(p0);
		return this;
	}

	@Override
	public Axis3f mulProjectGeneric(final Matrix4dc p0) {
		super.mulProjectGeneric(p0);
		return this;
	}

	@Override
	public Axis3f mul(final Matrix3fc p0) {
		super.mul(p0);
		return this;
	}

	@Override
	public Axis3f mul(final Matrix3dc p0) {
		super.mul(p0);
		return this;
	}

	@Override
	public Axis3f mul(final Matrix3x2fc p0) {
		super.mul(p0);
		return this;
	}

	@Override
	public Axis3f mul(final Matrix3x2dc p0) {
		super.mul(p0);
		return this;
	}

	@Override
	public Axis3f mulTranspose(final Matrix3fc p0) {
		super.mulTranspose(p0);
		return this;
	}

	@Override
	public Axis3f mulPosition(final Matrix4fc p0) {
		super.mulPosition(p0);
		return this;
	}

	@Override
	public Axis3f mulPosition(final Matrix4dc p0) {
		super.mulPosition(p0);
		return this;
	}

	@Override
	public Axis3f mulPositionTranslation(final Matrix4x3fc p0) {
		super.mulPositionTranslation(p0);
		return this;
	}

	@Override
	public Axis3f mulPositionTranslation(final Matrix4x3dc p0) {
		super.mulPositionTranslation(p0);
		return this;
	}

	@Override
	public Axis3f mulPositionTranslation(final Matrix4fc p0) {
		super.mulPositionTranslation(p0);
		return this;
	}

	@Override
	public Axis3f mulPositionTranslation(final Matrix4dc p0) {
		super.mulPositionTranslation(p0);
		return this;
	}

	@Override
	public Axis3f mulPositionGeneric(final Matrix4fc p0) {
		super.mulPositionGeneric(p0);
		return this;
	}

	@Override
	public Axis3f mulPositionGeneric(final Matrix4dc p0) {
		super.mulPositionGeneric(p0);
		return this;
	}

	@Override
	public Axis3f mulPosition(final Matrix4x3fc p0) {
		super.mulPosition(p0);
		return this;
	}

	@Override
	public Axis3f mulPosition(final Matrix4x3dc p0) {
		super.mulPosition(p0);
		return this;
	}

	@Override
	public Axis3f mulPositionGeneric(final Matrix4x3fc p0) {
		super.mulPositionGeneric(p0);
		return this;
	}

	@Override
	public Axis3f mulPositionGeneric(final Matrix4x3dc p0) {
		super.mulPositionGeneric(p0);
		return this;
	}

	@Override
	public Axis3f mulTransposePosition(final Matrix4fc p0) {
		super.mulTransposePosition(p0);
		return this;
	}

	@Override
	public Axis3f mulTransposePosition(final Matrix4dc p0) {
		super.mulTransposePosition(p0);
		return this;
	}

	@Override
	public Axis3f mulTransposePositionGeneric(final Matrix4dc p0) {
		super.mulTransposePositionGeneric(p0);
		return this;
	}

	@Override
	public Axis3f mulDirection(final Matrix4dc p0) {
		super.mulDirection(p0);
		return this;
	}

	@Override
	public Axis3f mulDirection(final Matrix4fc p0) {
		super.mulDirection(p0);
		return this;
	}

	@Override
	public Axis3f mulDirection(final Matrix4x3fc p0) {
		super.mulDirection(p0);
		return this;
	}

	@Override
	public Axis3f mulDirection(final Matrix4x3dc p0) {
		super.mulDirection(p0);
		return this;
	}

	@Override
	public Axis3f mulTransposeDirection(final Matrix4fc p0) {
		super.mulTransposeDirection(p0);
		return this;
	}

	@Override
	public Axis3f mul(final float p0) {
		super.mul(p0);
		return this;
	}

	@Override
	public Axis3f mul(final float p0, final float p1, final float p2) {
		super.mul(p0, p1, p2);
		return this;
	}

	@Override
	public Axis3f div(final float p0) {
		super.div(p0);
		return this;
	}

	@Override
	public Axis3f div(final float p0, final float p1, final float p2) {
		super.div(p0, p1, p2);
		return this;
	}

	@Override
	public Axis3f rotate(final Quaternionfc p0) {
		super.rotate(p0);
		return this;
	}

	@Override
	public Axis3f rotateAxis(final float p0, final float p1, final float p2, final float p3) {
		super.rotateAxis(p0, p1, p2, p3);
		return this;
	}

	@Override
	public Axis3f rotateX(final float p0) {
		super.rotateX(p0);
		return this;
	}

	@Override
	public Axis3f rotateY(final float p0) {
		super.rotateY(p0);
		return this;
	}

	@Override
	public Axis3f rotateZ(final float p0) {
		super.rotateZ(p0);
		return this;
	}

	@Override
	public Axis3f normalize() {
		super.normalize();
		return this;
	}

	@Override
	public Axis3f normalize(final float p0) {
		super.normalize(p0);
		return this;
	}

	@Override
	public Axis3f cross(final Vector3fc p0) {
		super.cross(p0);
		return this;
	}

	@Override
	public Axis3f cross(final float p0, final float p1, final float p2) {
		super.cross(p0, p1, p2);
		return this;
	}

	@Override
	public Axis3f min(final Vector3fc p0) {
		super.min(p0);
		return this;
	}

	@Override
	public Axis3f max(final Vector3fc p0) {
		super.max(p0);
		return this;
	}

	@Override
	public Axis3f zero() {
		super.zero();
		return this;
	}

	@Override
	public Axis3f negate() {
		super.negate();
		return this;
	}

	@Override
	public Axis3f absolute() {
		super.absolute();
		return this;
	}

	@Override
	public Axis3f reflect(final Vector3fc p0) {
		super.reflect(p0);
		return this;
	}

	@Override
	public Axis3f reflect(final float p0, final float p1, final float p2) {
		super.reflect(p0, p1, p2);
		return this;
	}

	@Override
	public Axis3f half(final Vector3fc p0) {
		super.half(p0);
		return this;
	}

	@Override
	public Axis3f half(final float p0, final float p1, final float p2) {
		super.half(p0, p1, p2);
		return this;
	}

	@Override
	public Axis3f lerp(final Vector3fc p0, final float p1) {
		super.lerp(p0, p1);
		return this;
	}

	@Override
	public Axis3f orthogonalize(final Vector3fc p0) {
		super.orthogonalize(p0);
		return this;
	}

	@Override
	public Axis3f orthogonalizeUnit(final Vector3fc p0) {
		super.orthogonalizeUnit(p0);
		return this;
	}

	@Override
	public Axis3f floor() {
		super.floor();
		return this;
	}

	@Override
	public Axis3f ceil() {
		super.ceil();
		return this;
	}

	@Override
	public Axis3f round() {
		super.round();
		return this;
	}
}

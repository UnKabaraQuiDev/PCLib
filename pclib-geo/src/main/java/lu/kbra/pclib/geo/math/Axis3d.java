package lu.kbra.pclib.geo.math;

import java.nio.ByteBuffer;
import java.nio.DoubleBuffer;

import org.joml.Matrix3dc;
import org.joml.Matrix3fc;
import org.joml.Matrix3x2dc;
import org.joml.Matrix3x2fc;
import org.joml.Matrix4dc;
import org.joml.Matrix4fc;
import org.joml.Matrix4x3dc;
import org.joml.Matrix4x3fc;
import org.joml.Quaterniondc;
import org.joml.Vector2dc;
import org.joml.Vector2fc;
import org.joml.Vector2ic;
import org.joml.Vector3d;
import org.joml.Vector3dc;
import org.joml.Vector3fc;
import org.joml.Vector3ic;
import org.joml.Vector4dc;
import org.joml.Vector4fc;
import org.joml.Vector4ic;

import lu.kbra.pclib.geo.Owned;

/** Unit direction on construction; inherited JOML mutators can change its length. */
public class Axis3d extends Vector3d implements Axis3dc, Cloneable {
	@Override
	public Axis3d getToAddress(final long address) {
		super.getToAddress(address);
		return this;
	}

	@Override
	public Axis3d set(final Vector4dc p0) {
		super.set(p0);
		return this;
	}

	@Override
	public Axis3d set(final Vector4fc p0) {
		super.set(p0);
		return this;
	}

	@Override
	public Axis3d set(final Vector4ic p0) {
		super.set(p0);
		return this;
	}

	@Override
	public Axis3d set(final Vector3dc p0) {
		super.set(p0);
		return this;
	}

	@Override
	public Axis3d set(final Vector3ic p0) {
		super.set(p0);
		return this;
	}

	@Override
	public Axis3d set(final Vector2dc p0, final double p1) {
		super.set(p0, p1);
		return this;
	}

	@Override
	public Axis3d set(final Vector2ic p0, final double p1) {
		super.set(p0, p1);
		return this;
	}

	@Override
	public Axis3d set(final Vector3fc p0) {
		super.set(p0);
		return this;
	}

	@Override
	public Axis3d set(final Vector2fc p0, final double p1) {
		super.set(p0, p1);
		return this;
	}

	@Override
	public Axis3d set(final double p0) {
		super.set(p0);
		return this;
	}

	@Override
	public Axis3d set(final double p0, final double p1, final double p2) {
		super.set(p0, p1, p2);
		return this;
	}

	@Override
	public Axis3d set(final double[] p0) {
		super.set(p0);
		return this;
	}

	@Override
	public Axis3d set(final float[] p0) {
		super.set(p0);
		return this;
	}

	@Override
	public Axis3d set(final ByteBuffer p0) {
		super.set(p0);
		return this;
	}

	@Override
	public Axis3d set(final int p0, final ByteBuffer p1) {
		super.set(p0, p1);
		return this;
	}

	@Override
	public Axis3d set(final DoubleBuffer p0) {
		super.set(p0);
		return this;
	}

	@Override
	public Axis3d set(final int p0, final DoubleBuffer p1) {
		super.set(p0, p1);
		return this;
	}

	@Override
	public Axis3d setFromAddress(final long p0) {
		super.setFromAddress(p0);
		return this;
	}

	@Override
	public Axis3d setComponent(final int p0, final double p1) {
		super.setComponent(p0, p1);
		return this;
	}

	@Override
	public Axis3d sub(final Vector3dc p0) {
		super.sub(p0);
		return this;
	}

	@Override
	public Axis3d sub(final Vector3dc p0, final Vector3d p1) {
		super.sub(p0, p1);
		return this;
	}

	@Override
	public Axis3d sub(final Vector3fc p0) {
		super.sub(p0);
		return this;
	}

	@Override
	public Axis3d sub(final Vector3fc p0, final Vector3d p1) {
		super.sub(p0, p1);
		return this;
	}

	@Override
	public Axis3d sub(final double p0, final double p1, final double p2) {
		super.sub(p0, p1, p2);
		return this;
	}

	@Override
	public Axis3d sub(final double p0, final double p1, final double p2, final Vector3d p3) {
		super.sub(p0, p1, p2, p3);
		return this;
	}

	@Override
	public Axis3d add(final Vector3dc p0) {
		super.add(p0);
		return this;
	}

	@Override
	public Axis3d add(final Vector3dc p0, final Vector3d p1) {
		super.add(p0, p1);
		return this;
	}

	@Override
	public Axis3d add(final Vector3fc p0) {
		super.add(p0);
		return this;
	}

	@Override
	public Axis3d add(final Vector3fc p0, final Vector3d p1) {
		super.add(p0, p1);
		return this;
	}

	@Override
	public Axis3d add(final double p0, final double p1, final double p2) {
		super.add(p0, p1, p2);
		return this;
	}

	@Override
	public Axis3d add(final double p0, final double p1, final double p2, final Vector3d p3) {
		super.add(p0, p1, p2, p3);
		return this;
	}

	@Override
	public Axis3d fma(final Vector3dc p0, final Vector3dc p1) {
		super.fma(p0, p1);
		return this;
	}

	@Override
	public Axis3d fma(final double p0, final Vector3dc p1) {
		super.fma(p0, p1);
		return this;
	}

	@Override
	public Axis3d fma(final Vector3fc p0, final Vector3fc p1) {
		super.fma(p0, p1);
		return this;
	}

	@Override
	public Axis3d fma(final Vector3fc p0, final Vector3fc p1, final Vector3d p2) {
		super.fma(p0, p1, p2);
		return this;
	}

	@Override
	public Axis3d fma(final double p0, final Vector3fc p1) {
		super.fma(p0, p1);
		return this;
	}

	@Override
	public Axis3d fma(final Vector3dc p0, final Vector3dc p1, final Vector3d p2) {
		super.fma(p0, p1, p2);
		return this;
	}

	@Override
	public Axis3d fma(final double p0, final Vector3dc p1, final Vector3d p2) {
		super.fma(p0, p1, p2);
		return this;
	}

	@Override
	public Axis3d fma(final Vector3dc p0, final Vector3fc p1, final Vector3d p2) {
		super.fma(p0, p1, p2);
		return this;
	}

	@Override
	public Axis3d fma(final double p0, final Vector3fc p1, final Vector3d p2) {
		super.fma(p0, p1, p2);
		return this;
	}

	@Override
	public Axis3d mulAdd(final Vector3dc p0, final Vector3dc p1) {
		super.mulAdd(p0, p1);
		return this;
	}

	@Override
	public Axis3d mulAdd(final double p0, final Vector3dc p1) {
		super.mulAdd(p0, p1);
		return this;
	}

	@Override
	public Axis3d mulAdd(final Vector3dc p0, final Vector3dc p1, final Vector3d p2) {
		super.mulAdd(p0, p1, p2);
		return this;
	}

	@Override
	public Axis3d mulAdd(final double p0, final Vector3dc p1, final Vector3d p2) {
		super.mulAdd(p0, p1, p2);
		return this;
	}

	@Override
	public Axis3d mulAdd(final Vector3fc p0, final Vector3dc p1, final Vector3d p2) {
		super.mulAdd(p0, p1, p2);
		return this;
	}

	@Override
	public Axis3d mul(final Vector3dc p0) {
		super.mul(p0);
		return this;
	}

	@Override
	public Axis3d mul(final Vector3fc p0) {
		super.mul(p0);
		return this;
	}

	@Override
	public Axis3d mul(final Vector3fc p0, final Vector3d p1) {
		super.mul(p0, p1);
		return this;
	}

	@Override
	public Axis3d mul(final Vector3dc p0, final Vector3d p1) {
		super.mul(p0, p1);
		return this;
	}

	@Override
	public Axis3d div(final Vector3dc p0) {
		super.div(p0);
		return this;
	}

	@Override
	public Axis3d div(final Vector3fc p0) {
		super.div(p0);
		return this;
	}

	@Override
	public Axis3d div(final Vector3fc p0, final Vector3d p1) {
		super.div(p0, p1);
		return this;
	}

	@Override
	public Axis3d div(final Vector3dc p0, final Vector3d p1) {
		super.div(p0, p1);
		return this;
	}

	@Override
	public Axis3d mulProject(final Matrix4dc p0, final double p1, final Vector3d p2) {
		super.mulProject(p0, p1, p2);
		return this;
	}

	@Override
	public Axis3d mulProject(final Matrix4dc p0) {
		super.mulProject(p0);
		return this;
	}

	@Override
	public Axis3d mulProject(final Matrix4dc p0, final Vector3d p1) {
		super.mulProject(p0, p1);
		return this;
	}

	@Override
	public Axis3d mulProject(final Matrix4fc p0) {
		super.mulProject(p0);
		return this;
	}

	@Override
	public Axis3d mulProject(final Matrix4fc p0, final Vector3d p1) {
		super.mulProject(p0, p1);
		return this;
	}

	@Override
	public Axis3d mulProjectTranslation(final Matrix4dc p0) {
		super.mulProjectTranslation(p0);
		return this;
	}

	@Override
	public Axis3d mulProjectTranslation(final Matrix4dc p0, final Vector3d p1) {
		super.mulProjectTranslation(p0, p1);
		return this;
	}

	@Override
	public Axis3d mulProjectTranslation(final Matrix4fc p0) {
		super.mulProjectTranslation(p0);
		return this;
	}

	@Override
	public Axis3d mulProjectTranslation(final Matrix4fc p0, final Vector3d p1) {
		super.mulProjectTranslation(p0, p1);
		return this;
	}

	@Override
	public Axis3d mulProjectAffine(final Matrix4dc p0) {
		super.mulProjectAffine(p0);
		return this;
	}

	@Override
	public Axis3d mulProjectAffine(final Matrix4dc p0, final Vector3d p1) {
		super.mulProjectAffine(p0, p1);
		return this;
	}

	@Override
	public Axis3d mulProjectAffine(final Matrix4fc p0) {
		super.mulProjectAffine(p0);
		return this;
	}

	@Override
	public Axis3d mulProjectAffine(final Matrix4fc p0, final Vector3d p1) {
		super.mulProjectAffine(p0, p1);
		return this;
	}

	@Override
	public Axis3d mulProjectGeneric(final Matrix4dc p0) {
		super.mulProjectGeneric(p0);
		return this;
	}

	@Override
	public Axis3d mulProjectGeneric(final Matrix4dc p0, final Vector3d p1) {
		super.mulProjectGeneric(p0, p1);
		return this;
	}

	@Override
	public Axis3d mulProjectGeneric(final Matrix4fc p0) {
		super.mulProjectGeneric(p0);
		return this;
	}

	@Override
	public Axis3d mulProjectGeneric(final Matrix4fc p0, final Vector3d p1) {
		super.mulProjectGeneric(p0, p1);
		return this;
	}

	@Override
	public Axis3d mul(final Matrix3fc p0) {
		super.mul(p0);
		return this;
	}

	@Override
	public Axis3d mul(final Matrix3dc p0) {
		super.mul(p0);
		return this;
	}

	@Override
	public Axis3d mul(final Matrix3dc p0, final Vector3d p1) {
		super.mul(p0, p1);
		return this;
	}

	@Override
	public Axis3d mul(final Matrix3fc p0, final Vector3d p1) {
		super.mul(p0, p1);
		return this;
	}

	@Override
	public Axis3d mul(final Matrix3x2dc p0) {
		super.mul(p0);
		return this;
	}

	@Override
	public Axis3d mul(final Matrix3x2dc p0, final Vector3d p1) {
		super.mul(p0, p1);
		return this;
	}

	@Override
	public Axis3d mul(final Matrix3x2fc p0) {
		super.mul(p0);
		return this;
	}

	@Override
	public Axis3d mul(final Matrix3x2fc p0, final Vector3d p1) {
		super.mul(p0, p1);
		return this;
	}

	@Override
	public Axis3d mulTranspose(final Matrix3dc p0) {
		super.mulTranspose(p0);
		return this;
	}

	@Override
	public Axis3d mulTranspose(final Matrix3dc p0, final Vector3d p1) {
		super.mulTranspose(p0, p1);
		return this;
	}

	@Override
	public Axis3d mulTranspose(final Matrix3fc p0) {
		super.mulTranspose(p0);
		return this;
	}

	@Override
	public Axis3d mulTranspose(final Matrix3fc p0, final Vector3d p1) {
		super.mulTranspose(p0, p1);
		return this;
	}

	@Override
	public Axis3d mulPosition(final Matrix4dc p0) {
		super.mulPosition(p0);
		return this;
	}

	@Override
	public Axis3d mulPosition(final Matrix4dc p0, final Vector3d p1) {
		super.mulPosition(p0, p1);
		return this;
	}

	@Override
	public Axis3d mulPosition(final Matrix4fc p0, final Vector3d p1) {
		super.mulPosition(p0, p1);
		return this;
	}

	@Override
	public Axis3d mulPosition(final Matrix4fc p0) {
		super.mulPosition(p0);
		return this;
	}

	@Override
	public Axis3d mulPositionTranslation(final Matrix4dc p0) {
		super.mulPositionTranslation(p0);
		return this;
	}

	@Override
	public Axis3d mulPositionTranslation(final Matrix4dc p0, final Vector3d p1) {
		super.mulPositionTranslation(p0, p1);
		return this;
	}

	@Override
	public Axis3d mulPositionTranslation(final Matrix4fc p0) {
		super.mulPositionTranslation(p0);
		return this;
	}

	@Override
	public Axis3d mulPositionTranslation(final Matrix4fc p0, final Vector3d p1) {
		super.mulPositionTranslation(p0, p1);
		return this;
	}

	@Override
	public Axis3d mulPositionGeneric(final Matrix4dc p0) {
		super.mulPositionGeneric(p0);
		return this;
	}

	@Override
	public Axis3d mulPositionGeneric(final Matrix4dc p0, final Vector3d p1) {
		super.mulPositionGeneric(p0, p1);
		return this;
	}

	@Override
	public Axis3d mulPositionGeneric(final Matrix4fc p0) {
		super.mulPositionGeneric(p0);
		return this;
	}

	@Override
	public Axis3d mulPositionGeneric(final Matrix4fc p0, final Vector3d p1) {
		super.mulPositionGeneric(p0, p1);
		return this;
	}

	@Override
	public Axis3d mulPosition(final Matrix4x3dc p0) {
		super.mulPosition(p0);
		return this;
	}

	@Override
	public Axis3d mulPositionTranslation(final Matrix4x3dc p0, final Vector3d p1) {
		super.mulPositionTranslation(p0, p1);
		return this;
	}

	@Override
	public Axis3d mulPositionTranslation(final Matrix4x3dc p0) {
		super.mulPositionTranslation(p0);
		return this;
	}

	@Override
	public Axis3d mulPositionGeneric(final Matrix4x3dc p0, final Vector3d p1) {
		super.mulPositionGeneric(p0, p1);
		return this;
	}

	@Override
	public Axis3d mulPositionGeneric(final Matrix4x3dc p0) {
		super.mulPositionGeneric(p0);
		return this;
	}

	@Override
	public Axis3d mulPosition(final Matrix4x3fc p0) {
		super.mulPosition(p0);
		return this;
	}

	@Override
	public Axis3d mulPositionTranslation(final Matrix4x3fc p0) {
		super.mulPositionTranslation(p0);
		return this;
	}

	@Override
	public Axis3d mulPositionTranslation(final Matrix4x3fc p0, final Vector3d p1) {
		super.mulPositionTranslation(p0, p1);
		return this;
	}

	@Override
	public Axis3d mulPositionGeneric(final Matrix4x3fc p0) {
		super.mulPositionGeneric(p0);
		return this;
	}

	@Override
	public Axis3d mulPositionGeneric(final Matrix4x3fc p0, final Vector3d p1) {
		super.mulPositionGeneric(p0, p1);
		return this;
	}

	@Override
	public Axis3d mulPosition(final Matrix4x3dc p0, final Vector3d p1) {
		super.mulPosition(p0, p1);
		return this;
	}

	@Override
	public Axis3d mulPosition(final Matrix4x3fc p0, final Vector3d p1) {
		super.mulPosition(p0, p1);
		return this;
	}

	@Override
	public Axis3d mulTransposePosition(final Matrix4dc p0) {
		super.mulTransposePosition(p0);
		return this;
	}

	@Override
	public Axis3d mulTransposePosition(final Matrix4dc p0, final Vector3d p1) {
		super.mulTransposePosition(p0, p1);
		return this;
	}

	@Override
	public Axis3d mulTransposePosition(final Matrix4fc p0) {
		super.mulTransposePosition(p0);
		return this;
	}

	@Override
	public Axis3d mulTransposePosition(final Matrix4fc p0, final Vector3d p1) {
		super.mulTransposePosition(p0, p1);
		return this;
	}

	@Override
	public Axis3d mulDirection(final Matrix4fc p0) {
		super.mulDirection(p0);
		return this;
	}

	@Override
	public Axis3d mulDirection(final Matrix4dc p0) {
		super.mulDirection(p0);
		return this;
	}

	@Override
	public Axis3d mulDirection(final Matrix4x3dc p0) {
		super.mulDirection(p0);
		return this;
	}

	@Override
	public Axis3d mulDirection(final Matrix4x3fc p0) {
		super.mulDirection(p0);
		return this;
	}

	@Override
	public Axis3d mulDirection(final Matrix4dc p0, final Vector3d p1) {
		super.mulDirection(p0, p1);
		return this;
	}

	@Override
	public Axis3d mulDirection(final Matrix4fc p0, final Vector3d p1) {
		super.mulDirection(p0, p1);
		return this;
	}

	@Override
	public Axis3d mulDirection(final Matrix4x3dc p0, final Vector3d p1) {
		super.mulDirection(p0, p1);
		return this;
	}

	@Override
	public Axis3d mulDirection(final Matrix4x3fc p0, final Vector3d p1) {
		super.mulDirection(p0, p1);
		return this;
	}

	@Override
	public Axis3d mulTransposeDirection(final Matrix4dc p0) {
		super.mulTransposeDirection(p0);
		return this;
	}

	@Override
	public Axis3d mulTransposeDirection(final Matrix4dc p0, final Vector3d p1) {
		super.mulTransposeDirection(p0, p1);
		return this;
	}

	@Override
	public Axis3d mulTransposeDirection(final Matrix4fc p0) {
		super.mulTransposeDirection(p0);
		return this;
	}

	@Override
	public Axis3d mulTransposeDirection(final Matrix4fc p0, final Vector3d p1) {
		super.mulTransposeDirection(p0, p1);
		return this;
	}

	@Override
	public Axis3d mul(final double p0) {
		super.mul(p0);
		return this;
	}

	@Override
	public Axis3d mul(final double p0, final Vector3d p1) {
		super.mul(p0, p1);
		return this;
	}

	@Override
	public Axis3d mul(final double p0, final double p1, final double p2) {
		super.mul(p0, p1, p2);
		return this;
	}

	@Override
	public Axis3d mul(final double p0, final double p1, final double p2, final Vector3d p3) {
		super.mul(p0, p1, p2, p3);
		return this;
	}

	@Override
	public Axis3d rotate(final Quaterniondc p0) {
		super.rotate(p0);
		return this;
	}

	@Override
	public Axis3d rotate(final Quaterniondc p0, final Vector3d p1) {
		super.rotate(p0, p1);
		return this;
	}

	@Override
	public Axis3d rotateAxis(final double p0, final double p1, final double p2, final double p3) {
		super.rotateAxis(p0, p1, p2, p3);
		return this;
	}

	@Override
	public Axis3d rotateAxis(final double p0, final double p1, final double p2, final double p3, final Vector3d p4) {
		super.rotateAxis(p0, p1, p2, p3, p4);
		return this;
	}

	@Override
	public Axis3d rotateX(final double p0) {
		super.rotateX(p0);
		return this;
	}

	@Override
	public Axis3d rotateX(final double p0, final Vector3d p1) {
		super.rotateX(p0, p1);
		return this;
	}

	@Override
	public Axis3d rotateY(final double p0) {
		super.rotateY(p0);
		return this;
	}

	@Override
	public Axis3d rotateY(final double p0, final Vector3d p1) {
		super.rotateY(p0, p1);
		return this;
	}

	@Override
	public Axis3d rotateZ(final double p0) {
		super.rotateZ(p0);
		return this;
	}

	@Override
	public Axis3d rotateZ(final double p0, final Vector3d p1) {
		super.rotateZ(p0, p1);
		return this;
	}

	@Override
	public Axis3d div(final double p0) {
		super.div(p0);
		return this;
	}

	@Override
	public Axis3d div(final double p0, final Vector3d p1) {
		super.div(p0, p1);
		return this;
	}

	@Override
	public Axis3d div(final double p0, final double p1, final double p2) {
		super.div(p0, p1, p2);
		return this;
	}

	@Override
	public Axis3d div(final double p0, final double p1, final double p2, final Vector3d p3) {
		super.div(p0, p1, p2, p3);
		return this;
	}

	@Override
	public Axis3d normalize() {
		super.normalize();
		return this;
	}

	@Override
	public Axis3d normalize(final Vector3d p0) {
		super.normalize(p0);
		return this;
	}

	@Override
	public Axis3d normalize(final double p0) {
		super.normalize(p0);
		return this;
	}

	@Override
	public Axis3d normalize(final double p0, final Vector3d p1) {
		super.normalize(p0, p1);
		return this;
	}

	@Override
	public Axis3d cross(final Vector3dc p0) {
		super.cross(p0);
		return this;
	}

	@Override
	public Axis3d cross(final double p0, final double p1, final double p2) {
		super.cross(p0, p1, p2);
		return this;
	}

	@Override
	public Axis3d cross(final Vector3dc p0, final Vector3d p1) {
		super.cross(p0, p1);
		return this;
	}

	@Override
	public Axis3d cross(final double p0, final double p1, final double p2, final Vector3d p3) {
		super.cross(p0, p1, p2, p3);
		return this;
	}

	@Override
	public Axis3d min(final Vector3dc p0) {
		super.min(p0);
		return this;
	}

	@Override
	public Axis3d min(final Vector3dc p0, final Vector3d p1) {
		super.min(p0, p1);
		return this;
	}

	@Override
	public Axis3d max(final Vector3dc p0) {
		super.max(p0);
		return this;
	}

	@Override
	public Axis3d max(final Vector3dc p0, final Vector3d p1) {
		super.max(p0, p1);
		return this;
	}

	@Override
	public Axis3d zero() {
		super.zero();
		return this;
	}

	@Override
	public Axis3d negate() {
		super.negate();
		return this;
	}

	@Override
	public Axis3d negate(final Vector3d p0) {
		super.negate(p0);
		return this;
	}

	@Override
	public Axis3d absolute() {
		super.absolute();
		return this;
	}

	@Override
	public Axis3d absolute(final Vector3d p0) {
		super.absolute(p0);
		return this;
	}

	@Override
	public Axis3d reflect(final Vector3dc p0) {
		super.reflect(p0);
		return this;
	}

	@Override
	public Axis3d reflect(final double p0, final double p1, final double p2) {
		super.reflect(p0, p1, p2);
		return this;
	}

	@Override
	public Axis3d reflect(final Vector3dc p0, final Vector3d p1) {
		super.reflect(p0, p1);
		return this;
	}

	@Override
	public Axis3d reflect(final double p0, final double p1, final double p2, final Vector3d p3) {
		super.reflect(p0, p1, p2, p3);
		return this;
	}

	@Override
	public Axis3d half(final Vector3dc p0) {
		super.half(p0);
		return this;
	}

	@Override
	public Axis3d half(final double p0, final double p1, final double p2) {
		super.half(p0, p1, p2);
		return this;
	}

	@Override
	public Axis3d half(final Vector3dc p0, final Vector3d p1) {
		super.half(p0, p1);
		return this;
	}

	@Override
	public Axis3d half(final double p0, final double p1, final double p2, final Vector3d p3) {
		super.half(p0, p1, p2, p3);
		return this;
	}

	@Override
	public Axis3d smoothStep(final Vector3dc p0, final double p1, final Vector3d p2) {
		super.smoothStep(p0, p1, p2);
		return this;
	}

	@Override
	public Axis3d hermite(final Vector3dc p0, final Vector3dc p1, final Vector3dc p2, final double p3, final Vector3d p4) {
		super.hermite(p0, p1, p2, p3, p4);
		return this;
	}

	@Override
	public Axis3d lerp(final Vector3dc p0, final double p1) {
		super.lerp(p0, p1);
		return this;
	}

	@Override
	public Axis3d lerp(final Vector3dc p0, final double p1, final Vector3d p2) {
		super.lerp(p0, p1, p2);
		return this;
	}

	@Override
	public Axis3d get(final Vector3d p0) {
		super.get(p0);
		return this;
	}

	@Override
	public Axis3d orthogonalize(final Vector3dc p0, final Vector3d p1) {
		super.orthogonalize(p0, p1);
		return this;
	}

	@Override
	public Axis3d orthogonalize(final Vector3dc p0) {
		super.orthogonalize(p0);
		return this;
	}

	@Override
	public Axis3d orthogonalizeUnit(final Vector3dc p0, final Vector3d p1) {
		super.orthogonalizeUnit(p0, p1);
		return this;
	}

	@Override
	public Axis3d orthogonalizeUnit(final Vector3dc p0) {
		super.orthogonalizeUnit(p0);
		return this;
	}

	@Override
	public Axis3d floor() {
		super.floor();
		return this;
	}

	@Override
	public Axis3d floor(final Vector3d p0) {
		super.floor(p0);
		return this;
	}

	@Override
	public Axis3d ceil() {
		super.ceil();
		return this;
	}

	@Override
	public Axis3d ceil(final Vector3d p0) {
		super.ceil(p0);
		return this;
	}

	@Override
	public Axis3d round() {
		super.round();
		return this;
	}

	@Override
	public Axis3d round(final Vector3d p0) {
		super.round(p0);
		return this;
	}

	public Axis3d() {
		super(1, 0, 0);
	}

	public Axis3d(final double x, final double y, final double z) {
		super(x, y, z);
		this.normalizeChecked();
	}

	public Axis3d(final Vector3dc v) {
		this(v.x(), v.y(), v.z());
	}

	private void normalizeChecked() {
		if (!this.isFinite() || !(this.lengthSquared() > 0)) {
			throw new IllegalArgumentException("Axis must be finite and nonzero");
		}
		this.normalize();
	}

	public Axis3d setUnit(final Vector3dc source) {
		this.set(source);
		this.normalizeChecked();
		return this;
	}

	@Override
	public @Owned Axis3d clone() {
		return new Axis3d(this);
	}
}

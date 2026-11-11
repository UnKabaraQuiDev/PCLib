package lu.kbra.pclib.geo.math;

import java.nio.*;

import org.joml.*;
import org.joml.Vector3fc;

import lu.kbra.pclib.geo.Geometry3fc;
import lu.kbra.pclib.geo.GeometryKind;

public interface Vertex3fc extends Vector3fc, Geometry3fc {
	@Override
	Vertex3fc getToAddress(long address);

	default Vertex3f sub(final Vector3fc p0, final Vertex3f p1) {
		sub(p0, (Vector3f) p1);
		return p1;
	}

	default Vertex3f sub(final float p0, final float p1, final float p2, final Vertex3f p3) {
		sub(p0, p1, p2, (Vector3f) p3);
		return p3;
	}

	default Vertex3f add(final Vector3fc p0, final Vertex3f p1) {
		add(p0, (Vector3f) p1);
		return p1;
	}

	default Vertex3f add(final float p0, final float p1, final float p2, final Vertex3f p3) {
		add(p0, p1, p2, (Vector3f) p3);
		return p3;
	}

	default Vertex3f fma(final Vector3fc p0, final Vector3fc p1, final Vertex3f p2) {
		fma(p0, p1, (Vector3f) p2);
		return p2;
	}

	default Vertex3f fma(final float p0, final Vector3fc p1, final Vertex3f p2) {
		fma(p0, p1, (Vector3f) p2);
		return p2;
	}

	default Vertex3f mulAdd(final Vector3fc p0, final Vector3fc p1, final Vertex3f p2) {
		mulAdd(p0, p1, (Vector3f) p2);
		return p2;
	}

	default Vertex3f mulAdd(final float p0, final Vector3fc p1, final Vertex3f p2) {
		mulAdd(p0, p1, (Vector3f) p2);
		return p2;
	}

	default Vertex3f mul(final Vector3fc p0, final Vertex3f p1) {
		mul(p0, (Vector3f) p1);
		return p1;
	}

	default Vertex3f div(final Vector3fc p0, final Vertex3f p1) {
		div(p0, (Vector3f) p1);
		return p1;
	}

	default Vertex3f mulProject(final Matrix4fc p0, final Vertex3f p1) {
		mulProject(p0, (Vector3f) p1);
		return p1;
	}

	default Vertex3f mulProject(final Matrix4fc p0, final float p1, final Vertex3f p2) {
		mulProject(p0, p1, (Vector3f) p2);
		return p2;
	}

	default Vertex3f mulProjectTranslation(final Matrix4fc p0, final Vertex3f p1) {
		mulProjectTranslation(p0, (Vector3f) p1);
		return p1;
	}

	default Vertex3f mulProjectTranslation(final Matrix4dc p0, final Vertex3f p1) {
		mulProjectTranslation(p0, (Vector3f) p1);
		return p1;
	}

	default Vertex3f mulProjectAffine(final Matrix4fc p0, final Vertex3f p1) {
		mulProjectAffine(p0, (Vector3f) p1);
		return p1;
	}

	default Vertex3f mulProjectAffine(final Matrix4dc p0, final Vertex3f p1) {
		mulProjectAffine(p0, (Vector3f) p1);
		return p1;
	}

	default Vertex3f mulProjectGeneric(final Matrix4fc p0, final Vertex3f p1) {
		mulProjectGeneric(p0, (Vector3f) p1);
		return p1;
	}

	default Vertex3f mulProjectGeneric(final Matrix4dc p0, final Vertex3f p1) {
		mulProjectGeneric(p0, (Vector3f) p1);
		return p1;
	}

	default Vertex3f mul(final Matrix3fc p0, final Vertex3f p1) {
		mul(p0, (Vector3f) p1);
		return p1;
	}

	default Vertex3f mul(final Matrix3dc p0, final Vertex3f p1) {
		mul(p0, (Vector3f) p1);
		return p1;
	}

	default Vertex3f mul(final Matrix3x2fc p0, final Vertex3f p1) {
		mul(p0, (Vector3f) p1);
		return p1;
	}

	default Vertex3f mul(final Matrix3x2dc p0, final Vertex3f p1) {
		mul(p0, (Vector3f) p1);
		return p1;
	}

	default Vertex3f mulTranspose(final Matrix3fc p0, final Vertex3f p1) {
		mulTranspose(p0, (Vector3f) p1);
		return p1;
	}

	default Vertex3f mulPosition(final Matrix4fc p0, final Vertex3f p1) {
		mulPosition(p0, (Vector3f) p1);
		return p1;
	}

	default Vertex3f mulPosition(final Matrix4dc p0, final Vertex3f p1) {
		mulPosition(p0, (Vector3f) p1);
		return p1;
	}

	default Vertex3f mulPosition(final Matrix4x3fc p0, final Vertex3f p1) {
		mulPosition(p0, (Vector3f) p1);
		return p1;
	}

	default Vertex3f mulPosition(final Matrix4x3dc p0, final Vertex3f p1) {
		mulPosition(p0, (Vector3f) p1);
		return p1;
	}

	default Vertex3f mulPositionGeneric(final Matrix4fc p0, final Vertex3f p1) {
		mulPositionGeneric(p0, (Vector3f) p1);
		return p1;
	}

	default Vertex3f mulPositionGeneric(final Matrix4dc p0, final Vertex3f p1) {
		mulPositionGeneric(p0, (Vector3f) p1);
		return p1;
	}

	default Vertex3f mulPositionTranslation(final Matrix4fc p0, final Vertex3f p1) {
		mulPositionTranslation(p0, (Vector3f) p1);
		return p1;
	}

	default Vertex3f mulPositionTranslation(final Matrix4dc p0, final Vertex3f p1) {
		mulPositionTranslation(p0, (Vector3f) p1);
		return p1;
	}

	default Vertex3f mulPositionTranslation(final Matrix4x3fc p0, final Vertex3f p1) {
		mulPositionTranslation(p0, (Vector3f) p1);
		return p1;
	}

	default Vertex3f mulPositionTranslation(final Matrix4x3dc p0, final Vertex3f p1) {
		mulPositionTranslation(p0, (Vector3f) p1);
		return p1;
	}

	default Vertex3f mulTransposePosition(final Matrix4fc p0, final Vertex3f p1) {
		mulTransposePosition(p0, (Vector3f) p1);
		return p1;
	}

	default Vertex3f mulTransposePosition(final Matrix4dc p0, final Vertex3f p1) {
		mulTransposePosition(p0, (Vector3f) p1);
		return p1;
	}

	default Vertex3f mulDirection(final Matrix4dc p0, final Vertex3f p1) {
		mulDirection(p0, (Vector3f) p1);
		return p1;
	}

	default Vertex3f mulDirection(final Matrix4fc p0, final Vertex3f p1) {
		mulDirection(p0, (Vector3f) p1);
		return p1;
	}

	default Vertex3f mulDirection(final Matrix4x3fc p0, final Vertex3f p1) {
		mulDirection(p0, (Vector3f) p1);
		return p1;
	}

	default Vertex3f mulDirection(final Matrix4x3dc p0, final Vertex3f p1) {
		mulDirection(p0, (Vector3f) p1);
		return p1;
	}

	default Vertex3f mulTransposeDirection(final Matrix4fc p0, final Vertex3f p1) {
		mulTransposeDirection(p0, (Vector3f) p1);
		return p1;
	}

	default Vertex3f mul(final float p0, final Vertex3f p1) {
		mul(p0, (Vector3f) p1);
		return p1;
	}

	default Vertex3f mul(final float p0, final float p1, final float p2, final Vertex3f p3) {
		mul(p0, p1, p2, (Vector3f) p3);
		return p3;
	}

	default Vertex3f div(final float p0, final Vertex3f p1) {
		div(p0, (Vector3f) p1);
		return p1;
	}

	default Vertex3f div(final float p0, final float p1, final float p2, final Vertex3f p3) {
		div(p0, p1, p2, (Vector3f) p3);
		return p3;
	}

	default Vertex3f rotate(final Quaternionfc p0, final Vertex3f p1) {
		rotate(p0, (Vector3f) p1);
		return p1;
	}

	default Vertex3f rotateAxis(final float p0, final float p1, final float p2, final float p3, final Vertex3f p4) {
		rotateAxis(p0, p1, p2, p3, (Vector3f) p4);
		return p4;
	}

	default Vertex3f rotateX(final float p0, final Vertex3f p1) {
		rotateX(p0, (Vector3f) p1);
		return p1;
	}

	default Vertex3f rotateY(final float p0, final Vertex3f p1) {
		rotateY(p0, (Vector3f) p1);
		return p1;
	}

	default Vertex3f rotateZ(final float p0, final Vertex3f p1) {
		rotateZ(p0, (Vector3f) p1);
		return p1;
	}

	default Vertex3f normalize(final Vertex3f p0) {
		normalize((Vector3f) p0);
		return p0;
	}

	default Vertex3f normalize(final float p0, final Vertex3f p1) {
		normalize(p0, (Vector3f) p1);
		return p1;
	}

	default Vertex3f cross(final Vector3fc p0, final Vertex3f p1) {
		cross(p0, (Vector3f) p1);
		return p1;
	}

	default Vertex3f cross(final float p0, final float p1, final float p2, final Vertex3f p3) {
		cross(p0, p1, p2, (Vector3f) p3);
		return p3;
	}

	default Vertex3f min(final Vector3fc p0, final Vertex3f p1) {
		min(p0, (Vector3f) p1);
		return p1;
	}

	default Vertex3f max(final Vector3fc p0, final Vertex3f p1) {
		max(p0, (Vector3f) p1);
		return p1;
	}

	default Vertex3f negate(final Vertex3f p0) {
		negate((Vector3f) p0);
		return p0;
	}

	default Vertex3f absolute(final Vertex3f p0) {
		absolute((Vector3f) p0);
		return p0;
	}

	default Vertex3f reflect(final Vector3fc p0, final Vertex3f p1) {
		reflect(p0, (Vector3f) p1);
		return p1;
	}

	default Vertex3f reflect(final float p0, final float p1, final float p2, final Vertex3f p3) {
		reflect(p0, p1, p2, (Vector3f) p3);
		return p3;
	}

	default Vertex3f half(final Vector3fc p0, final Vertex3f p1) {
		half(p0, (Vector3f) p1);
		return p1;
	}

	default Vertex3f half(final float p0, final float p1, final float p2, final Vertex3f p3) {
		half(p0, p1, p2, (Vector3f) p3);
		return p3;
	}

	default Vertex3f smoothStep(final Vector3fc p0, final float p1, final Vertex3f p2) {
		smoothStep(p0, p1, (Vector3f) p2);
		return p2;
	}

	default Vertex3f hermite(final Vector3fc p0, final Vector3fc p1, final Vector3fc p2, final float p3, final Vertex3f p4) {
		hermite(p0, p1, p2, p3, (Vector3f) p4);
		return p4;
	}

	default Vertex3f lerp(final Vector3fc p0, final float p1, final Vertex3f p2) {
		lerp(p0, p1, (Vector3f) p2);
		return p2;
	}

	default Vertex3f get(final Vertex3f p0) {
		get((Vector3f) p0);
		return p0;
	}

	default Vertex3f orthogonalize(final Vector3fc p0, final Vertex3f p1) {
		orthogonalize(p0, (Vector3f) p1);
		return p1;
	}

	default Vertex3f orthogonalizeUnit(final Vector3fc p0, final Vertex3f p1) {
		orthogonalizeUnit(p0, (Vector3f) p1);
		return p1;
	}

	default Vertex3f floor(final Vertex3f p0) {
		floor((Vector3f) p0);
		return p0;
	}

	default Vertex3f ceil(final Vertex3f p0) {
		ceil((Vector3f) p0);
		return p0;
	}

	default Vertex3f round(final Vertex3f p0) {
		round((Vector3f) p0);
		return p0;
	}

	@Override
	default GeometryKind geometryKind() {
		return GeometryKind.POINT;
	}

	@Override
	default int intrinsicDimension() {
		return 0;
	}

	@Override
	default boolean isBounded() {
		return true;
	}
}

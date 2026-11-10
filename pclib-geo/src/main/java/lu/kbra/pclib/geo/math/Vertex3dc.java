package lu.kbra.pclib.geo.math;

import org.joml.Matrix3dc;
import org.joml.Matrix3fc;
import org.joml.Matrix3x2dc;
import org.joml.Matrix3x2fc;
import org.joml.Matrix4dc;
import org.joml.Matrix4fc;
import org.joml.Matrix4x3dc;
import org.joml.Matrix4x3fc;
import org.joml.Quaterniondc;
import org.joml.Vector3d;
import org.joml.Vector3dc;
import org.joml.Vector3fc;

import lu.kbra.pclib.geo.Geometry3dc;
import lu.kbra.pclib.geo.GeometryKind;

public interface Vertex3dc extends Vector3dc, Geometry3dc {
	@Override
	Vertex3dc getToAddress(long address);

	default Vertex3d sub(final Vector3dc p0, final Vertex3d p1) {
		this.sub(p0, (Vector3d) p1);
		return p1;
	}

	default Vertex3d sub(final Vector3fc p0, final Vertex3d p1) {
		this.sub(p0, (Vector3d) p1);
		return p1;
	}

	default Vertex3d sub(final double p0, final double p1, final double p2, final Vertex3d p3) {
		this.sub(p0, p1, p2, (Vector3d) p3);
		return p3;
	}

	default Vertex3d add(final Vector3dc p0, final Vertex3d p1) {
		this.add(p0, (Vector3d) p1);
		return p1;
	}

	default Vertex3d add(final Vector3fc p0, final Vertex3d p1) {
		this.add(p0, (Vector3d) p1);
		return p1;
	}

	default Vertex3d add(final double p0, final double p1, final double p2, final Vertex3d p3) {
		this.add(p0, p1, p2, (Vector3d) p3);
		return p3;
	}

	default Vertex3d fma(final Vector3dc p0, final Vector3dc p1, final Vertex3d p2) {
		this.fma(p0, p1, (Vector3d) p2);
		return p2;
	}

	default Vertex3d fma(final double p0, final Vector3dc p1, final Vertex3d p2) {
		this.fma(p0, p1, (Vector3d) p2);
		return p2;
	}

	default Vertex3d fma(final Vector3dc p0, final Vector3fc p1, final Vertex3d p2) {
		this.fma(p0, p1, (Vector3d) p2);
		return p2;
	}

	default Vertex3d fma(final Vector3fc p0, final Vector3fc p1, final Vertex3d p2) {
		this.fma(p0, p1, (Vector3d) p2);
		return p2;
	}

	default Vertex3d fma(final double p0, final Vector3fc p1, final Vertex3d p2) {
		this.fma(p0, p1, (Vector3d) p2);
		return p2;
	}

	default Vertex3d mulAdd(final Vector3dc p0, final Vector3dc p1, final Vertex3d p2) {
		this.mulAdd(p0, p1, (Vector3d) p2);
		return p2;
	}

	default Vertex3d mulAdd(final double p0, final Vector3dc p1, final Vertex3d p2) {
		this.mulAdd(p0, p1, (Vector3d) p2);
		return p2;
	}

	default Vertex3d mulAdd(final Vector3fc p0, final Vector3dc p1, final Vertex3d p2) {
		this.mulAdd(p0, p1, (Vector3d) p2);
		return p2;
	}

	default Vertex3d mul(final Vector3fc p0, final Vertex3d p1) {
		this.mul(p0, (Vector3d) p1);
		return p1;
	}

	default Vertex3d mul(final Vector3dc p0, final Vertex3d p1) {
		this.mul(p0, (Vector3d) p1);
		return p1;
	}

	default Vertex3d div(final Vector3fc p0, final Vertex3d p1) {
		this.div(p0, (Vector3d) p1);
		return p1;
	}

	default Vertex3d div(final Vector3dc p0, final Vertex3d p1) {
		this.div(p0, (Vector3d) p1);
		return p1;
	}

	default Vertex3d mulProject(final Matrix4dc p0, final double p1, final Vertex3d p2) {
		this.mulProject(p0, p1, (Vector3d) p2);
		return p2;
	}

	default Vertex3d mulProject(final Matrix4dc p0, final Vertex3d p1) {
		this.mulProject(p0, (Vector3d) p1);
		return p1;
	}

	default Vertex3d mulProject(final Matrix4fc p0, final Vertex3d p1) {
		this.mulProject(p0, (Vector3d) p1);
		return p1;
	}

	default Vertex3d mulProjectTranslation(final Matrix4fc p0, final Vertex3d p1) {
		this.mulProjectTranslation(p0, (Vector3d) p1);
		return p1;
	}

	default Vertex3d mulProjectTranslation(final Matrix4dc p0, final Vertex3d p1) {
		this.mulProjectTranslation(p0, (Vector3d) p1);
		return p1;
	}

	default Vertex3d mulProjectAffine(final Matrix4fc p0, final Vertex3d p1) {
		this.mulProjectAffine(p0, (Vector3d) p1);
		return p1;
	}

	default Vertex3d mulProjectGeneric(final Matrix4fc p0, final Vertex3d p1) {
		this.mulProjectGeneric(p0, (Vector3d) p1);
		return p1;
	}

	default Vertex3d mulProjectGeneric(final Matrix4dc p0, final Vertex3d p1) {
		this.mulProjectGeneric(p0, (Vector3d) p1);
		return p1;
	}

	default Vertex3d mulProjectAffine(final Matrix4dc p0, final Vertex3d p1) {
		this.mulProjectAffine(p0, (Vector3d) p1);
		return p1;
	}

	default Vertex3d mul(final Matrix3dc p0, final Vertex3d p1) {
		this.mul(p0, (Vector3d) p1);
		return p1;
	}

	default Vertex3d mul(final Matrix3fc p0, final Vertex3d p1) {
		this.mul(p0, (Vector3d) p1);
		return p1;
	}

	default Vertex3d mul(final Matrix3x2dc p0, final Vertex3d p1) {
		this.mul(p0, (Vector3d) p1);
		return p1;
	}

	default Vertex3d mul(final Matrix3x2fc p0, final Vertex3d p1) {
		this.mul(p0, (Vector3d) p1);
		return p1;
	}

	default Vertex3d mulTranspose(final Matrix3dc p0, final Vertex3d p1) {
		this.mulTranspose(p0, (Vector3d) p1);
		return p1;
	}

	default Vertex3d mulTranspose(final Matrix3fc p0, final Vertex3d p1) {
		this.mulTranspose(p0, (Vector3d) p1);
		return p1;
	}

	default Vertex3d mulPosition(final Matrix4dc p0, final Vertex3d p1) {
		this.mulPosition(p0, (Vector3d) p1);
		return p1;
	}

	default Vertex3d mulPositionGeneric(final Matrix4dc p0, final Vertex3d p1) {
		this.mulPositionGeneric(p0, (Vector3d) p1);
		return p1;
	}

	default Vertex3d mulPositionTranslation(final Matrix4dc p0, final Vertex3d p1) {
		this.mulPositionTranslation(p0, (Vector3d) p1);
		return p1;
	}

	default Vertex3d mulPosition(final Matrix4fc p0, final Vertex3d p1) {
		this.mulPosition(p0, (Vector3d) p1);
		return p1;
	}

	default Vertex3d mulPositionGeneric(final Matrix4fc p0, final Vertex3d p1) {
		this.mulPositionGeneric(p0, (Vector3d) p1);
		return p1;
	}

	default Vertex3d mulPositionTranslation(final Matrix4fc p0, final Vertex3d p1) {
		this.mulPositionTranslation(p0, (Vector3d) p1);
		return p1;
	}

	default Vertex3d mulPosition(final Matrix4x3dc p0, final Vertex3d p1) {
		this.mulPosition(p0, (Vector3d) p1);
		return p1;
	}

	default Vertex3d mulPositionGeneric(final Matrix4x3dc p0, final Vertex3d p1) {
		this.mulPositionGeneric(p0, (Vector3d) p1);
		return p1;
	}

	default Vertex3d mulPositionTranslation(final Matrix4x3dc p0, final Vertex3d p1) {
		this.mulPositionTranslation(p0, (Vector3d) p1);
		return p1;
	}

	default Vertex3d mulPosition(final Matrix4x3fc p0, final Vertex3d p1) {
		this.mulPosition(p0, (Vector3d) p1);
		return p1;
	}

	default Vertex3d mulPositionGeneric(final Matrix4x3fc p0, final Vertex3d p1) {
		this.mulPositionGeneric(p0, (Vector3d) p1);
		return p1;
	}

	default Vertex3d mulPositionTranslation(final Matrix4x3fc p0, final Vertex3d p1) {
		this.mulPositionTranslation(p0, (Vector3d) p1);
		return p1;
	}

	default Vertex3d mulTransposePosition(final Matrix4dc p0, final Vertex3d p1) {
		this.mulTransposePosition(p0, (Vector3d) p1);
		return p1;
	}

	default Vertex3d mulTransposePosition(final Matrix4fc p0, final Vertex3d p1) {
		this.mulTransposePosition(p0, (Vector3d) p1);
		return p1;
	}

	default Vertex3d mulDirection(final Matrix4dc p0, final Vertex3d p1) {
		this.mulDirection(p0, (Vector3d) p1);
		return p1;
	}

	default Vertex3d mulDirection(final Matrix4fc p0, final Vertex3d p1) {
		this.mulDirection(p0, (Vector3d) p1);
		return p1;
	}

	default Vertex3d mulDirection(final Matrix4x3dc p0, final Vertex3d p1) {
		this.mulDirection(p0, (Vector3d) p1);
		return p1;
	}

	default Vertex3d mulDirection(final Matrix4x3fc p0, final Vertex3d p1) {
		this.mulDirection(p0, (Vector3d) p1);
		return p1;
	}

	default Vertex3d mulTransposeDirection(final Matrix4dc p0, final Vertex3d p1) {
		this.mulTransposeDirection(p0, (Vector3d) p1);
		return p1;
	}

	default Vertex3d mulTransposeDirection(final Matrix4fc p0, final Vertex3d p1) {
		this.mulTransposeDirection(p0, (Vector3d) p1);
		return p1;
	}

	default Vertex3d mul(final double p0, final Vertex3d p1) {
		this.mul(p0, (Vector3d) p1);
		return p1;
	}

	default Vertex3d mul(final double p0, final double p1, final double p2, final Vertex3d p3) {
		this.mul(p0, p1, p2, (Vector3d) p3);
		return p3;
	}

	default Vertex3d rotate(final Quaterniondc p0, final Vertex3d p1) {
		this.rotate(p0, (Vector3d) p1);
		return p1;
	}

	default Vertex3d rotateAxis(final double p0, final double p1, final double p2, final double p3, final Vertex3d p4) {
		this.rotateAxis(p0, p1, p2, p3, (Vector3d) p4);
		return p4;
	}

	default Vertex3d rotateX(final double p0, final Vertex3d p1) {
		this.rotateX(p0, (Vector3d) p1);
		return p1;
	}

	default Vertex3d rotateY(final double p0, final Vertex3d p1) {
		this.rotateY(p0, (Vector3d) p1);
		return p1;
	}

	default Vertex3d rotateZ(final double p0, final Vertex3d p1) {
		this.rotateZ(p0, (Vector3d) p1);
		return p1;
	}

	default Vertex3d div(final double p0, final Vertex3d p1) {
		this.div(p0, (Vector3d) p1);
		return p1;
	}

	default Vertex3d div(final double p0, final double p1, final double p2, final Vertex3d p3) {
		this.div(p0, p1, p2, (Vector3d) p3);
		return p3;
	}

	default Vertex3d normalize(final Vertex3d p0) {
		this.normalize((Vector3d) p0);
		return p0;
	}

	default Vertex3d normalize(final double p0, final Vertex3d p1) {
		this.normalize(p0, (Vector3d) p1);
		return p1;
	}

	default Vertex3d cross(final Vector3dc p0, final Vertex3d p1) {
		this.cross(p0, (Vector3d) p1);
		return p1;
	}

	default Vertex3d cross(final double p0, final double p1, final double p2, final Vertex3d p3) {
		this.cross(p0, p1, p2, (Vector3d) p3);
		return p3;
	}

	default Vertex3d min(final Vector3dc p0, final Vertex3d p1) {
		this.min(p0, (Vector3d) p1);
		return p1;
	}

	default Vertex3d max(final Vector3dc p0, final Vertex3d p1) {
		this.max(p0, (Vector3d) p1);
		return p1;
	}

	default Vertex3d negate(final Vertex3d p0) {
		this.negate((Vector3d) p0);
		return p0;
	}

	default Vertex3d absolute(final Vertex3d p0) {
		this.absolute((Vector3d) p0);
		return p0;
	}

	default Vertex3d reflect(final Vector3dc p0, final Vertex3d p1) {
		this.reflect(p0, (Vector3d) p1);
		return p1;
	}

	default Vertex3d reflect(final double p0, final double p1, final double p2, final Vertex3d p3) {
		this.reflect(p0, p1, p2, (Vector3d) p3);
		return p3;
	}

	default Vertex3d half(final Vector3dc p0, final Vertex3d p1) {
		this.half(p0, (Vector3d) p1);
		return p1;
	}

	default Vertex3d half(final double p0, final double p1, final double p2, final Vertex3d p3) {
		this.half(p0, p1, p2, (Vector3d) p3);
		return p3;
	}

	default Vertex3d smoothStep(final Vector3dc p0, final double p1, final Vertex3d p2) {
		this.smoothStep(p0, p1, (Vector3d) p2);
		return p2;
	}

	default Vertex3d hermite(final Vector3dc p0, final Vector3dc p1, final Vector3dc p2, final double p3, final Vertex3d p4) {
		this.hermite(p0, p1, p2, p3, (Vector3d) p4);
		return p4;
	}

	default Vertex3d lerp(final Vector3dc p0, final double p1, final Vertex3d p2) {
		this.lerp(p0, p1, (Vector3d) p2);
		return p2;
	}

	default Vertex3d get(final Vertex3d p0) {
		this.get((Vector3d) p0);
		return p0;
	}

	default Vertex3d orthogonalize(final Vector3dc p0, final Vertex3d p1) {
		this.orthogonalize(p0, (Vector3d) p1);
		return p1;
	}

	default Vertex3d orthogonalizeUnit(final Vector3dc p0, final Vertex3d p1) {
		this.orthogonalizeUnit(p0, (Vector3d) p1);
		return p1;
	}

	default Vertex3d floor(final Vertex3d p0) {
		this.floor((Vector3d) p0);
		return p0;
	}

	default Vertex3d ceil(final Vertex3d p0) {
		this.ceil((Vector3d) p0);
		return p0;
	}

	default Vertex3d round(final Vertex3d p0) {
		this.round((Vector3d) p0);
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

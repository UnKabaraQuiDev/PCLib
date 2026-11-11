package lu.kbra.pclib.geo.math;

import java.nio.*;

import org.joml.*;
import org.joml.Vector2fc;

import lu.kbra.pclib.geo.Geometry2fc;
import lu.kbra.pclib.geo.GeometryKind;

public interface Vertex2fc extends Vector2fc, Geometry2fc {
	@Override
	Vertex2fc getToAddress(long address);

	default Vertex2f sub(final Vector2fc p0, final Vertex2f p1) {
		sub(p0, (Vector2f) p1);
		return p1;
	}

	default Vertex2f sub(final float p0, final float p1, final Vertex2f p2) {
		sub(p0, p1, (Vector2f) p2);
		return p2;
	}

	default Vertex2f normalize(final Vertex2f p0) {
		normalize((Vector2f) p0);
		return p0;
	}

	default Vertex2f normalize(final float p0, final Vertex2f p1) {
		normalize(p0, (Vector2f) p1);
		return p1;
	}

	default Vertex2f add(final Vector2fc p0, final Vertex2f p1) {
		add(p0, (Vector2f) p1);
		return p1;
	}

	default Vertex2f add(final float p0, final float p1, final Vertex2f p2) {
		add(p0, p1, (Vector2f) p2);
		return p2;
	}

	default Vertex2f negate(final Vertex2f p0) {
		negate((Vector2f) p0);
		return p0;
	}

	default Vertex2f mul(final float p0, final Vertex2f p1) {
		mul(p0, (Vector2f) p1);
		return p1;
	}

	default Vertex2f mul(final float p0, final float p1, final Vertex2f p2) {
		mul(p0, p1, (Vector2f) p2);
		return p2;
	}

	default Vertex2f mul(final Vector2fc p0, final Vertex2f p1) {
		mul(p0, (Vector2f) p1);
		return p1;
	}

	default Vertex2f div(final float p0, final Vertex2f p1) {
		div(p0, (Vector2f) p1);
		return p1;
	}

	default Vertex2f div(final Vector2fc p0, final Vertex2f p1) {
		div(p0, (Vector2f) p1);
		return p1;
	}

	default Vertex2f div(final float p0, final float p1, final Vertex2f p2) {
		div(p0, p1, (Vector2f) p2);
		return p2;
	}

	default Vertex2f mul(final Matrix2fc p0, final Vertex2f p1) {
		mul(p0, (Vector2f) p1);
		return p1;
	}

	default Vertex2f mul(final Matrix2dc p0, final Vertex2f p1) {
		mul(p0, (Vector2f) p1);
		return p1;
	}

	default Vertex2f mulTranspose(final Matrix2fc p0, final Vertex2f p1) {
		mulTranspose(p0, (Vector2f) p1);
		return p1;
	}

	default Vertex2f mulPosition(final Matrix3x2fc p0, final Vertex2f p1) {
		mulPosition(p0, (Vector2f) p1);
		return p1;
	}

	default Vertex2f mulDirection(final Matrix3x2fc p0, final Vertex2f p1) {
		mulDirection(p0, (Vector2f) p1);
		return p1;
	}

	default Vertex2f lerp(final Vector2fc p0, final float p1, final Vertex2f p2) {
		lerp(p0, p1, (Vector2f) p2);
		return p2;
	}

	default Vertex2f fma(final Vector2fc p0, final Vector2fc p1, final Vertex2f p2) {
		fma(p0, p1, (Vector2f) p2);
		return p2;
	}

	default Vertex2f fma(final float p0, final Vector2fc p1, final Vertex2f p2) {
		fma(p0, p1, (Vector2f) p2);
		return p2;
	}

	default Vertex2f min(final Vector2fc p0, final Vertex2f p1) {
		min(p0, (Vector2f) p1);
		return p1;
	}

	default Vertex2f max(final Vector2fc p0, final Vertex2f p1) {
		max(p0, (Vector2f) p1);
		return p1;
	}

	default Vertex2f get(final Vertex2f p0) {
		get((Vector2f) p0);
		return p0;
	}

	default Vertex2f floor(final Vertex2f p0) {
		floor((Vector2f) p0);
		return p0;
	}

	default Vertex2f ceil(final Vertex2f p0) {
		ceil((Vector2f) p0);
		return p0;
	}

	default Vertex2f round(final Vertex2f p0) {
		round((Vector2f) p0);
		return p0;
	}

	default Vertex2f absolute(final Vertex2f p0) {
		absolute((Vector2f) p0);
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

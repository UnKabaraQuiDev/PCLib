package lu.kbra.pclib.geo.math;

import org.joml.Matrix2dc;
import org.joml.Matrix2fc;
import org.joml.Matrix3x2dc;
import org.joml.Vector2d;
import org.joml.Vector2dc;
import org.joml.Vector2fc;

import lu.kbra.pclib.geo.Geometry2dc;
import lu.kbra.pclib.geo.GeometryKind;

public interface Vertex2dc extends Vector2dc, Geometry2dc {
	@Override
	Vertex2dc getToAddress(long address);

	default Vertex2d sub(final double p0, final double p1, final Vertex2d p2) {
		this.sub(p0, p1, (Vector2d) p2);
		return p2;
	}

	default Vertex2d sub(final Vector2dc p0, final Vertex2d p1) {
		this.sub(p0, (Vector2d) p1);
		return p1;
	}

	default Vertex2d sub(final Vector2fc p0, final Vertex2d p1) {
		this.sub(p0, (Vector2d) p1);
		return p1;
	}

	default Vertex2d mul(final double p0, final Vertex2d p1) {
		this.mul(p0, (Vector2d) p1);
		return p1;
	}

	default Vertex2d mul(final double p0, final double p1, final Vertex2d p2) {
		this.mul(p0, p1, (Vector2d) p2);
		return p2;
	}

	default Vertex2d mul(final Vector2dc p0, final Vertex2d p1) {
		this.mul(p0, (Vector2d) p1);
		return p1;
	}

	default Vertex2d div(final double p0, final Vertex2d p1) {
		this.div(p0, (Vector2d) p1);
		return p1;
	}

	default Vertex2d div(final double p0, final double p1, final Vertex2d p2) {
		this.div(p0, p1, (Vector2d) p2);
		return p2;
	}

	default Vertex2d div(final Vector2fc p0, final Vertex2d p1) {
		this.div(p0, (Vector2d) p1);
		return p1;
	}

	default Vertex2d div(final Vector2dc p0, final Vertex2d p1) {
		this.div(p0, (Vector2d) p1);
		return p1;
	}

	default Vertex2d mul(final Matrix2dc p0, final Vertex2d p1) {
		this.mul(p0, (Vector2d) p1);
		return p1;
	}

	default Vertex2d mul(final Matrix2fc p0, final Vertex2d p1) {
		this.mul(p0, (Vector2d) p1);
		return p1;
	}

	default Vertex2d mulTranspose(final Matrix2dc p0, final Vertex2d p1) {
		this.mulTranspose(p0, (Vector2d) p1);
		return p1;
	}

	default Vertex2d mulTranspose(final Matrix2fc p0, final Vertex2d p1) {
		this.mulTranspose(p0, (Vector2d) p1);
		return p1;
	}

	default Vertex2d mulPosition(final Matrix3x2dc p0, final Vertex2d p1) {
		this.mulPosition(p0, (Vector2d) p1);
		return p1;
	}

	default Vertex2d mulDirection(final Matrix3x2dc p0, final Vertex2d p1) {
		this.mulDirection(p0, (Vector2d) p1);
		return p1;
	}

	default Vertex2d normalize(final Vertex2d p0) {
		this.normalize((Vector2d) p0);
		return p0;
	}

	default Vertex2d normalize(final double p0, final Vertex2d p1) {
		this.normalize(p0, (Vector2d) p1);
		return p1;
	}

	default Vertex2d add(final double p0, final double p1, final Vertex2d p2) {
		this.add(p0, p1, (Vector2d) p2);
		return p2;
	}

	default Vertex2d add(final Vector2dc p0, final Vertex2d p1) {
		this.add(p0, (Vector2d) p1);
		return p1;
	}

	default Vertex2d add(final Vector2fc p0, final Vertex2d p1) {
		this.add(p0, (Vector2d) p1);
		return p1;
	}

	default Vertex2d negate(final Vertex2d p0) {
		this.negate((Vector2d) p0);
		return p0;
	}

	default Vertex2d lerp(final Vector2dc p0, final double p1, final Vertex2d p2) {
		this.lerp(p0, p1, (Vector2d) p2);
		return p2;
	}

	default Vertex2d fma(final Vector2dc p0, final Vector2dc p1, final Vertex2d p2) {
		this.fma(p0, p1, (Vector2d) p2);
		return p2;
	}

	default Vertex2d fma(final double p0, final Vector2dc p1, final Vertex2d p2) {
		this.fma(p0, p1, (Vector2d) p2);
		return p2;
	}

	default Vertex2d min(final Vector2dc p0, final Vertex2d p1) {
		this.min(p0, (Vector2d) p1);
		return p1;
	}

	default Vertex2d max(final Vector2dc p0, final Vertex2d p1) {
		this.max(p0, (Vector2d) p1);
		return p1;
	}

	default Vertex2d get(final Vertex2d p0) {
		this.get((Vector2d) p0);
		return p0;
	}

	default Vertex2d floor(final Vertex2d p0) {
		this.floor((Vector2d) p0);
		return p0;
	}

	default Vertex2d ceil(final Vertex2d p0) {
		this.ceil((Vector2d) p0);
		return p0;
	}

	default Vertex2d round(final Vertex2d p0) {
		this.round((Vector2d) p0);
		return p0;
	}

	default Vertex2d absolute(final Vertex2d p0) {
		this.absolute((Vector2d) p0);
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

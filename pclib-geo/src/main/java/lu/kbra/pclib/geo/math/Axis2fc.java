package lu.kbra.pclib.geo.math;

import java.nio.*;

import org.joml.*;
import org.joml.Vector2fc;

public interface Axis2fc extends Vector2fc {
	@Override
	Axis2fc getToAddress(long address);

	default Axis2f sub(final Vector2fc p0, final Axis2f p1) {
		sub(p0, (Vector2f) p1);
		return p1;
	}

	default Axis2f sub(final float p0, final float p1, final Axis2f p2) {
		sub(p0, p1, (Vector2f) p2);
		return p2;
	}

	default Axis2f normalize(final Axis2f p0) {
		normalize((Vector2f) p0);
		return p0;
	}

	default Axis2f normalize(final float p0, final Axis2f p1) {
		normalize(p0, (Vector2f) p1);
		return p1;
	}

	default Axis2f add(final Vector2fc p0, final Axis2f p1) {
		add(p0, (Vector2f) p1);
		return p1;
	}

	default Axis2f add(final float p0, final float p1, final Axis2f p2) {
		add(p0, p1, (Vector2f) p2);
		return p2;
	}

	default Axis2f negate(final Axis2f p0) {
		negate((Vector2f) p0);
		return p0;
	}

	default Axis2f mul(final float p0, final Axis2f p1) {
		mul(p0, (Vector2f) p1);
		return p1;
	}

	default Axis2f mul(final float p0, final float p1, final Axis2f p2) {
		mul(p0, p1, (Vector2f) p2);
		return p2;
	}

	default Axis2f mul(final Vector2fc p0, final Axis2f p1) {
		mul(p0, (Vector2f) p1);
		return p1;
	}

	default Axis2f div(final float p0, final Axis2f p1) {
		div(p0, (Vector2f) p1);
		return p1;
	}

	default Axis2f div(final Vector2fc p0, final Axis2f p1) {
		div(p0, (Vector2f) p1);
		return p1;
	}

	default Axis2f div(final float p0, final float p1, final Axis2f p2) {
		div(p0, p1, (Vector2f) p2);
		return p2;
	}

	default Axis2f mul(final Matrix2fc p0, final Axis2f p1) {
		mul(p0, (Vector2f) p1);
		return p1;
	}

	default Axis2f mul(final Matrix2dc p0, final Axis2f p1) {
		mul(p0, (Vector2f) p1);
		return p1;
	}

	default Axis2f mulTranspose(final Matrix2fc p0, final Axis2f p1) {
		mulTranspose(p0, (Vector2f) p1);
		return p1;
	}

	default Axis2f mulPosition(final Matrix3x2fc p0, final Axis2f p1) {
		mulPosition(p0, (Vector2f) p1);
		return p1;
	}

	default Axis2f mulDirection(final Matrix3x2fc p0, final Axis2f p1) {
		mulDirection(p0, (Vector2f) p1);
		return p1;
	}

	default Axis2f lerp(final Vector2fc p0, final float p1, final Axis2f p2) {
		lerp(p0, p1, (Vector2f) p2);
		return p2;
	}

	default Axis2f fma(final Vector2fc p0, final Vector2fc p1, final Axis2f p2) {
		fma(p0, p1, (Vector2f) p2);
		return p2;
	}

	default Axis2f fma(final float p0, final Vector2fc p1, final Axis2f p2) {
		fma(p0, p1, (Vector2f) p2);
		return p2;
	}

	default Axis2f min(final Vector2fc p0, final Axis2f p1) {
		min(p0, (Vector2f) p1);
		return p1;
	}

	default Axis2f max(final Vector2fc p0, final Axis2f p1) {
		max(p0, (Vector2f) p1);
		return p1;
	}

	default Axis2f get(final Axis2f p0) {
		get((Vector2f) p0);
		return p0;
	}

	default Axis2f floor(final Axis2f p0) {
		floor((Vector2f) p0);
		return p0;
	}

	default Axis2f ceil(final Axis2f p0) {
		ceil((Vector2f) p0);
		return p0;
	}

	default Axis2f round(final Axis2f p0) {
		round((Vector2f) p0);
		return p0;
	}

	default Axis2f absolute(final Axis2f p0) {
		absolute((Vector2f) p0);
		return p0;
	}
}

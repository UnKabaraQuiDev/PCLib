package lu.kbra.pclib.geo.two.region;

import lu.kbra.pclib.geo.Owned;

import java.util.ArrayList;
import java.util.List;

import org.joml.Vector2fc;

import lu.kbra.pclib.geo.Geometry2fc;
import lu.kbra.pclib.geo.GeometryKind;
import lu.kbra.pclib.geo.equation.ImplicitRepresentable;
import lu.kbra.pclib.geo.equation.constraint.MathConstraint2fc;
import lu.kbra.pclib.geo.equation.implicit.ImplicitSystem2fc;
import lu.kbra.pclib.geo.math.Vertex2f;
import lu.kbra.pclib.geo.math.Vertex2fc;

/** Polygon with an exterior ring and optional holes (non-self-intersecting input). */
public class Polygon2f implements Geometry2fc, ImplicitRepresentable<ImplicitSystem2fc, MathConstraint2fc>, Cloneable {
	@Override
	public @Owned Polygon2f clone() {
		return new Polygon2f(exterior(), holes());
	}

	private final List<Vertex2f> exterior;
	private final List<List<Vertex2f>> holes;

	public Polygon2f(final List<? extends Vector2fc> exterior, final List<? extends List<? extends Vector2fc>> holes) {
		this.exterior = Polygon2f.copyRing(exterior);
		this.holes = new ArrayList<>();
		for (final List<? extends Vector2fc> hole : holes) {
			this.holes.add(Polygon2f.copyRing(hole));
		}
	}

	public Polygon2f(final List<? extends Vector2fc> exterior) {
		this(exterior, List.of());
	}

	private static List<Vertex2f> copyRing(final List<? extends Vector2fc> ring) {
		if (ring.size() < 3) {
			throw new IllegalArgumentException("Ring needs at least three vertices");
		}
		final List<Vertex2f> result = new ArrayList<>();
		for (final Vector2fc point : ring) {
			result.add(new Vertex2f(point));
		}
		return List.copyOf(result);
	}

	public List<Vertex2fc> exterior() {
		return List.copyOf(this.exterior);
	}

	public List<List<Vertex2fc>> holes() {
		final List<List<Vertex2fc>> result = new ArrayList<>();
		for (final List<Vertex2f> ring : this.holes) {
			result.add(List.copyOf(ring));
		}
		return List.copyOf(result);
	}

	private static boolean inside(final List<Vertex2f> ring, final Vector2fc p) {
		boolean result = false;
		for (int i = 0, j = ring.size() - 1; i < ring.size(); j = i++) {
			final Vertex2f a = ring.get(i), b = ring.get(j);
			if (a.y > p.y() != b.y > p.y() && p.x() < (b.x - a.x) * (p.y() - a.y) / (b.y - a.y) + a.x) {
				result = !result;
			}
		}
		return result;
	}

	private static boolean onBoundary(final List<Vertex2f> ring, final Vector2fc p) {
		for (int i = 0, j = ring.size() - 1; i < ring.size(); j = i++) {
			final Vertex2f a = ring.get(i), b = ring.get(j);
			final float dx = b.x - a.x, dy = b.y - a.y;
			final float cross = (p.x() - a.x) * dy - (p.y() - a.y) * dx;
			final float dot = (p.x() - a.x) * dx + (p.y() - a.y) * dy;
			if (Math.abs(cross) <= 1e-10 * Math.max(1, Math.hypot(dx, dy)) && dot >= 0 && dot <= dx * dx + dy * dy) {
				return true;
			}
		}
		return false;
	}

	public boolean contains(final Vector2fc point) {
		if (Polygon2f.onBoundary(this.exterior, point)) {
			return true;
		}
		if (!Polygon2f.inside(this.exterior, point)) {
			return false;
		}
		for (final List<Vertex2f> hole : this.holes) {
			if (Polygon2f.onBoundary(hole, point)) {
				return true;
			}
			if (Polygon2f.inside(hole, point)) {
				return false;
			}
		}
		return true;
	}

	@Override
	public ImplicitSystem2fc implicitEquation() {
		return new ImplicitSystem2fc();
	}

	@Override
	public List<MathConstraint2fc> constraints() {
		return List.of((p, precision) -> this.contains(p));
	}

	@Override
	public GeometryKind geometryKind() {
		return GeometryKind.REGION;
	}

	@Override
	public int intrinsicDimension() {
		return 2;
	}

	@Override
	public boolean isBounded() {
		return true;
	}

}

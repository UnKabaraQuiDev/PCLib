package lu.kbra.pclib.geo.two.region;

import java.util.ArrayList;
import java.util.List;

import org.joml.Vector2dc;

import lu.kbra.pclib.geo.Geometry2dc;
import lu.kbra.pclib.geo.GeometryKind;
import lu.kbra.pclib.geo.equation.ImplicitRepresentable;
import lu.kbra.pclib.geo.equation.constraint.MathConstraint2dc;
import lu.kbra.pclib.geo.equation.implicit.ImplicitSystem2dc;
import lu.kbra.pclib.geo.math.Vertex2d;
import lu.kbra.pclib.geo.math.Vertex2dc;

/** Polygon with an exterior ring and optional holes (non-self-intersecting input). */
public class Polygon2d implements Geometry2dc, ImplicitRepresentable<ImplicitSystem2dc, MathConstraint2dc> {

	private final List<Vertex2d> exterior;
	private final List<List<Vertex2d>> holes;

	public Polygon2d(final List<? extends Vector2dc> exterior, final List<? extends List<? extends Vector2dc>> holes) {
		this.exterior = Polygon2d.copyRing(exterior);
		this.holes = new ArrayList<>();
		for (final List<? extends Vector2dc> hole : holes) {
			this.holes.add(Polygon2d.copyRing(hole));
		}
	}

	public Polygon2d(final List<? extends Vector2dc> exterior) {
		this(exterior, List.of());
	}

	private static List<Vertex2d> copyRing(final List<? extends Vector2dc> ring) {
		if (ring.size() < 3) {
			throw new IllegalArgumentException("Ring needs at least three vertices");
		}
		final List<Vertex2d> result = new ArrayList<>();
		for (final Vector2dc point : ring) {
			result.add(new Vertex2d(point));
		}
		return List.copyOf(result);
	}

	public List<Vertex2dc> exterior() {
		return List.copyOf(this.exterior);
	}

	public List<List<Vertex2dc>> holes() {
		final List<List<Vertex2dc>> result = new ArrayList<>();
		for (final List<Vertex2d> ring : this.holes) {
			result.add(List.copyOf(ring));
		}
		return List.copyOf(result);
	}

	private static boolean inside(final List<Vertex2d> ring, final Vector2dc p) {
		boolean result = false;
		for (int i = 0, j = ring.size() - 1; i < ring.size(); j = i++) {
			final Vertex2d a = ring.get(i), b = ring.get(j);
			if (a.y > p.y() != b.y > p.y() && p.x() < (b.x - a.x) * (p.y() - a.y) / (b.y - a.y) + a.x) {
				result = !result;
			}
		}
		return result;
	}

	private static boolean onBoundary(final List<Vertex2d> ring, final Vector2dc p) {
		for (int i = 0, j = ring.size() - 1; i < ring.size(); j = i++) {
			final Vertex2d a = ring.get(i), b = ring.get(j);
			final double dx = b.x - a.x, dy = b.y - a.y;
			final double cross = (p.x() - a.x) * dy - (p.y() - a.y) * dx;
			final double dot = (p.x() - a.x) * dx + (p.y() - a.y) * dy;
			if (Math.abs(cross) <= 1e-10 * Math.max(1, Math.hypot(dx, dy)) && dot >= 0 && dot <= dx * dx + dy * dy) {
				return true;
			}
		}
		return false;
	}

	public boolean contains(final Vector2dc point) {
		if (Polygon2d.onBoundary(this.exterior, point)) {
			return true;
		}
		if (!Polygon2d.inside(this.exterior, point)) {
			return false;
		}
		for (final List<Vertex2d> hole : this.holes) {
			if (Polygon2d.onBoundary(hole, point)) {
				return true;
			}
			if (Polygon2d.inside(hole, point)) {
				return false;
			}
		}
		return true;
	}

	@Override
	public ImplicitSystem2dc implicitEquation() {
		return new ImplicitSystem2dc();
	}

	@Override
	public List<MathConstraint2dc> constraints() {
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

package lu.kbra.pclib.geo.equation.solver;

import java.util.ArrayList;
import java.util.List;

import lu.kbra.pclib.geo.Owned;

/** A finite set of mathematical coordinate tuples, not Vertex objects. */
public final class FiniteSolutions implements MathSolution {
	private final List<double[]> points;

	public FiniteSolutions(final List<double[]> points) {
		this.points = new ArrayList<>();
		for (final double[] p : points) {
			this.points.add(p.clone());
		}
	}

	public @Owned List<double[]> points() {
		return this.points.stream().map(double[]::clone).toList();
	}
}

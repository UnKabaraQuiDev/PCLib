package lu.kbra.pclib.geo.equation.solver;

import java.util.ArrayList;
import java.util.List;

import lu.kbra.pclib.geo.equation.implicit.ImplicitSystem2dc;
import lu.kbra.pclib.geo.equation.implicit.ImplicitSystem3dc;
import lu.kbra.pclib.geo.equation.implicit.LinearEquation2d;
import lu.kbra.pclib.geo.equation.implicit.LinearEquation3d;
import lu.kbra.pclib.geo.math.GeometryPrecision;

/** Rank-revealing partial-pivot Gaussian elimination for small 2D/3D systems. */
public final class LinearSystemSolver {
	private LinearSystemSolver() {
	}

	public static MathSolution solve(final ImplicitSystem3dc system) {
		return LinearSystemSolver.solve(system, GeometryPrecision.DEFAULT);
	}

	public static MathSolution solve(final ImplicitSystem3dc system, final GeometryPrecision precision) {
		final double[][] rows = new double[system.equations().size()][4];
		for (int i = 0; i < rows.length; i++) {
			if (!(system.equations().get(i) instanceof final LinearEquation3d e)) {
				return new UnresolvedSolution("System contains nonlinear equations");
			}
			rows[i] = new double[] { e.a(), e.b(), e.c(), -e.d() };
		}
		return LinearSystemSolver.solveLinear(rows, 3, precision);
	}

	public static MathSolution solve(final ImplicitSystem2dc system) {
		return LinearSystemSolver.solve(system, GeometryPrecision.DEFAULT);
	}

	public static MathSolution solve(final ImplicitSystem2dc system, final GeometryPrecision precision) {
		final double[][] rows = new double[system.equations().size()][3];
		for (int i = 0; i < rows.length; i++) {
			if (!(system.equations().get(i) instanceof final LinearEquation2d e)) {
				return new UnresolvedSolution("System contains nonlinear equations");
			}
			rows[i] = new double[] { e.a(), e.b(), -e.c() };
		}
		return LinearSystemSolver.solveLinear(rows, 2, precision);
	}

	private static MathSolution solveLinear(final double[][] input, final int dimensions, final GeometryPrecision precision) {
		final int m = input.length;
		final double[][] a = new double[m][dimensions + 1];
		double scale = 0;
		for (int r = 0; r < m; r++) {
			if (input[r].length != dimensions + 1) {
				throw new IllegalArgumentException("Wrong row length");
			}
			for (int c = 0; c <= dimensions; c++) {
				if (!Double.isFinite(input[r][c])) {
					throw new IllegalArgumentException("Non-finite coefficient");
				}
				a[r][c] = input[r][c];
				if (c < dimensions) {
					scale = Math.max(scale, Math.abs(a[r][c]));
				}
			}
		}
		final double threshold = Math.max(precision.relative() * scale, precision.distance() * 1e-4);
		final List<Integer> pivots = new ArrayList<>();
		int pivotRow = 0;
		for (int col = 0; col < dimensions && pivotRow < m; col++) {
			int best = pivotRow;
			for (int r = pivotRow + 1; r < m; r++) {
				if (Math.abs(a[r][col]) > Math.abs(a[best][col])) {
					best = r;
				}
			}
			if (Math.abs(a[best][col]) <= threshold) {
				continue;
			}
			final double[] tmp = a[best];
			a[best] = a[pivotRow];
			a[pivotRow] = tmp;
			final double divider = a[pivotRow][col];
			for (int k = col; k <= dimensions; k++) {
				a[pivotRow][k] /= divider;
			}
			for (int r = 0; r < m; r++) {
				if (r != pivotRow) {
					final double factor = a[r][col];
					for (int k = col; k <= dimensions; k++) {
						a[r][k] -= factor * a[pivotRow][k];
					}
				}
			}
			pivots.add(col);
			pivotRow++;
		}
		for (int r = pivotRow; r < m; r++) {
			double norm = 0;
			for (int c = 0; c < dimensions; c++) {
				norm = Math.max(norm, Math.abs(a[r][c]));
			}
			if (norm <= threshold && Math.abs(a[r][dimensions]) > precision.distance()) {
				return NoSolution.INSTANCE;
			}
		}
		final double[] origin = new double[dimensions];
		for (int r = 0; r < pivots.size(); r++) {
			origin[pivots.get(r)] = a[r][dimensions];
		}
		final List<double[]> basis = new ArrayList<>();
		for (int free = 0; free < dimensions; free++) {
			if (!pivots.contains(free)) {
				final double[] vector = new double[dimensions];
				vector[free] = 1;
				for (int r = 0; r < pivots.size(); r++) {
					vector[pivots.get(r)] = -a[r][free];
				}
				basis.add(vector);
			}
		}
		return new AffineSolution(origin, basis.toArray(double[][]::new));
	}
}

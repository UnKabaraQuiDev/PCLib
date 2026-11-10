package lu.kbra.pclib.geo.equation.solver;

import java.util.List;

public record CompositeSolution(List<MathSolution> components) implements MathSolution {
	public CompositeSolution {
		components = List.copyOf(components);
	}

	@Override
	public boolean complete() {
		return this.components.stream().allMatch(MathSolution::complete);
	}
}

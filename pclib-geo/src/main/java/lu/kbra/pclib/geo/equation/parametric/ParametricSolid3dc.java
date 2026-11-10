package lu.kbra.pclib.geo.equation.parametric;

import lu.kbra.pclib.geo.Owned;
import lu.kbra.pclib.geo.equation.ParametricRepresentable;
import lu.kbra.pclib.geo.math.Vertex3d;

public interface ParametricSolid3dc extends ParametricRepresentable<ParametricEquation3to3dc> {
	default ParameterDomain3d parameterDomain() {
		return ParameterDomain3d.ALL;
	}

	default @Owned Vertex3d at(final double u, final double v, final double w) {
		return this.at(u, v, w, new Vertex3d());
	}

	default Vertex3d at(final double u, final double v, final double w, final Vertex3d dest) {
		this.parametricEquation().at(u, v, w, dest);
		return dest;
	}
}

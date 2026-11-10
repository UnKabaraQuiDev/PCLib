package lu.kbra.pclib.geo.equation.parametric;

import lu.kbra.pclib.geo.Owned;
import lu.kbra.pclib.geo.equation.ParametricRepresentable;
import lu.kbra.pclib.geo.math.Vertex3f;

public interface ParametricSolid3fc extends ParametricRepresentable<ParametricEquation3to3fc> {
	default ParameterDomain3d parameterDomain() {
		return ParameterDomain3d.ALL;
	}

	default @Owned Vertex3f at(final float u, final float v, final float w) {
		return this.at(u, v, w, new Vertex3f());
	}

	default Vertex3f at(final float u, final float v, final float w, final Vertex3f dest) {
		this.parametricEquation().at(u, v, w, dest);
		return dest;
	}
}

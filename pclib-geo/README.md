# PCLib Geometry (`pclib-geo`)

Java 17 geometry submodule backed by **JOML 1.10.8**. Copy this directory into the PCLib repository, add `<module>pclib-geo</module>` to the parent `pom.xml`, and build with a JDK 17+ and Maven:

```bash
mvn -pl pclib-geo -am test
```

The POM references the existing PCLib parent, `pclib-datastruct` tuples, and `pclib-function` throwing functions; this directory is a **submodule**, not a completely independent Maven application. PCLib's existing Java 8 modules need not change their source level, but Java 8 consumers cannot load this module's Java 17 bytecode.

## Implemented foundation

- JOML-style `Vertex2/3d/f(c)` and `Axis2/3d/f(c)`; `LCS2/3d/f(c)` frames. Both double and float point/axis values override JOML mutators with covariant return types; both constant-interface families have typed-destination overloads. Float vertices now support reflection and projection, and float frames support construction from normals, X axes, and surface tangents.
- Geometry metadata and separate 2D/3D interfaces.
- Parametric equation capability, arity-specific double/float mappings, curve/surface/volume evaluation, and 1D/2D/3D parameter domains. Parametric curves provide domain-aware tangents and LCS frames; 3D surfaces provide both partial derivatives, tangent/cotangent/normal, and LCS frames, with analytic overrides where available.
- Implicit equation systems, linear and quadratic equations, predicates/inequality constraints, and linear rank/null-space solving. Both float and double representations use the same mathematical solution types; the float solver overloads widen their coefficients to double for elimination. The polynomial solver supports any number of linear equalities plus one 3D quadratic equality; it returns mathematical finite points, affine loci, quadratic loci, `NoSolution`, or `UnresolvedSolution` when the supported class is exceeded.
- Constraint clipping of one-dimensional affine mathematical solutions with structural linear inequalities, including strict bounds. The constrained intersection solver keeps unresolved higher-dimensional restrictions rather than silently dropping them.
- Sealed mathematical solution model (`AffineSolution`, `NoSolution`, finite/implicit/composite/constrained/unresolved solutions); it never constructs PCLib shapes.
- Tagged `Result2`–`Result8` extending PCLib's read-only tuple implementations, plus `Empty.INSTANCE`; tests cover `get`, `getOrElse`, and `getOrThrow`.
- Double-precision lines, rays, segments, planes, circles, ellipse curves, disks, sphere shells, balls, boxes, rectangle, polygon with holes, parabola, helix, tanh curves, and generic parametric/implicit surface wrappers.
- Arbitrary-degree Bézier and nonrational B-spline curves (2D/3D), rational NURBS curves (2D/3D), tensor-product Bézier, B-spline, and NURBS surface patches (3D). Clamped knot/de Boor evaluation and analytic Bézier derivatives are included.
- Finite right-circular cylinder lateral shells and filled cylinders (quadratic implicit radius + axial bounds); ring torus shells and solids (quartic implicit predicate and parametric charts).
- Finite right-circular **cone** lateral shells and filled cones with exact quadratic implicit representations and bounded parameter domains. Added planar catenary, Archimedean spiral, and logarithmic spiral curves with analytic derivatives.
- Typed 3D line–plane, plane–plane, sphere–plane, ball–plane, ray–plane, segment–plane, ray–segment, and 2D line–line intersections.
- Default orthogonal and explicit-direction line–plane projections, vertex reflection, and selected affine transforms.
- Float counterparts for the analytic 2D and 3D primitives, generic parametric curves and surfaces, planar catenaries/spirals, Bézier/B-spline/NURBS curves and surfaces, cylinder/cone/torus shells and solids, geometry collections, CSG, and initial B-rep topology. Float lines and planes support projection, intersection, reflection, and affine transformations. Core float shapes provide deep-copy `clone()` methods.
- Lazy CSG membership for union, intersection, difference, and XOR of supported filled 3D regions.
- Initial identity-preserving B-rep topology (`TopoVertex3d`, oriented `TopoEdge3d`/`TopoEdgeUse3d`, `TopoWire3d`, `TopoFace3d`, `TopoShell3d`, `TopoSolid3d`, `TopoCompound3d`) with connectivity and opposite-oriented edge-incidence validation. Geometric and topological types stay separate.
- Geometry collections for mixed and disconnected 2D/3D components, plus `MultiVertex2d/f` and `MultiVertex3d/f`.
- Explicit `@Move` ownership-transfer constructors and delegating copy constructors for core double-precision primitives and LCS frames. Value copies use `clone()`, while returned independent values carry `@Owned`. Double-precision ellipse semiaxes and parabola coefficients retain their lengths instead of being reduced to unit directions.

## API ownership and testing

- Passing `Vertex2d`/`Axis2d` (or their 3D equivalents) to a concrete `@Move` overload transfers the supplied instance. Cast a value to its read-only `*c` interface to select the copying overload when both signatures apply.
- Methods with a destination argument return that **same** destination. Methods allocating a result use `@Owned`; `clone()` returns a new independent value for mutable point, axis, and LCS types.
- JUnit tests are split into `GeometryCoreTest`, `MathSolverTest`, `Primitives2dTest`, `ValueAndResultTest`, `AdvancedSolids3dTest`, and `FloatParityTest`. The latter covers typed JOML methods, solver widening, float primitives, frame transforms, intersections, splines, solids, CSG, and topology.

## Not yet implemented from `SPEC_ARCH.md`

The full specification is larger than a single reliable implementation pass. Not included yet:

- General symbolic, multi-quadratic, cubic/quartic and nonlinear intersection solvers. One quadratic plus linear equalities is supported; other nonlinear systems return `UnresolvedSolution` rather than fabricating geometry.
- Full float/double parity for specialized editing, transformation, and advanced geometry operations. The common double and float primitive/spline/CSG/topology families now exist, but some advanced intersection and transform overloads remain double-only.
- Full spline editing, knot insertion, continuity analysis, trimmed spline patches, generalized quadrics, ellipsoids, and higher-order solids.
- Full geometric B-rep validation (geometric continuity, UV trims, face embedding, manifold/self-intersection checks), topology reconstruction from equations, Boolean boundary evaluation, meshing, and import/export. `TopoShell3d.isClosedTopologically()` **only** checks edge incidence: it does not prove that the faces bound a valid volume.
- General constraint clipping of nonlinear and higher-dimensional positive-dimensional sets to curves, faces, or disconnected regions; supported one-dimensional *linear* intervals are clipped. `ConstrainedSolution` preserves other restrictions instead of falsely classifying a sample point.
- Global exact geometric classification of arbitrary implicit result sets.

Keep adding those as independent tested milestones; do not treat an unimplemented solver family as an empty intersection.

## Design notes

`ImplicitRepresentable` and `ParametricRepresentable` are independent capabilities. Equation solvers work on equations and constraints, not runtime geometry types. Primitive-specific methods own result interpretation into precise `ResultN` variants. A 3D line has one parameter; a surface two; a solid three. `Sphere3d` is the **shell**, `Ball3d` the **solid**.

Java 17 supports sealed classes, records, and `instanceof` patterns. Type-pattern `switch` still requires preview flags on Java 17, so this module uses ordinary `instanceof` patterns and stable switch expressions.

All topology containers copy their element lists; `TopoVertex3d.position()` returns a copy. A `TopoEdge3d` does retain its caller-provided parametric support object, which should not be mutated after topology construction. Current topology validation enforces shared vertex **identity**, not merely spatial coincidence. Surface tangents fall back to finite differences, so singular chart points (such as sphere poles) may not have a unique frame.

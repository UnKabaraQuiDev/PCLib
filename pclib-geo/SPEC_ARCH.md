# PCLib Geometry — Architecture Specification

**Status:** Proposed architecture; no implementation is included.  
**Target module:** `lu.kbra:pclib-geo`  
**Java:** 17 (this module overrides the parent PCLib Java 8 setting)  
**Primary mathematical library:** `org.joml:joml`  
**PCLib dependency:** `pclib-datastruct` for the existing read-only tuples

## 1. Objectives

Build a typed, extensible 2D/3D geometry module supporting points, axes, lines, rays, segments, planes, analytic curves and surfaces, filled regions, solids, arbitrary parametric geometry, and eventually composite/Boolean geometry and boundary representation (B-rep).

Key rules:

1. Follow JOML conventions: `d`/`f` precision suffixes; `c` read-only interfaces; mutable concrete implementations without `c`; destination-object overloads.
2. Keep geometry-specific methods and precise `ResultN` alternatives. Do **not** declare a universal `projectOnto`, `intersection`, or `reflectAround` on the root `Geometry2dc`/`Geometry3dc` interface.
3. Support **parametric and implicit equation representations** as independently implementable capabilities. A shape may implement either or both; not all arbitrary shapes admit a useful closed-form representation of both kinds.
4. Express boundaries, inequalities, and parameter domains as mathematical constraints.
5. Solvers return **mathematical solution sets**, never PCLib shape objects. The calling geometry interprets the solutions and constructs the appropriate typed geometry result.
6. Do not use a universal `ProjectionUtils`/`IntersectionUtils` with large geometry-type `instanceof` chains. Share equation-solving algorithms, numerical primitives, and typed delegation—not giant shape dispatchers.
7. Separate a geometric locus (curve/surface) from its topological use as an edge/face/shell/solid.
8. Prefer exact analytic algorithms where practical, then polynomial solvers, then controlled numerical fallbacks. Never claim all solutions were found unless completeness is established.

## 2. Module and package layout

Initially use one Maven module, `pclib-geo`:

```text
lu.kbra.pclib.geo
├── math                 Vertex, Axis, LCS, affine frames, precision
├── equation             Scalar equations, parametric maps, expression trees
│   ├── implicit         Implicit equations and systems
│   ├── parametric       Parametric maps and derivatives
│   ├── constraint       Constraints, domains, logic/composition
│   └── solver           Mathematical systems and solution types
├── result               Result2..Result8, Empty
├── shape                Geometry2/3, categories, capability interfaces
├── two
│   ├── curve            2D open/closed curves and bounded edges
│   ├── region           2D filled geometry
│   └── composite        Multiple 2D components
├── three
│   ├── curve            3D curves
│   ├── surface          Analytic and parametric surfaces
│   ├── face             Bounded surface patches
│   ├── shell            Connected/open/closed surface collections
│   ├── solid            3D volume geometry
│   └── composite        Multiple 3D components
├── topology             B-rep vertex/edge/wire/face/shell/solid graph
├── implicit             General implicit shape representations
└── csg                  Lazy Boolean-expression objects (future)
```

Potential later modules: `pclib-geo-boolean`, `pclib-geo-mesh`, `pclib-geo-io`. Keep the initial release independent of AWT, Swing, native libraries, and rendering engines.

**Java 17 note:** Pattern matching with `instanceof` and sealed classes is standard in Java 17. **Type-pattern `switch` is only a preview feature in Java 17** and requires `--enable-preview` for compilation and execution. Avoid preview-dependent public APIs and prefer `instanceof` patterns or switching on explicit result tags. If production type-pattern switches are mandatory without preview, Java 21 is the appropriate baseline. Do not silently enable preview in a library published for Java 17.

## 3. Geometric type hierarchy and metadata

All mutable shape implementations have corresponding read-only interfaces.

```text
Geometry2dc
├── Vertex2dc
├── Curve2dc             Line, Ray, Segment, Circle, Ellipse, conics, splines
└── Region2dc            Disk, FilledEllipse, Polygon, Rectangle, etc.

Geometry3dc
├── Vertex3dc
├── Curve3dc             Line, Ray, Segment, Circle, Helix, splines
├── Surface3dc           Plane, Sphere, CylinderSurface, etc.
├── Face3dc              Trimmed planar/curved surface region
├── Shell3dc             Collection of oriented faces/surfaces
└── Solid3dc             Ball, Box, CylinderSolid, etc.
```

Every geometry exposes consistent **derived, non-arbitrarily-mutable** metadata:

- `ambientDimension()` (`2` or `3`)
- `intrinsicDimension()` (`0..ambientDimension`)
- `geometryKind()` (`POINT`, `CURVE`, `SURFACE`, `REGION`, `SOLID`, `COMPOSITE`)
- `topologyKind()` where meaningful (`VERTEX`, `EDGE`, `WIRE`, `FACE`, `SHELL`, `SOLID`, `COMPOUND`)
- `isBounded()`, `isClosed()`, `isConnected()`, `isFilled()` where meaningful
- bounds, classification, measure, and other methods only where defined

Do not allow, for example, a `Circle3d` constructor to declare itself a `SOLID`. Separate `Circle3d` (1D curve), `Disk3d` (2D planar face), `Sphere3d` (2D spherical shell), and `Ball3d` (3D filled volume). A geometric shape and a topological face/edge are distinct concepts.

## 4. Mathematical primitives and JOML integration

| Read-only interface | Mutable implementation | JOML base |
|---|---|---|
| `Vertex2dc` | `Vertex2d` | `Vector2dc` / `Vector2d` |
| `Vertex3dc` | `Vertex3d` | `Vector3dc` / `Vector3d` |
| `Vertex2fc` | `Vertex2f` | `Vector2fc` / `Vector2f` |
| `Vertex3fc` | `Vertex3f` | `Vector3fc` / `Vector3f` |
| `Axis2dc` | `Axis2d` | `Vector2dc` / `Vector2d` |
| `Axis3dc` | `Axis3d` | `Vector3dc` / `Vector3d` |
| `Axis2fc` | `Axis2f` | `Vector2fc` / `Vector2f` |
| `Axis3fc` | `Axis3f` | `Vector3fc` / `Vector3f` |

`Vertex` means **position**; `Axis` means **direction**, with no positional anchor. Require nonzero, normalized axes at geometry boundaries. Because mutable JOML vectors can be altered, validate or defensively copy when the invariants matter. Provide typed overloads accepting `Vertex*d`/`Axis*d` destinations and covariant returns where Java/JOML signatures allow them. It is impossible to override every JOML method taking a plain `Vector3d dest` to return `Vertex3d` safely, since that destination need not be a vertex.

Provide `LCS2dc/LCS2d`, `LCS2fc/LCS2f`, `LCS3dc/LCS3d`, and `LCS3fc/LCS3f`. An LCS is an origin plus orthonormal axes. For oriented 3D surfaces, local Z equals the normal; X and Y span the tangent plane. A 3D line alone does not determine a unique roll around its direction, so require a reference axis or a documented deterministic convention. General shear/nonuniform scale uses an affine frame rather than silently corrupting an orthonormal LCS.

Useful LCS operations: `toLocal`, `toWorld`, `origin`, `xAxis`, `yAxis`, `zAxis`, `normal`, `matrix`, `inverse`, `isOrthogonal`, `isRightHanded`, `transformed` (subject to representability).

## 5. Equation and representation API

### 5.1 Naming and capabilities

Use **`ParametricRepresentable`** and **`ImplicitRepresentable`** (not `ParametricDefined` or `ImplicitMathDefined`). The names indicate available *representations*, not exclusive kinds of geometry.

Conceptual API contracts (signatures only; no implementation):

```java
interface ParametricRepresentable<E> {
    E parametricEquation();
}

interface ImplicitRepresentable<E> {
    E implicitEquation();
    MathConstraint[] constraints();  // empty when unconstrained
}
```

Shape-specific interfaces supply typed `at(...)` and derivative methods. A default implementation should delegate to `parametricEquation()`; a concrete shape may override it with a faster analytic implementation.

Use `implicitEquation()` as the canonical method name. `cartesianEquation()` may be an alias for familiar analytic types; it should not be a separate model. `parametricEquation()` is distinct from `implicitEquation()`.

### 5.2 Dimension-specific equation names

Do not use an ambiguous `ParametricEquation3d` alone: this says nothing about the number of parameters. Prefer **parameter dimension → ambient dimension** naming:

| Proposed interface | Mathematical mapping | Typical use |
|---|---|---|
| `ParametricEquation1to2dc` | R → R² | 2D curve |
| `ParametricEquation1to3dc` | R → R³ | 3D curve |
| `ParametricEquation2to2dc` | R² → R² | 2D filled region |
| `ParametricEquation2to3dc` | R² → R³ | 3D surface |
| `ParametricEquation3to3dc` | R³ → R³ | 3D solid/interior |
| `ImplicitEquation2dc` | F: R² → R, F = 0 | 2D implicit locus |
| `ImplicitEquation3dc` | F: R³ → R, F = 0 | 3D implicit locus |
| `ImplicitSystem2dc` | F₁=0, ..., Fₘ=0 in R² | Multiple constraints |
| `ImplicitSystem3dc` | F₁=0, ..., Fₘ=0 in R³ | Line as two plane equations |

Provide `f` variants and typed mutable equivalents when the representation has mutable state. A general internal `EquationSystemN` may support arbitrary N-dimensional systems; public 2D/3D overloads should remain convenient and statically typed.

An **implicit equation** is a scalar zero relation. An **implicit system** is a conjunction of one or more such relations. A line in R³ is normally an `ImplicitSystem3dc` of two independent plane equations, not one equation. A solid ball's interior is an inequality; equality alone describes only its shell.

Internally represent expressions structurally (constants, variables, arithmetic, powers, standard functions) where symbolic analysis is wanted. Also permit evaluatable function-backed equations for arbitrary numeric geometry. A raw `String` must not be the authoritative mathematical representation.

### 5.3 Parameterized geometry interfaces

Use dimension-specific capabilities so `at` always has the correct number of parameters:

```text
ParametricCurve2dc      -> at(double t) / at(double t, Vertex2d dest)
ParametricCurve3dc      -> at(double t) / at(double t, Vertex3d dest)
ParametricRegion2dc     -> at(double u, double v) / destination overload
ParametricSurface3dc    -> at(double u, double v) / destination overload
ParametricSolid3dc      -> at(double u, double v, double w) / destination overload
```

These expose a typed `parametricEquation()`, and appropriate `parameterDomain()` and derivative methods. For a `Sphere3dc`, `at(u,v)` lies **on the shell**. For a `Ball3dc`, `at(u,v,r)` lies in the volume with `0 ≤ r ≤ radius`. A 3D line still needs only `at(t)`; ambient dimension must not dictate parameter count. `Vertex.at()` needs no parameter.

Add `normalAt`, `tangentAt`, `tangentUAt`, `tangentVAt`, `binormalAt`, curvature, and `localCoordinateSystemAt` **only where mathematically meaningful**. True cotangents are dual covectors, not synonyms for tangent V. Deal explicitly with singularities (sphere poles, zero curvature, zero-gradient implicit surfaces).

### 5.4 Both representations are optional capabilities

Examples:

| Shape | Parametric | Implicit |
|---|---|---|
| Line3d | `origin + t * direction` | Two independent linear equations |
| Plane3d | `origin + u*X + v*Y` | One linear equation |
| Circle3d | Angular map | Plane equation plus quadratic equation |
| Sphere3d | Two angles | Quadratic equality |
| Ball3d | Angles plus radius | Quadratic inequality |
| Box3d | Three bounded local coordinates | Six half-space inequalities |
| TanhCurve3d | Analytic parametric function | Not necessarily a simple closed-form system |
| NURBS surface | Piecewise rational map | General implicitization may be expensive or impractical |
| Compound | Piecewise component domains | Boolean combination of child predicates |

Do **not** require artificial implementations of a representation that is unavailable or impractical. A shape implements the appropriate capability interfaces; optional introspection can report the available representations.

## 6. Constraints and domains

Use the name **`MathConstraint`** for a mathematical predicate, with `constraints()` returning an immutable array/snapshot or immutable list. Prefer plural `constraints()` over singular `constraint()`.

Supported relations: `=`, `≠`, `<`, `≤`, `>`, `≥`; parameter bounds; periodic domains; and, for complex sets, `AND`, `OR`, and `NOT`. Constraints may involve world variables (`x,y,z`) or shape parameter variables (`t,u,v,w`); these namespaces must not be conflated.

Examples:

- Ray: parameter `t ≥ 0`.
- Segment: `0 ≤ t ≤ 1`.
- Half-plane: world coordinate inequality `a*x + b*y + c ≥ 0`.
- Solid ball: `(x-cx)²+(y-cy)²+(z-cz)² ≤ r²`.
- Annulus: `rInner² ≤ (x-cx)²+(y-cy)² ≤ rOuter²`.
- Sphere shell: equality to `r²` rather than inequality.
- Polygon with holes: a compound set of inside/outside predicates; a flat conjunction alone may not express every shape.

Keep a distinction between **coefficients/constants of equations** (e.g., radius) and **constraints** (inequalities/domains). Constants do not automatically imply additional constraints.

The solver should incorporate constraints *during* the solution analysis and verify them afterward. Merely testing one representative point is incorrect: intersecting an infinite line with a half-space may yield a ray, not a whole line or nothing. Track strict versus inclusive boundaries and a defined numerical precision policy.

## 7. Mathematical solvers and result model

### 7.1 Solver responsibility

Solvers accept equations, constraints, coordinate domains, and precision settings. **They must not import or construct** `Vertex`, `Line`, `Circle`, `Sphere`, or other geometry classes.

Suggested solver strata:

1. `LinearSystemSolver`: rank, consistency, affine null-space, degeneracy.
2. `PolynomialSystemSolver`: polynomial substitution/elimination; finite roots and positive-dimensional solution descriptions.
3. `NonlinearSystemSolver`: iterative numerical solving with convergence and completeness metadata.
4. `ParametricIntersectionSolver`: solve equality of parametric maps with domain restrictions; handle periodicity, multiple branches, and numeric continuation.

`EquationSolver` may be a common **mathematical** service, but must not be a `solve(Shape,Shape)` switch over geometry types. Each capability supplies equations/constraints, and the shape-specific caller chooses the appropriate mathematical solver or specialized analytic route.

### 7.2 Linear solutions

For `A*x = b`, compare ranks of `A` and the augmented matrix `[A|b]`:

| Condition in ambient R³ | Mathematical result |
|---|---|
| Inconsistent ranks | No solution |
| Consistent rank 3 | One point (0D affine subspace) |
| Consistent rank 2 | 1D affine solution space |
| Consistent rank 1 | 2D affine solution space |
| Consistent rank 0 | All R³ |

A consistent solution has general form `x = p + Σ(ti * vi)`, where the `vi` vectors span the null space. The linear solver returns coefficients/basis/metadata, not a `Line3d` or `Plane3d`.

### 7.3 Solver result hierarchy

Prefer a **sealed** mathematical solution hierarchy (Java 17) with explicit tags. Suggested types:

```text
MathSolution
├── NoSolution
├── FiniteSolutions          one or more mathematical coordinate tuples
├── AffineSolution           base vector + null-space basis
├── ParametricSolution       one or more parameterized solution branches
├── ImplicitSolution         residual equations / constraints describing a set
├── CompositeSolution        multiple components/branches
├── ApproximateSolution      bounds, residuals, confidence/completeness status
└── UnresolvedSolution       unsupported, nonconvergent, or incomplete
```

An actual implementation may organize these as orthogonal properties rather than exactly these subclasses. Avoid conflating *no solution* with *solver failure* or *not proven complete*. Results must track solution dimension, exact/approximate status, whether all branches were found, and applicable domains where known. A singleton root must not be mistaken for a whole curve just because there are fewer equations than unknowns: nonlinear geometry can collapse dimensions.

### 7.4 Geometric interpretation stays with the caller

Concrete operations continue to declare **precise public geometric results**:

```java
Line3dc#intersection(Plane3dc):
    Result3<@Owned Line3d, @Owned Vertex3d, Empty>

Line3dc#projectOnto(Plane3dc):
    Result2<@Owned Line3d, @Owned Vertex3d>

Line3dc#projectOnto(Plane3dc, Axis3dc direction):
    Result2<@Owned Line3d, @Owned Vertex3d>

Sphere3dc#intersection(Plane3dc):
    Result3<@Owned Circle3d, @Owned Vertex3d, Empty>

Ball3dc#intersection(Plane3dc):
    Result3<@Owned Disk3d, @Owned Vertex3d, Empty>
```

These signatures describe the intended API, not Java source declarations. Specific methods obtain the mathematical solution, verify source domains, and reconstruct the known admissible geometry. The same operation may use a direct analytic algorithm when that is faster, more precise, or able to classify special cases better. A generic capability-level operation may expose `solveIntersection(ImplicitRepresentable<?> other): MathSolution` but must **not** pretend it can statically return every possible `ResultN` geometry alternative.

Type-specific code should use sealed-result inspection, normal Java 17 `instanceof` patterns, or an explicit solution tag. Avoid Java 17 preview type-pattern `switch` unless preview compilation/runtime is intentionally enabled.

### 7.5 Complex solutions

Provide `MultiVertex2/3d`, `MultiCurve2/3d`, `GeometryCollection2/3d`, `MultiSurface3d`, and `MultiSolid3d` for operations producing several disconnected or differently dimensional components. Keep multi-component mathematical solutions distinct from reconstructed geometry. A nonlinear solver may return a valid *implicit equation of the intersection* without being able to identify a named primitive; the caller must not fabricate a precise class when that identification is unproven.

## 8. ResultN, Empty, and ownership

`Result2<A,B>` extends existing `ReadOnlyPair<A,B>`; `Result3<A,B,C>` extends `ReadOnlyTriplet<A,B,C>`; continue through available tuple arities (`Result8` if the existing tuple infrastructure supports it).

- Exactly **one** alternative is selected; `selectedIndex()`, `selectedValue()`, and typed static factories `first`, `second`, etc., are authoritative.
- Active value must not be null. Inactive tuple fields may be null for compatibility, but `hasKey`/`hasValue` must not be used to infer the selected alternative.
- `clone`/mapping must preserve the result tag or document when they deliberately return plain tuples.
- `Empty.INSTANCE` is a singleton for a **valid geometric operation with no geometric result**. `Optional<ResultN>` is not necessary for this case. `NoSolution` is the corresponding **mathematical** solver outcome.
- Undefined operations (e.g., projection direction parallel to target plane) and unresolved numerical calculations are not the same as `Empty`.
- `@Owned` marks newly returned caller-owned objects, including selected generic result payloads. `@Move` transfers ownership of a stored parameter. Destination objects are caller-owned and are **not** moved merely because a method fills them.
- Shared B-rep topology needs stable ownership and identity; do not expose mutable shared vertices without a documented borrowing/update model. Consider a `@Borrowed` annotation for non-owning accessors.

## 9. Geometry operations and dispatch policy

Declare `intersection`, `projectOnto`, `reflectAround`, `rotateAround`, and specific geometric methods **only on the types for which their arguments and outcomes are defined** (`Line3dc`, `Plane3dc`, etc.), not on root interfaces. A specific interface may have multiple overloads; new shapes may add their own.

- Default `projectOnto(Plane3dc)` uses the target plane's normal.
- `projectOnto(Plane3dc, Axis3dc direction)` uses the explicit direction. If direction is parallel to target plane, projection can be undefined; do not return `Empty` to hide this.
- Line-plane intersection returns coincident line, one point, or empty. Sphere-plane returns circle, tangent point, or empty. Ball-plane returns disk, tangent point, or empty.
- Reflection around a 2D line or 3D plane is ordinary mirror reflection; reflection around a 3D line is defined as a 180° axial rotation; around a point is central inversion.
- For symmetric operations, one specific implementation may delegate to the other; avoid duplicate algorithms.
- For implicit-capable operands, the shared equation-solving path is available; shape-specific methods remain responsible for typed conversion and specialized fast paths.
- Operations that are **not equations or are not suitably defined by them** remain explicitly implemented on the owning shapes. Examples: reflections, affine transformations, direct distance formulas, shortest-point projection, arc length, tessellation, and specialized intersection/classification algorithms.

## 10. Parameter evaluation and local frames

Use the intrinsic dimension to determine parameter arity. Support a destination overload for every allocating `at(...)`, and analogous methods for frames and derivatives where defined:

- Curve: `at(t)`, `tangentAt(t)`, `localCoordinateSystemAt(t)`.
- Surface: `at(u,v)`, `tangentUAt(u,v)`, `tangentVAt(u,v)`, `normalAt(u,v)`, `localCoordinateSystemAt(u,v)`.
- Solid: `at(u,v,w)` for a defined volume parameterization; boundary normals belong to its boundary/surface.
- `localCoordinateSystem()` and `localCoordinateSystem(LCS* dest)` provide a reference frame.
- `localCoordinateSystemAt(...,LCS* dest)` provides a frame at the evaluated parameter location.

For every domain, define angle units (radians), orientation, periodicity, inclusive endpoints, singularities, and whether parameters are world distances or normalized. A plane uses `origin + u*X + v*Y`; a sphere shell uses two angular parameters at its fixed radius; a ball uses two angles plus bounded radius.

## 11. Transformations

Offer mutating `transform(...)` and allocating `transformed(...)` following JOML conventions, with precise/covariant results where possible. Matrix types:

| Ambient space | Affine | General homogeneous |
|---|---|---|
| 2D double/float | `Matrix3x2dc` / `Matrix3x2fc` | `Matrix3dc` / `Matrix3fc` |
| 3D double/float | `Matrix4x3dc` / `Matrix4x3fc` | `Matrix4dc` / `Matrix4fc` |

**Do not promise same-concrete-type output for arbitrary transforms:** nonuniform scaling maps a circle to an ellipse and a sphere to an ellipsoid; shear maps a box to a parallelepiped. Typed rigid/similarity transforms can preserve type. An in-place transform must reject transformations that cannot be represented without changing concrete type, leaving the original unchanged. A broad transformed overload can return a wider type or a mathematically representable general conic/quadric. Projective transforms need separate degeneracy/infinity handling. Normals transform using inverse-transpose of the linear part, not as positions.

## 12. Primitive catalogue and representations

| Family | Primitives | Preferred representation/algorithm |
|---|---|---|
| Linear (2D/3D) | Vertex, Line, Ray, Segment, Polyline, Loop; Plane (3D) | Anchor/direction or endpoints; linear implicit system; bounded parameter domains |
| Circular/conic (2D/3D) | Circle, Arc, Ellipse, EllipticArc, Parabola, ParabolicArc, Hyperbola, HyperbolicArc, GeneralConic | Analytic parameterization; quadratic equations, explicit interval constraints |
| Other curves (2D/3D where applicable) | TanhCurve, Catenary, ArchimedeanSpiral, LogarithmicSpiral, Clothoid, Helix, Bezier, Hermite, CatmullRom, BSpline, NURBS | Analytic/parametric mapping and derivatives; domain-aware numerical fallback |
| 2D filled | Disk, FilledEllipse, Triangle, Rectangle, Parallelogram, RoundedRectangle, PolygonWithHoles, RegularPolygon, Annulus, CircularSector, CircularSegment, FilledPath | Inequality/region membership; oriented loops and constraints |
| 3D surfaces | Plane, Sphere, EllipsoidSurface, CylinderSurface, ConeSurface, TorusSurface, EllipticParaboloid, HyperbolicParaboloid, Hyperboloid, GeneralQuadric | Analytic parameterization and implicit equations/gradients |
| Parametric surfaces | RuledSurface, SurfaceOfRevolution, ExtrudedSurface, SweptSurface, BezierSurface, BSplineSurface, NURBSSurface, HeightField | Parametric maps, partial derivatives, trimming domains |
| 3D planar faces | Disk3d, FilledEllipse3d, PlanarPolygon3d | Supporting plane + planar region/domain |
| 3D solids | Ball, EllipsoidSolid, AABB, OrientedBox, Parallelepiped, CylinderSolid, ConeSolid, Frustum, Prism, Pyramid, Capsule, SolidTorus, Wedge, Tetrahedron, ConvexPolyhedron, Polyhedron | Volume membership constraints, bounded surfaces, topology when required |
| Procedural/composite | ParametricCurve, ParametricSurface, ParametricSolid, ImplicitCurve, ImplicitSurface, Mesh, Compound, MultiGeometry, CSGSolid | Functions/expressions and constraints; component composition |

Use `Sphere3d` strictly for the surface and `Ball3d` for the filled volume. A general 3D ellipse is a planar curve with two semi-axis vectors; its filled planar region is `FilledEllipse3d`. A 3D `Circle3d` remains an edge/curve, not a face.

For `TanhCurve`, a useful general affine-friendly form is `P(t)=O+A*t+B*tanh(k*(t-t0))`, with exact derivatives where defined. Bézier curves use de Casteljau/derivative control polygons; B-splines/NURBS use knot-span/de Boor evaluation. General implicit geometry uses evaluated scalar fields and gradients; symbolic expressions are optional, not mandatory for function-defined shapes.

## 13. Topology, compounds, and Boolean operations (future)

Use a separate B-rep graph, distinct from mathematical geometry:

```text
TopoCompound / Assembly
└── TopoSolid
    └── TopoShell
        └── TopoFace         support surface + trimming wires
            └── TopoWire     ordered, oriented edges
                └── TopoEdge support curve + parameter interval
                    └── TopoVertex  stable vertex identity + position
```

A `TopoEdge` may refer to a line, circular arc, Bézier, or trimmed NURBS; a `TopoFace` may refer to a plane or trimmed curved surface. Store surface trimming curves in local UV coordinates when applicable. Track orientation, adjacency, manifoldness, loops/holes, connectivity, and component identity. A compound need not be a connected or valid solid.

Boolean operations (union, intersection, difference, XOR, complement relative to a universe) have two modes:

1. **Lazy CSG**: expression trees of set operations and constraints; classify points and compose implicit predicates without immediately building boundaries.
2. **Evaluated topology**: split/classify edges and faces, reconstruct wires/shells/solids and retain exact or approximated geometry as supported.

An equation/constraint solver supports both modes but does **not** replace topology reconstruction. Stage implementation: 2D regions → polyhedral 3D → curved analytic faces → general trimmed surfaces.

## 14. Numerical and mathematical contracts

- `GeometryPrecision` should cover absolute distance, relative distance, angular tolerance, and parameter tolerance separately.
- Distinguish exact, approximate, incomplete, and unresolved solver results.
- Use rank-revealing QR/SVD for linear degeneracy; do not infer consistency from a naive determinant or exact float equality.
- A valid infinite solution may become a bounded ray, segment, face, or region after applying constraints. Interpret **the entire constrained solution set**, not a sample point.
- Nonlinear systems may have isolated points even when equation count suggests free dimensions; they may also contain multiple branches or singular components.
- Avoid claiming that generic numerical root-finding enumerates every intersection.
- Transformations and geometry constructors must preserve invariants or report failure without partial mutation.
- Tests must cover coincident/parallel/tangent/degenerate cases, constraints, disconnected intersections, parameter singularities, float-vs-double consistency, and solver completeness metadata.
- Property tests: double reflection is identity; projection onto a plane is idempotent when defined; symmetric intersections agree geometrically; LCS local/world conversion round-trips; normals are perpendicular to valid tangents; translated intersections transform consistently.

## 15. Suggested implementation phases

1. **Foundations:** JOML value types, axes, LCS, precision, ownership, `ResultN`/`Empty`, geometry interfaces.
2. **Equation model:** parameter maps, implicit scalar equations/systems, expressions, world/parameter constraints, domains, sealed `MathSolution` types.
3. **Linear solver and geometry:** lines/rays/segments/planes, rank/null-space, typed intersections, projections/reflections.
4. **Conics/quadrics:** circles/ellipses/spheres/balls/cylinders/cones and direct analytic classification with polynomial fallback.
5. **Parametric curves and surfaces:** tanh, parabolas, Bézier, splines, NURBS; derivatives, limits, numeric intersections.
6. **Regions and topology:** polygons with holes, faces, wires, shells, solids, multi-component collections.
7. **Boolean engine:** lazy CSG, polyhedral evaluation, then analytic curved topology.

## 16. Decisions to keep explicit

1. **Java 17** is required for `pclib-geo`; this module must not pretend to be compatible with Java 8 consumers. Java 17 type-pattern switches are preview-only.
2. `ParametricRepresentable`/`ImplicitRepresentable` are **capabilities**, not mutually exclusive types; names with mapping arity avoid ambiguity.
3. `implicitEquation()` is the canonical name; optional `cartesianEquation()` can be an alias.
4. `constraints()` combines explicit inequalities/domains with the equations. Use structural constraints with strict/inclusive semantics, not strings.
5. A solver returns **only mathematics**. Each shape's explicit typed operation constructs `ResultN<...>`; it may use specialized analytics instead.
6. `Empty.INSTANCE` means an empty **geometric** result; `NoSolution` means an empty **mathematical** solution; nonconvergence is neither.
7. No global shape-type `instanceof` dispatch. Java 17 pattern matching is suitable for interpreting the sealed *mathematical-result* hierarchy, with the switch preview caveat.
8. Geometry metadata, fill state, parameter dimension, equation capabilities, and topology role must remain distinct.

## References

- [PCLib repository](https://github.com/UnKabaraQuiDev/PCLib)
- [JOML](https://github.com/JOML-CI/JOML)
- [SymPy: linear and nonlinear systems](https://docs.sympy.org/latest/modules/solvers/solveset.html)
- [Apache Commons Math: linear algebra](https://commons.apache.org/proper/commons-math/userguide/linear)
- [Apache Commons Geometry: BSP trees and Boolean operations](https://commons.apache.org/proper/commons-geometry/tutorials/bsp-tree.html)
- [Oracle Java 17: Pattern Matching for `switch` (preview)](https://docs.oracle.com/en/java/javase/17/language/pattern-matching-switch.html)


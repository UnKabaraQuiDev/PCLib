package lu.kbra.pclib.geo.topology;

/** A boundary-representation node, distinct from its supporting geometry. */
public sealed interface TopoElement3d permits TopoVertex3d, TopoEdge3d, TopoWire3d, TopoFace3d, TopoShell3d, TopoSolid3d, TopoCompound3d {

	TopologyKind topologyKind();
}

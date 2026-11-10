package lu.kbra.pclib.geo.topology;

/** A boundary-representation node, distinct from its supporting geometry. */
public sealed interface TopoElement3f permits TopoVertex3f, TopoEdge3f, TopoWire3f, TopoFace3f, TopoShell3f, TopoSolid3f, TopoCompound3f {

	TopologyKind topologyKind();
}

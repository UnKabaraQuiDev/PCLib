package lu.kbra.pclib.geo.three.solid;

import org.joml.Vector3fc;

import lu.kbra.pclib.geo.Geometry3fc;

/** Membership in a filled volume, including its boundary. */
public interface SolidRegion3fc extends Geometry3fc {
	boolean contains(Vector3fc point);
}

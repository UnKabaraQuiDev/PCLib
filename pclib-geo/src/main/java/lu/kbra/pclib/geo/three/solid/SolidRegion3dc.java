package lu.kbra.pclib.geo.three.solid;

import org.joml.Vector3dc;

import lu.kbra.pclib.geo.Geometry3dc;

/** Membership in a filled volume, including its boundary. */
public interface SolidRegion3dc extends Geometry3dc {
	boolean contains(Vector3dc point);
}

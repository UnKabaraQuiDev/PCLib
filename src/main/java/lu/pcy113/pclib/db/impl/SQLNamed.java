package lu.pcy113.pclib.db.impl;

import lu.pcy113.pclib.PCUtils;

@FunctionalInterface
public interface SQLNamed {

	String getName();

	default String getQualifiedName() {
		return PCUtils.sqlEscapeIdentifier(getName());
	}

}

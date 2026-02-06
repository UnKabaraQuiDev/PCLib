package lu.kbra.pclib.db.impl;

import lu.kbra.pclib.PCUtils;

@FunctionalInterface
public interface SQLNamed {

	SQLNamed MOCK = () -> "[NAME]";

	String getName();

	default String getQualifiedName() {
		return PCUtils.sqlEscapeIdentifier(getName());
	}

}

package lu.kbra.pclib.db.impl;

import lu.kbra.pclib.db.base.DeferredDatabase;

public interface ProxySQLQueryable<T extends DatabaseEntry> extends SQLQueryable<T> {

	String METHOD_NAME = "ctor";

	void ctor(DeferredDatabase db);

}

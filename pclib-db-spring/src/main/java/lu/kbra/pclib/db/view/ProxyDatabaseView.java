package lu.kbra.pclib.db.view;

import lu.kbra.pclib.db.impl.DatabaseEntry;
import lu.kbra.pclib.db.impl.ProxySQLQueryable;

public interface ProxyDatabaseView<T extends DatabaseEntry> extends AbstractDBView<T>, ProxySQLQueryable<T> {

}

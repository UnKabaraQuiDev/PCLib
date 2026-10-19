package lu.kbra.pclib.db.table;

import lu.kbra.pclib.db.impl.DatabaseEntry;
import lu.kbra.pclib.db.impl.ProxySQLQueryable;

public interface ProxyDatabaseTable<T extends DatabaseEntry> extends AbstractDBTable<T>, ProxySQLQueryable<T> {

}

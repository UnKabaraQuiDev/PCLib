package lu.kbra.pclib.db;

import lu.kbra.pclib.db.impl.DataBaseEntry;
import lu.kbra.pclib.db.impl.SQLQueryable;

public interface DeferredSQLQueryable<T extends DataBaseEntry> extends SQLQueryable<T> {

}

package lu.kbra.pclib.db.table;

import lu.kbra.pclib.db.impl.DataBaseEntry;
import lu.kbra.pclib.db.impl.NTSQLQueryable;

public interface DeferredNTSQLQueryable<T extends DataBaseEntry> extends DeferredSQLQueryable<T>, NTSQLQueryable<T> {

}

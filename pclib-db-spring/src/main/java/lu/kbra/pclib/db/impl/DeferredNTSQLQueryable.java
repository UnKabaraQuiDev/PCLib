package lu.kbra.pclib.db.impl;

public interface DeferredNTSQLQueryable<T extends DataBaseEntry> extends DeferredSQLQueryable<T>, NTSQLQueryable<T> {

}

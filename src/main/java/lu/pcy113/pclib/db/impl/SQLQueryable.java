package lu.pcy113.pclib.db.impl;

public interface SQLQueryable<T extends SQLEntry> {

	String getName();
	
	String getQualifiedName();
	
}

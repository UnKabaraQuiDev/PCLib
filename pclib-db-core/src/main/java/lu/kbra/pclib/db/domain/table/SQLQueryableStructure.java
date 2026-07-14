package lu.kbra.pclib.db.domain.table;

import lu.kbra.pclib.db.domain.column.ColumnData;
import lu.kbra.pclib.db.impl.DatabaseEntry;
import lu.kbra.pclib.db.impl.SQLQueryable;
import lu.kbra.pclib.db.impl.SQLQueryableDependencyOwner;

public interface SQLQueryableStructure extends AbstractDBStructure, EntryHintsOwner, StructureNameOwner, SQLQueryableDependencyOwner {

	Class<? extends SQLQueryable<?>> getTargetClass();

	Class<? extends DatabaseEntry> getEntryClass();

	ColumnData[] getColumns();

	@Override
	default SQLQueryableDependency getKey() {
		return new SQLQueryableDependency(this.getTargetClass(), this.getName());
	}

	@Override
	String toString();

}

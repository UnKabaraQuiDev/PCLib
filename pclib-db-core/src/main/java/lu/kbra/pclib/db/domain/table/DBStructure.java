package lu.kbra.pclib.db.domain.table;

import lu.kbra.pclib.db.domain.column.ColumnData;
import lu.kbra.pclib.db.impl.DataBaseEntry;
import lu.kbra.pclib.db.impl.SQLQueryable;

public interface DBStructure {

	StructureName getStructureName();

	default String getName() {
		return getStructureName().getName();
	}

	default String[] getNameParts() {
		return getStructureName().getNameParts();
	}

	default String getQualifiedName() {
		return getStructureName().getQualifiedName();
	}

	Class<? extends SQLQueryable<?>> getTargetClass();

	Class<? extends DataBaseEntry> getEntryClass();

	ColumnData[] getPrimaryKeys();

}

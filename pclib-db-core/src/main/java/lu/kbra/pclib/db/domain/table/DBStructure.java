package lu.kbra.pclib.db.domain.table;

import lu.kbra.pclib.db.domain.column.ColumnData;
import lu.kbra.pclib.db.impl.DataBaseEntry;
import lu.kbra.pclib.db.impl.SQLQueryable;
import lu.kbra.pclib.db.impl.SQLQueryableDependencyOwner;

public interface DBStructure extends StructureNameOwner, SQLQueryableDependencyOwner, MapConvertible {

	Class<? extends SQLQueryable<?>> getTargetClass();

	Class<? extends DataBaseEntry> getEntryClass();

	ColumnData[] getColumns();

	@Override
	default SQLQueryableDependency getKey() {
		return new SQLQueryableDependency(this.getTargetClass(), this.getName());
	}

}

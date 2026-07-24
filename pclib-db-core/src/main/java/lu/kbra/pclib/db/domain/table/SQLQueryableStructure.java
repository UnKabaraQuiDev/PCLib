package lu.kbra.pclib.db.domain.table;

import java.util.Set;

import lu.kbra.pclib.db.domain.column.ColumnData;
import lu.kbra.pclib.db.impl.DatabaseEntry;
import lu.kbra.pclib.db.impl.SQLQueryable;
import lu.kbra.pclib.db.impl.SQLQueryableDependencyOwner;

public interface SQLQueryableStructure extends AbstractDBStructure, StructureNameOwner, SQLQueryableDependencyOwner {

	ColumnData[] getColumns();

	ConstraintData[] getConstraints();

	Class<? extends DatabaseEntry> getEntryClass();

	@Override
	default SQLQueryableDependency getKey() {
		return new SQLQueryableDependency(this.getTargetClass(), this.getName());
	}

	Class<? extends SQLQueryable<?>> getTargetClass();

	void setConstraints(ConstraintData[] array);

	@Override
	Set<SQLQueryableDependency> getDependencies();

	void setDependencies(Set<SQLQueryableDependency> dependencies);

	@Override
	String toString();

}

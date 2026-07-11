package lu.kbra.pclib.db.domain.view;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;
import lu.kbra.pclib.db.domain.table.StructureName;
import lu.kbra.pclib.db.domain.table.StructureNameOwner;
import lu.kbra.pclib.db.impl.SQLQueryable;
import lu.kbra.pclib.db.impl.SingleSQLQueryableDependencyOwner;

@Data
public class UnionTableStructure implements SingleSQLQueryableDependencyOwner, StructureNameOwner {

	private final String foreignName;
	private final Class<? extends SQLQueryable<?>> foreignClass;
	private final StructureName resolvedName;
	private final String condition;

	private final List<ViewColumnStructure> columns = new ArrayList<>();

	@Override
	public SQLQueryableDependency getKey() {
		throw new UnsupportedOperationException();
	}

	@Override
	public SQLQueryableDependency getDependency() {
		return new SQLQueryableDependency(foreignClass, resolvedName.getName());
	}
	
	@Override
	public StructureName getStructureName() {
		return resolvedName;
	}

}

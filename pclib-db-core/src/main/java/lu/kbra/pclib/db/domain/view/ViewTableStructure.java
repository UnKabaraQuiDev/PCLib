package lu.kbra.pclib.db.domain.view;

import java.util.ArrayList;
import java.util.List;

import lu.kbra.pclib.db.annotations.view.ViewTable;
import lu.kbra.pclib.db.domain.table.StructureName;
import lu.kbra.pclib.db.domain.table.StructureNameOwner;
import lu.kbra.pclib.db.impl.SQLQueryable;
import lu.kbra.pclib.db.impl.SingleSQLQueryableDependencyOwner;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ViewTableStructure implements SingleSQLQueryableDependencyOwner, StructureNameOwner {

	private final String foreignName;
	private final Class<? extends SQLQueryable<?>> foreignClass;
	private final StructureName resolvedName;
	private final String alias;
	private String on;
	private final ViewTable.Type joinType;
	private final boolean distinct;
	private final List<ViewColumnStructure> columns = new ArrayList<>();

	@Override
	public SQLQueryableDependency getKey() {
		throw new UnsupportedOperationException("No key.");
	}

	@Override
	public SQLQueryableDependency getDependency() {
		return new SQLQueryableDependency(this.foreignClass, this.resolvedName.getName());
	}

	@Override
	public StructureName getStructureName() {
		return this.resolvedName;
	}

}

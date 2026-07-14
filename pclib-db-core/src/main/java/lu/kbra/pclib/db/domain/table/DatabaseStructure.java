package lu.kbra.pclib.db.domain.table;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lu.kbra.pclib.datastructure.tree.dependency.DependencyTree;
import lu.kbra.pclib.db.domain.view.ViewStructure;
import lu.kbra.pclib.db.impl.SQLQueryable;
import lu.kbra.pclib.db.impl.SQLQueryableDependencyOwner.SQLQueryableDependency;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DatabaseStructure implements AbstractDBStructure {

	private String name;
	private String qualifiedName;
	private Map<String, Object> hints;
	private final Set<TableStructure> tableStructures = new HashSet<>();
	private final Set<ViewStructure> viewStructures = new HashSet<>();
	private DependencyTree<? extends SQLQueryable<?>, SQLQueryableDependency> dependencyTree;
	private final Map<String, SQLQueryableStructure> simpleNames = new HashMap<>();
	private final Map<String, Map<String, SQLQueryableStructure>> linkedNames = new HashMap<>();

	@Override
	public Map<String, Object> toMap() {
		final Map<String, Object> map = new HashMap<>();
		map.put("structureName", this.name);
		map.put("qualifiedName", this.qualifiedName);
		map.put("hints", this.hints);
		map.put("tableStructures", this.tableStructures);
		map.put("viewStructures", this.viewStructures);
		map.put("dependencyTree", this.dependencyTree);

		return map;
	}

	public SQLQueryableStructure getSimpleName(String simpleName) {
		return simpleNames.get(simpleName);
	}

	public SQLQueryableStructure getLinkedName(String tableClazz, String name) {
		return linkedNames.get(tableClazz).get(name);
	}

}

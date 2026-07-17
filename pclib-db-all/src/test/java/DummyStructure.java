import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import lu.kbra.pclib.db.domain.column.ColumnData;
import lu.kbra.pclib.db.domain.table.SQLQueryableStructure;
import lu.kbra.pclib.db.domain.table.StructureName;
import lu.kbra.pclib.db.impl.DatabaseEntry;
import lu.kbra.pclib.db.impl.SQLQueryable;
import lu.kbra.pclib.db.utils.impl.DatabaseEntryUtils;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DummyStructure implements SQLQueryableStructure {

	private final StructureName structureName;
	private final Class<? extends SQLQueryable<?>> targetClass;
	private final Class<? extends DatabaseEntry> entryClass;
	private final Map<String, Object> hints = new HashMap<>(), entryHints = new HashMap<>();
	private ColumnData[] columns;

	public DummyStructure(
			final DatabaseEntryUtils databaseEntryUtils,
			final Class<? extends SQLQueryable<?>> targetClass,
			final Class<? extends DatabaseEntry> entryClass) {
		final String[] namePart = databaseEntryUtils.getStructureVisitor().getQueryableNameParts(targetClass, Collections.emptyMap());
		this.structureName = new StructureName(Arrays.stream(namePart).collect(Collectors.joining(".")),
				namePart,
				databaseEntryUtils.getStructureVisitor().qualifiedName(namePart));
		this.targetClass = targetClass;
		this.entryClass = entryClass;
	}

	@Override
	public Set<SQLQueryableDependency> getDependencies() {
		return Collections.emptySet();
	}

	@Override
	public Map<String, Object> toMap() {
		final Map<String, Object> map = new HashMap<>();
		map.put("structureName", this.structureName);
		map.put("targetClass", this.targetClass);
		map.put("entryClass", this.entryClass);

		return map;
	}

}

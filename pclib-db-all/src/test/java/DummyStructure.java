import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import lombok.Getter;
import lu.kbra.pclib.db.domain.column.ColumnData;
import lu.kbra.pclib.db.domain.table.DBStructure;
import lu.kbra.pclib.db.domain.table.StructureName;
import lu.kbra.pclib.db.impl.DataBaseEntry;
import lu.kbra.pclib.db.impl.SQLQueryable;
import lu.kbra.pclib.db.utils.impl.DataBaseEntryUtils;

@Getter
public class DummyStructure implements DBStructure {

	private StructureName structureName;
	private Class<? extends SQLQueryable<?>> targetClass;
	private Class<? extends DataBaseEntry> entryClass;

	public DummyStructure(
			String name,
			DataBaseEntryUtils dataBaseEntryUtils,
			final Class<? extends SQLQueryable<?>> targetClass,
			Class<? extends DataBaseEntry> entryClass) {
		final String[] namePart = dataBaseEntryUtils.getStructureVisitor().getQueryableNameParts(targetClass, Collections.emptyMap());
		structureName = new StructureName(Arrays.stream(namePart).collect(Collectors.joining(".")),
				namePart,
				dataBaseEntryUtils.getStructureVisitor().qualifiedName(namePart));
		this.targetClass = targetClass;
		this.entryClass = entryClass;
	}

	@Override
	public Set<SQLQueryableDependency> getDependencies() {
		return Collections.emptySet();
	}

	@Override
	public ColumnData[] getColumns() {
		return new ColumnData[0];
	}

	@Override
	public Map<String, Object> toMap() {
		final Map<String, Object> map = new HashMap<>();
		map.put("structureName", structureName.toMap());
		map.put("targetClass", targetClass);
		map.put("entryClass", entryClass);

		return map;
	}

}
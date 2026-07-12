import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import lombok.Getter;
import lu.kbra.pclib.db.domain.column.ColumnData;
import lu.kbra.pclib.db.domain.table.SQLQueryableStructure;
import lu.kbra.pclib.db.domain.table.StructureName;
import lu.kbra.pclib.db.impl.DataBaseEntry;
import lu.kbra.pclib.db.impl.SQLQueryable;
import lu.kbra.pclib.db.utils.impl.DataBaseEntryUtils;

@Getter
public class DummyStructure implements SQLQueryableStructure {

	private final StructureName structureName;
	private final Class<? extends SQLQueryable<?>> targetClass;
	private final Class<? extends DataBaseEntry> entryClass;

	public DummyStructure(
			final String name,
			final DataBaseEntryUtils dataBaseEntryUtils,
			final Class<? extends SQLQueryable<?>> targetClass,
			final Class<? extends DataBaseEntry> entryClass) {
		final String[] namePart = dataBaseEntryUtils.getStructureVisitor().getQueryableNameParts(targetClass, Collections.emptyMap());
		this.structureName = new StructureName(Arrays.stream(namePart).collect(Collectors.joining(".")),
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
		map.put("structureName", this.structureName.toMap());
		map.put("targetClass", this.targetClass);
		map.put("entryClass", this.entryClass);

		return map;
	}

	@Override
	public Map<String, Object> getHints() {
		return Collections.emptyMap();
	}

}
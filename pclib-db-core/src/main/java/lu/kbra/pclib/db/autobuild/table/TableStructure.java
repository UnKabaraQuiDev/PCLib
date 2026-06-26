package lu.kbra.pclib.db.autobuild.table;

import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lu.kbra.pclib.PCUtils;
import lu.kbra.pclib.db.autobuild.SQLBuildable;
import lu.kbra.pclib.db.autobuild.column.ColumnData;
import lu.kbra.pclib.db.autobuild.dialect.SQLStructureVisitor;
import lu.kbra.pclib.db.autobuild.dialect.SQLStructureVisitors;
import lu.kbra.pclib.db.connector.impl.DataBaseConnector;
import lu.kbra.pclib.db.impl.DataBaseEntry;
import lu.kbra.pclib.db.table.AbstractDBTable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TableStructure implements SQLBuildable {

	@Deprecated
	public static String entryClassNameToTableName(String className) {
		if (className == null || className.isEmpty()) {
			return className;
		}

		if (className.toLowerCase().startsWith("ro")) {
			className = "ro" + className.substring(2);
		}

		if (className.toLowerCase().endsWith("rodata")) {
			className = "ro" + className.substring(0, className.length() - 6);
		} else if (className.toLowerCase().endsWith("data")) {
			className = className.substring(0, className.length() - 4);
		}

		return PCUtils.camelCaseToSnakeCase(className);
	}

	@Deprecated
	public static String entryClassToTableName(final Class<? extends DataBaseEntry> simpleName) {
		return TableStructure.entryClassNameToTableName(simpleName.getSimpleName());
	}

	public static String tableClassNameToTableName(final Class<? extends AbstractDBTable<?>> simpleName) {
		return TableStructure.tableClassNameToTableName(simpleName.getSimpleName());
	}

	public static String tableClassNameToTableName(String className) {
		if (className == null || className.isEmpty() || className.trim().isEmpty()) {
			return className;
		}

		if (className.toLowerCase().startsWith("ro")) {
			className = "ro" + className.substring(2);
		}

		if (className.toLowerCase().endsWith("rotable")) {
			className = "ro" + className.substring(0, className.length() - 7);
		} else if (className.toLowerCase().endsWith("table")) {
			className = className.substring(0, className.length() - 5);
		}

		return PCUtils.camelCaseToSnakeCase(className);
	}

	private String name;
	private Class<? extends AbstractDBTable<?>> tableClass;
	private Class<? extends DataBaseEntry> entryClass;
	private Map<String, Object> tableHints;
	private ColumnData[] columns;
	private ConstraintData[] constraints;

	public <T extends DataBaseEntry> TableStructure(
			final String name,
			final Class<? extends AbstractDBTable<T>> tableClass,
			final Class<T> entryClass,
			final Map<String, Object> tableHints) {
		this.name = name;
		this.tableClass = tableClass;
		this.entryClass = entryClass;
		this.tableHints = tableHints;
	}

	public TableStructure(
			final String name,
			final Map<String, Object> tableHints,
			final ColumnData[] columns,
			final ConstraintData[] constraints) {
		this.name = name;
		this.tableClass = null;
		this.entryClass = null;
		this.tableHints = tableHints;
		this.columns = columns;
		this.constraints = constraints;
	}

	public String accept(final SQLStructureVisitor visitor) {
		return visitor.visit(this);
	}

	@Override
	public String build(final DataBaseConnector connector) {
		return this.accept(SQLStructureVisitors.forConnector(connector));
	}

	public <V> V getTableHint(final String key) {
		return (V) this.tableHints.get(key);
	}

	public <V> V getTableHint(final String key, final V default_) {
		return (V) this.tableHints.getOrDefault(key, default_);
	}

	public <V> boolean hasTableHint(final String key) {
		return this.tableHints.containsKey(key);
	}

}

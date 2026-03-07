package lu.kbra.pclib.db.autobuild.table;

import java.util.ArrayList;
import java.util.List;

import lu.kbra.pclib.PCUtils;
import lu.kbra.pclib.db.autobuild.SQLBuildable;
import lu.kbra.pclib.db.autobuild.column.ColumnData;
import lu.kbra.pclib.db.connector.impl.CharacterSetCapable;
import lu.kbra.pclib.db.connector.impl.CollationCapable;
import lu.kbra.pclib.db.connector.impl.DataBaseConnector;
import lu.kbra.pclib.db.connector.impl.EngineCapable;
import lu.kbra.pclib.db.impl.DataBaseEntry;

public class TableStructure implements SQLBuildable {

	private String name;
	private Class<?> entryClass;

	private String characterSet;
	private String engine;
	private String collation;

	private ColumnData[] columns;
	private ConstraintData[] constraints;

	public TableStructure(final Class<?> entryClass) {
		this.name = classNameToTableName(entryClass.getSimpleName());
		this.entryClass = entryClass;
	}

	public TableStructure(final String name, final Class<?> entryClass) {
		this.name = name;
		this.entryClass = entryClass;
	}

	public TableStructure(final Class<?> entryClass, final ColumnData[] columns) {
		this.name = classNameToTableName(entryClass.getSimpleName());
		this.entryClass = entryClass;
		this.columns = columns;
	}

	public TableStructure(final String name, final ColumnData[] columns) {
		this.name = name;
		this.entryClass = null;
		this.columns = columns;
	}

	public TableStructure(final Class<?> entryClass, final ColumnData[] columns, final ConstraintData[] constraints) {
		this.name = classNameToTableName(entryClass.getSimpleName());
		this.entryClass = entryClass;
		this.columns = columns;
		this.constraints = constraints;
	}

	public TableStructure(final String name, final ColumnData[] columns, final ConstraintData[] constraints) {
		this.name = name;
		this.entryClass = null;
		this.columns = columns;
		this.constraints = constraints;
	}

	public static String classNameToTableName(String className) {
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

	public static String classToTableName(final Class<? extends DataBaseEntry> simpleName) {
		return PCUtils.camelCaseToSnakeCase(simpleName.getSimpleName());
	}

	public void update(final DataBaseConnector connector) {
		if ((this.collation == null || this.collation.isEmpty()) && connector instanceof CollationCapable) {
			this.collation = ((CollationCapable) connector).getCollation();
		}
		if ((this.characterSet == null || this.characterSet.isEmpty()) && connector instanceof CharacterSetCapable) {
			this.characterSet = ((CharacterSetCapable) connector).getCharacterSet();
		}
		if ((this.engine == null || this.engine.isEmpty()) && connector instanceof EngineCapable) {
			this.engine = ((EngineCapable) connector).getEngine();
		}
	}

	public String getName() {
		return this.name;
	}

	public void setName(final String name) {
		this.name = name;
	}

	public String getEscapedName() {
		return PCUtils.sqlEscapeIdentifier(this.name);
	}

	public Class<?> getEntryClass() {
		return this.entryClass;
	}

	public void setEntryClass(final Class<?> entryClass) {
		this.entryClass = entryClass;
	}

	public ColumnData[] getColumns() {
		return this.columns;
	}

	public ConstraintData[] getConstraints() {
		return this.constraints;
	}

	public void setColumns(final ColumnData[] columns) {
		this.columns = columns;
	}

	public void setConstraints(final ConstraintData[] constraints) {
		this.constraints = constraints;
	}

	public String getCharacterSet() {
		return this.characterSet;
	}

	public void setCharacterSet(final String characterSet) {
		this.characterSet = characterSet;
	}

	public String getEngine() {
		return this.engine;
	}

	public void setEngine(final String engine) {
		this.engine = engine;
	}

	public String getCollation() {
		return this.collation;
	}

	public void setCollation(final String collation) {
		this.collation = collation;
	}

	@Override
	public String build(final DataBaseConnector connector) {
		final StringBuilder sb = new StringBuilder();
		sb.append("CREATE TABLE ");
		sb.append(this.getEscapedName());
		sb.append(" (\n");

		final List<String> columnDefs = new ArrayList<>();
		for (final ColumnData col : this.columns) {
			columnDefs.add("  " + col.build(connector));
		}

		// TODO: rework this to support more dialects
		if (this.constraints != null) {
			for (final ConstraintData constraint : this.constraints) {
				columnDefs.add("  " + constraint.build(connector));
			}
		}

		sb.append(String.join(",\n", columnDefs));
		sb.append("\n)");

		if (this.characterSet != null && !this.characterSet.isEmpty()) {
			sb.append(" CHARACTER SET ").append(this.characterSet);
		}

		if (this.engine != null && !this.engine.isEmpty()) {
			sb.append(" ENGINE=").append(this.engine);
		}

		sb.append(";\n");

		return sb.toString();
	}

}

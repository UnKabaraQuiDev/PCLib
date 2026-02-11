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

public class TableStructure implements SQLBuildable {

	private String name;
	private Class<?> entryClass;

	private String characterSet, engine, collation;

	private ColumnData[] columns;
	private ConstraintData[] constraints;

	public TableStructure(Class<?> entryClass) {
		this.name = classNameToTableName(entryClass.getSimpleName());
		this.entryClass = entryClass;
	}

	public TableStructure(String name, Class<?> entryClass) {
		this.name = name;
		this.entryClass = entryClass;
	}

	public TableStructure(Class<?> entryClass, ColumnData[] columns) {
		this.name = classNameToTableName(entryClass.getSimpleName());
		this.entryClass = entryClass;
		this.columns = columns;
	}

	public TableStructure(String name, ColumnData[] columns) {
		this.name = name;
		this.entryClass = null;
		this.columns = columns;
	}

	public TableStructure(Class<?> entryClass, ColumnData[] columns, ConstraintData[] constraints) {
		this.name = classNameToTableName(entryClass.getSimpleName());
		this.entryClass = entryClass;
		this.columns = columns;
		this.constraints = constraints;
	}

	public TableStructure(String name, ColumnData[] columns, ConstraintData[] constraints) {
		this.name = name;
		this.entryClass = null;
		this.columns = columns;
		this.constraints = constraints;
	}

	public static String classNameToTableName(String className) {
		if (className == null || className.isEmpty()) {
			return className;
		}

		if (className.toLowerCase().endsWith("data")) {
			className = className.substring(0, className.length() - 4);
		}

		className = Character.toLowerCase(className.charAt(0)) + className.substring(1);

		return PCUtils.camelCaseToSnakeCase(className);
	}

	public void update(DataBaseConnector connector) {
		if ((collation == null || collation.isEmpty()) && connector instanceof CollationCapable) {
			collation = ((CollationCapable) connector).getCollation();
		}
		if ((characterSet == null || characterSet.isEmpty()) && connector instanceof CharacterSetCapable) {
			characterSet = ((CharacterSetCapable) connector).getCharacterSet();
		}
		if ((engine == null || engine.isEmpty()) && connector instanceof EngineCapable) {
			engine = ((EngineCapable) connector).getEngine();
		}
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getEscapedName() {
		return PCUtils.sqlEscapeIdentifier(name);
	}

	public Class<?> getEntryClass() {
		return entryClass;
	}

	public void setEntryClass(Class<?> entryClass) {
		this.entryClass = entryClass;
	}

	public ColumnData[] getColumns() {
		return columns;
	}

	public ConstraintData[] getConstraints() {
		return constraints;
	}

	public void setColumns(ColumnData[] columns) {
		this.columns = columns;
	}

	public void setConstraints(ConstraintData[] constraints) {
		this.constraints = constraints;
	}

	public String getCharacterSet() {
		return characterSet;
	}

	public void setCharacterSet(String characterSet) {
		this.characterSet = characterSet;
	}

	public String getEngine() {
		return engine;
	}

	public void setEngine(String engine) {
		this.engine = engine;
	}

	public String getCollation() {
		return collation;
	}

	public void setCollation(String collation) {
		this.collation = collation;
	}

	@Override
	public String build(DataBaseConnector connector) {
		StringBuilder sb = new StringBuilder();
		sb.append("CREATE TABLE ");
		sb.append(getEscapedName());
		sb.append(" (\n");

		List<String> columnDefs = new ArrayList<>();
		for (ColumnData col : columns) {
			columnDefs.add("  " + col.build(connector));
		}

		// TODO: rework this to support more dialects
		if (constraints != null) {
			for (ConstraintData constraint : constraints) {
				columnDefs.add("  " + constraint.build(connector));
			}
		}

		sb.append(String.join(",\n", columnDefs));
		sb.append("\n)");

		if (characterSet != null && !characterSet.isEmpty()) {
			sb.append(" CHARACTER SET ").append(characterSet);
		}

		if (engine != null && !engine.isEmpty()) {
			sb.append(" ENGINE=").append(engine);
		}

		sb.append(";\n");

		return sb.toString();
	}

}

package lu.pcy113.pclib.db.autobuild.table;

import lu.pcy113.pclib.PCUtils;
import lu.pcy113.pclib.db.autobuild.SQLBuildable;
import lu.pcy113.pclib.db.autobuild.column.ColumnData;

public class TableStructure implements SQLBuildable {

	private final String name;
	private final Class<?> entryClass;

	private ColumnData[] columns;
	private ConstraintData[] constraints;

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
		if (className == null || className.isEmpty())
			return className;

		if (className.toLowerCase().endsWith("data")) {
			className = className.substring(0, className.length() - 4);
		}

		className = Character.toLowerCase(className.charAt(0)) + className.substring(1);

		return PCUtils.camelToSnake(className);
	}

	public String getName() {
		return name;
	}

	public Class<?> getEntryClass() {
		return entryClass;
	}

	public ColumnData[] getColumns() {
		return columns;
	}

	public ConstraintData[] getConstraints() {
		return constraints;
	}

	@Override
	public String build() {
		return null;
	}

}

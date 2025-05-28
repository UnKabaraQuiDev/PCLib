package lu.pcy113.pclib.db.autobuild.column;

import lu.pcy113.pclib.db.autobuild.SQLBuildable;

public class ColumnData implements SQLBuildable {

	private String name;
	private ColumnType type;
	private boolean autoIncrement = false;
	private boolean nullable = true;
	private String defaultValue = null;

	public ColumnData() {
	}

	public ColumnData(String name, ColumnType type) {
		this.name = name;
		this.type = type;
	}

	public ColumnData(String name, ColumnType type, boolean autoIncrement, boolean nullable, String defaultValue) {
		this.name = name;
		this.type = type;
		this.autoIncrement = autoIncrement;
		this.nullable = nullable;
		this.defaultValue = defaultValue;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public ColumnType getType() {
		return type;
	}

	public void setType(ColumnType type) {
		this.type = type;
	}

	public boolean isAutoIncrement() {
		return autoIncrement;
	}

	public void setAutoIncrement(boolean autoIncrement) {
		this.autoIncrement = autoIncrement;
	}

	public boolean isNullable() {
		return nullable;
	}

	public void setNullable(boolean nullable) {
		this.nullable = nullable;
	}

	public String getDefaultValue() {
		return defaultValue;
	}

	public void setDefaultValue(String defaultValue) {
		this.defaultValue = defaultValue;
	}

	@Override
	public String build() {
		return "`" + name + "` " + type.build() + (autoIncrement ? " AUTO_INCREMENT" : "") + (nullable ? "" : " NOT NULL") + (defaultValue != null ? " DEFAULT " + defaultValue : "");
	}

}

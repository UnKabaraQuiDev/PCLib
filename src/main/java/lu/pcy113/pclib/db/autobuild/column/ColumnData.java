package lu.pcy113.pclib.db.autobuild.column;

import lu.pcy113.pclib.PCUtils;
import lu.pcy113.pclib.db.autobuild.SQLBuildable;

public class ColumnData implements SQLBuildable {

	private String name;
	private ColumnType type;
	private boolean autoIncrement = false;
	private boolean nullable = true;
	private String defaultValue = null;
	private String onUpdate = null;

	public ColumnData() {
	}

	public ColumnData(String name, ColumnType type) {
		this.name = name;
		this.type = type;
	}

	public ColumnData(String name, ColumnType type, boolean autoIncrement, boolean nullable, String defaultValue, String onUpdate) {
		this.name = name;
		this.type = type;
		this.autoIncrement = autoIncrement;
		this.nullable = nullable;
		this.defaultValue = defaultValue;
		this.onUpdate = onUpdate;
	}

	public String getName() {
		return name;
	}

	public String getEscapedName() {
		return PCUtils.sqlEscapeIdentifier(name);
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

	public String getOnUpdate() {
		return onUpdate;
	}

	public void setOnUpdate(String onUpdate) {
		this.onUpdate = onUpdate;
	}

	@Override
	public String build() {
		return getEscapedName() + " " + type.build() + (autoIncrement ? " AUTO_INCREMENT" : "") + (nullable ? "" : " NOT NULL") + (defaultValue != null ? " DEFAULT " + defaultValue : "") + (onUpdate != null ? " ON UPDATE " + onUpdate : "");
	}

}

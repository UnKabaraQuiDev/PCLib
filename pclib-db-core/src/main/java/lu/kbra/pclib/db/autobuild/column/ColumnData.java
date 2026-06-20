package lu.kbra.pclib.db.autobuild.column;

import lu.kbra.pclib.PCUtils;
import lu.kbra.pclib.db.autobuild.SQLBuildable;
import lu.kbra.pclib.db.autobuild.column.type.ColumnType;
import lu.kbra.pclib.db.connector.impl.DataBaseConnector;

public class ColumnData implements SQLBuildable {

	protected String name;
	protected ColumnType type;
	protected boolean autoIncrement = false;
	protected boolean nullable = false;
	protected String defaultValue = null;
	protected String onUpdate = null;
	protected boolean primaryKey;
	protected boolean unique;
	protected boolean foreignKey;

	public ColumnData() {
	}

	public ColumnData(final ColumnData cd) {
		this.name = cd.name;
		this.type = cd.type;
		this.type = cd.type;
		this.autoIncrement = cd.autoIncrement;
		this.nullable = cd.nullable;
		this.defaultValue = cd.defaultValue;
		this.onUpdate = cd.onUpdate;
	}

	public ColumnData(final String name, final ColumnType type) {
		this.name = name;
		this.type = type;
	}

	public ColumnData(
			final String name,
			final ColumnType type,
			final boolean autoIncrement,
			final boolean nullable,
			final String defaultValue,
			final String onUpdate) {
		this.name = name;
		this.type = type;
		this.autoIncrement = autoIncrement;
		this.nullable = nullable;
		this.defaultValue = defaultValue;
		this.onUpdate = onUpdate;
	}

	@Override
	public String build(final DataBaseConnector conn) {
		return this.getEscapedName() + " " + this.type.build(conn)
				+ (this.autoIncrement ? "sqlite".equalsIgnoreCase(conn.getProtocol()) ? " AUTOINCREMENT" : " AUTO_INCREMENT" : "")
				+ (this.nullable ? "" : " NOT NULL") + (this.defaultValue != null ? " DEFAULT " + this.defaultValue : "")
				+ (this.onUpdate != null ? " ON UPDATE " + this.onUpdate : "");
	}

	public String getDefaultValue() {
		return this.defaultValue;
	}

	public String getEscapedName() {
		return PCUtils.sqlEscapeIdentifier(this.name);
	}

	public String getName() {
		return this.name;
	}

	public String getOnUpdate() {
		return this.onUpdate;
	}

	public ColumnType getType() {
		return this.type;
	}

	public boolean isAutoIncrement() {
		return this.autoIncrement;
	}

	public boolean isNullable() {
		return this.nullable;
	}

	public void setAutoIncrement(final boolean autoIncrement) {
		this.autoIncrement = autoIncrement;
	}

	public void setDefaultValue(final String defaultValue) {
		this.defaultValue = defaultValue;
	}

	public void setName(final String name) {
		this.name = name;
	}

	public void setNullable(final boolean nullable) {
		this.nullable = nullable;
	}

	public void setOnUpdate(final String onUpdate) {
		this.onUpdate = onUpdate;
	}

	public void setType(final ColumnType type) {
		this.type = type;
	}

	public boolean isPrimaryKey() {
		return primaryKey;
	}

	public void setPrimaryKey(boolean primaryKey) {
		this.primaryKey = primaryKey;
	}

	public boolean isUnique() {
		return unique;
	}

	public void setUnique(boolean unique) {
		this.unique = unique;
	}

	public boolean isForeignKey() {
		return foreignKey;
	}

	public void setForeignKey(boolean foreignKey) {
		this.foreignKey = foreignKey;
	}

	@Override
	public String toString() {
		return "ColumnData@" + System.identityHashCode(this) + " [name=" + name + ", type=" + type + ", autoIncrement=" + autoIncrement
				+ ", nullable=" + nullable + ", defaultValue=" + defaultValue + ", onUpdate=" + onUpdate + ", primaryKey=" + primaryKey
				+ ", unique=" + unique + ", foreignKey=" + foreignKey + "]";
	}

}

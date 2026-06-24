package lu.kbra.pclib.db.autobuild.column;

import java.lang.reflect.Field;
import java.util.Optional;

import lu.kbra.pclib.PCUtils;
import lu.kbra.pclib.db.autobuild.SQLBuildable;
import lu.kbra.pclib.db.autobuild.column.type.ColumnType;
import lu.kbra.pclib.db.connector.impl.DataBaseConnector;

public class ColumnData implements SQLBuildable, Cloneable {

	protected String name;
	protected ColumnType type;
	protected boolean autoIncrement = false;
	protected boolean nullable = false;
	protected String defaultValue = null;
	protected String onUpdate = null;
	protected boolean primaryKey;
	protected boolean unique;
	protected boolean foreignKey;
	protected Optional<Field> field;

	public ColumnData() {
	}

	/**
	 * @deprecated use {@link clone}
	 */
	@Deprecated
	public ColumnData(final ColumnData cd) {
		this.name = cd.name;
		this.type = cd.type;
		this.autoIncrement = cd.autoIncrement;
		this.nullable = cd.nullable;
		this.defaultValue = cd.defaultValue;
		this.onUpdate = cd.onUpdate;
		this.primaryKey = cd.primaryKey;
		this.unique = cd.unique;
		this.foreignKey = cd.foreignKey;
		this.field = cd.field;
	}

	public ColumnData(final Optional<Field> field, final String name, final ColumnType type) {
		this.name = name;
		this.type = type;
		this.field = field;
	}

	public ColumnData(
			final Optional<Field> field,
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
		this.field = field;
	}

	@Override
	public String build(final DataBaseConnector conn) {
		return this.getEscapedName() + " " + this.type.build(conn)
				+ (this.autoIncrement ? "sqlite".equalsIgnoreCase(conn.getProtocol()) ? " AUTOINCREMENT" : " AUTO_INCREMENT" : "")
				+ (this.nullable ? "" : " NOT NULL") + (this.defaultValue != null ? " DEFAULT " + this.defaultValue : "")
				+ (this.onUpdate != null ? " ON UPDATE " + this.onUpdate : "");
	}

	@Override
	public ColumnData clone() {
		try {
			return (ColumnData) super.clone();
		} catch (final CloneNotSupportedException e) {
			throw new InternalError(e);
		}
	}

	public String getDefaultValue() {
		return this.defaultValue;
	}

	@Deprecated
	public String getEscapedName() {
		return PCUtils.sqlEscapeIdentifier(this.name);
	}

	public Optional<Field> getField() {
		return this.field;
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

	public boolean hasDefaultValue() {
		return this.defaultValue != null && !this.defaultValue.isBlank();
	}

	public boolean hasOnUpdate() {
		return this.onUpdate != null && !this.onUpdate.isBlank();
	}

	public boolean isAutoIncrement() {
		return this.autoIncrement;
	}

	public boolean isForeignKey() {
		return this.foreignKey;
	}

	public boolean isGenerated() {
		return false;
	}

	public boolean isNullable() {
		return this.nullable;
	}

	public boolean isPrimaryKey() {
		return this.primaryKey;
	}

	public boolean isUnique() {
		return this.unique;
	}

	public void setAutoIncrement(final boolean autoIncrement) {
		this.autoIncrement = autoIncrement;
	}

	public void setDefaultValue(final String defaultValue) {
		this.defaultValue = defaultValue;
	}

	public void setField(final Optional<Field> field) {
		this.field = field;
	}

	public void setForeignKey(final boolean foreignKey) {
		this.foreignKey = foreignKey;
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

	public void setPrimaryKey(final boolean primaryKey) {
		this.primaryKey = primaryKey;
	}

	public void setType(final ColumnType type) {
		this.type = type;
	}

	public void setUnique(final boolean unique) {
		this.unique = unique;
	}

	@Override
	public String toString() {
		return "ColumnData@" + System.identityHashCode(this) + " [name=" + this.name + ", type=" + this.type + ", autoIncrement="
				+ this.autoIncrement + ", nullable=" + this.nullable + ", defaultValue=" + this.defaultValue + ", onUpdate=" + this.onUpdate
				+ ", primaryKey=" + this.primaryKey + ", unique=" + this.unique + ", foreignKey=" + this.foreignKey + ", field="
				+ this.field + "]";
	}

}

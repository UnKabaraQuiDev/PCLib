package lu.kbra.pclib.db.autobuild.column;

import java.lang.reflect.Field;
import java.util.Optional;

import lu.kbra.pclib.PCUtils;
import lu.kbra.pclib.db.autobuild.SQLBuildable;
import lu.kbra.pclib.db.autobuild.column.type.ColumnType;
import lu.kbra.pclib.db.connector.impl.DataBaseConnector;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
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
		return PCUtils.safeClone(super::clone);
	}

	public String getDefaultValue() {
		return this.defaultValue;
	}

	@Deprecated
	public String getEscapedName() {
		return PCUtils.sqlEscapeIdentifier(this.name);
	}

	public boolean hasDefaultValue() {
		return this.defaultValue != null && !this.defaultValue.isBlank();
	}

	public boolean hasOnUpdate() {
		return this.onUpdate != null && !this.onUpdate.isBlank();
	}

	public boolean isGenerated() {
		return false;
	}

}

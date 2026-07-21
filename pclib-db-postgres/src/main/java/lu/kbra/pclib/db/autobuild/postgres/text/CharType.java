package lu.kbra.pclib.db.autobuild.postgres.text;

import java.lang.reflect.Type;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

import lu.kbra.pclib.db.domain.column.type.ColumnType;

public class CharType implements ColumnType<String> {

	private final int length;

	public CharType(final int length) {
		this.length = length;
	}

	public CharType(final Object object) {
		this.length = ColumnType.asInt(object);
	}

	@Override
	public Object decode(final String value, final Type type) {
		if (type == Character.class) {
			return value.length() > 0 ? value.charAt(0) : null;
		} else if (type == String.class) {
			return value;
		}

		return ColumnType.unsupported(type);
	}

	@Override
	public String encode(final Object value) {
		if (value instanceof Character) {
			return Character.toString((Character) value);
		} else if (value instanceof String) {
			return ((String) value).length() > this.length ? ((String) value).substring(0, this.length) : (String) value;
		}

		return ColumnType.unsupported(value);
	}

	@Override
	public String getObject(final ResultSet rs, final int columnIndex) throws SQLException {
		return rs.getString(columnIndex);
	}

	@Override
	public String getObject(final ResultSet rs, final String columnName) throws SQLException {
		return rs.getString(columnName);
	}

	@Override
	public int getSQLType() {
		return Types.CHAR;
	}

	@Override
	public String getTypeName() {
		return "CHAR";
	}

	@Override
	public boolean isVariable() {
		return true;
	}

	@Override
	public void setObject(final PreparedStatement stmt, final int index, final String value) throws SQLException {
		stmt.setString(index, value);
	}

	@Override
	public Object variableValue() {
		return this.length;
	}
}

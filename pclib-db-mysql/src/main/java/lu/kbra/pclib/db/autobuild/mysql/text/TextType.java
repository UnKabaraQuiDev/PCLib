package lu.kbra.pclib.db.autobuild.mysql.text;

import java.lang.reflect.Type;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import lu.kbra.pclib.db.domain.column.type.ColumnType;
import lu.kbra.pclib.db.domain.column.type.ColumnType.FixedColumnType;

public class TextType implements FixedColumnType<String> {

	@Override
	public Object decode(final String value, final Type type) {
		if (type == String.class) {
			return value;
		} else if (type == CharSequence.class) {
			return value;
		} else if (type == char[].class) {
			return value.toCharArray();
		} else if (type == byte[].class) {
			return value.getBytes();
		} else if (type instanceof Class && ((Class<?>) type).isEnum()) {
			return Enum.valueOf((Class<Enum>) type, value);
		}

		return ColumnType.unsupported(type);
	}

	@Override
	public String encode(final Object value) {
		if (value instanceof String) {
			return (String) value;
		} else if (value instanceof CharSequence) {
			return ((CharSequence) value).toString();
		} else if (value instanceof char[]) {
			return new String((char[]) value);
		} else if (value instanceof byte[]) {
			return new String((byte[]) value);
		} else if (value instanceof Enum<?>) {
			return ((Enum<?>) value).name();
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
	public String getTypeName() {
		return "TEXT";
	}

	@Override
	public void setObject(final PreparedStatement stmt, final int index, final String value) throws SQLException {
		stmt.setString(index, value);
	}

}
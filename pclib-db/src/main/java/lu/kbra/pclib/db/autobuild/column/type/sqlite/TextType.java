package lu.kbra.pclib.db.autobuild.column.type.sqlite;

import java.lang.reflect.Type;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

import lu.kbra.pclib.db.autobuild.column.type.mysql.ColumnType;
import lu.kbra.pclib.db.autobuild.column.type.mysql.ColumnType.FixedColumnType;

public class TextType implements FixedColumnType {

	@Override
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public Object decode(final Object value, final Type type) {
		if (value == null) {
			return null;
		}

		final String text = value.toString();
		if (type == String.class || type == CharSequence.class) {
			return text;
		} else if (type == char[].class) {
			return text.toCharArray();
		} else if (type == byte[].class) {
			return text.getBytes();
		} else if (type == Character.class || type == char.class) {
			return text.isEmpty() ? null : text.charAt(0);
		} else if (type instanceof Class && ((Class<?>) type).isEnum()) {
			return Enum.valueOf((Class<? extends Enum>) ((Class<?>) type).asSubclass(Enum.class), text);
		}

		return ColumnType.unsupported(type);
	}

	@Override
	public Object encode(final Object value) {
		if (value instanceof String) {
			return value;
		} else if (value instanceof CharSequence) {
			return value.toString();
		} else if (value instanceof char[]) {
			return new String((char[]) value);
		} else if (value instanceof byte[]) {
			return new String((byte[]) value);
		} else if (value instanceof Character) {
			return Character.toString((Character) value);
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
	public int getSQLType() {
		return Types.VARCHAR;
	}

	@Override
	public String getTypeName() {
		return "TEXT";
	}

	@Override
	public void setObject(final PreparedStatement stmt, final int index, final Object value) throws SQLException {
		stmt.setString(index, value == null ? null : value.toString());
	}

}

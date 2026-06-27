package lu.kbra.pclib.db.autobuild.postgres;

import java.lang.reflect.Type;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.UUID;

import org.json.JSONArray;
import org.json.JSONObject;

import lu.kbra.pclib.db.domain.column.type.ColumnType;
import lu.kbra.pclib.db.domain.column.type.ColumnType.FixedColumnType;

public final class TextTypes {

	public static class CharType extends VarcharType {
		public CharType(final int length) {
			super(length);
		}

		public CharType(final Object object) {
			super(object);
		}

		@Override
		public String getTypeName() {
			return "CHAR";
		}
	}

	public static class JsonType implements FixedColumnType {
		@Override
		public Object decode(final Object value, final Type type) {
			if (value == null) {
				return null;
			}
			final String text = value.toString();
			if (type == JSONObject.class) {
				return new JSONObject(text);
			} else if (type == JSONArray.class) {
				return new JSONArray(text);
			} else if (type == String.class) {
				return text;
			}
			return ColumnType.unsupported(type);
		}

		@Override
		public Object encode(final Object value) {
			if (value instanceof JSONObject || value instanceof JSONArray) {
				return value.toString();
			} else if (value instanceof String) {
				final String text = (String) value;
				try {
					return new JSONObject(text).toString();
				} catch (final RuntimeException objectException) {
					return new JSONArray(text).toString();
				}
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
			return Types.OTHER;
		}

		@Override
		public String getTypeName() {
			return "JSONB";
		}

		@Override
		public void setObject(final PreparedStatement stmt, final int index, final Object value) throws SQLException {
			stmt.setObject(index, value, Types.OTHER);
		}
	}

	public static class TextType implements FixedColumnType {
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

	public static class UUIDType implements FixedColumnType {

		@Override
		public Object decode(final Object value, final Type type) {
			if (value == null) {
				return null;
			}
			if (type == UUID.class && value instanceof UUID) {
				return value;
			}
			if (type == UUID.class) {
				return UUID.fromString(value.toString());
			}
			if (type == String.class) {
				return value.toString();
			}
			return ColumnType.unsupported(type);
		}

		@Override
		public Object encode(final Object value) {
			if (value == null) {
				return null;
			}
			if (value instanceof UUID) {
				return value;
			}
			if (value instanceof String) {
				return UUID.fromString((String) value);
			}
			return ColumnType.unsupported(value);
		}

		@Override
		public UUID getObject(final ResultSet rs, final int columnIndex) throws SQLException {
			return rs.getObject(columnIndex, UUID.class);
		}

		@Override
		public UUID getObject(final ResultSet rs, final String columnName) throws SQLException {
			return rs.getObject(columnName, UUID.class);
		}

		@Override
		public int getSQLType() {
			return Types.OTHER;
		}

		@Override
		public String getTypeName() {
			return "UUID";
		}

		@Override
		public void setObject(final PreparedStatement stmt, final int index, final Object value) throws SQLException {
			stmt.setObject(index, value, Types.OTHER);
		}

	}

	public static class VarcharType extends TextType implements ColumnType {

		private final int length;

		public VarcharType(final int length) {
			this.length = length;
		}

		public VarcharType(final Object object) {
			this.length = ColumnType.asInt(object);
		}

		@Override
		public String getTypeName() {
			return "VARCHAR";
		}

		@Override
		public boolean isVariable() {
			return true;
		}

		@Override
		public Object variableValue() {
			return this.length;
		}

	}

	private TextTypes() {
	}
}

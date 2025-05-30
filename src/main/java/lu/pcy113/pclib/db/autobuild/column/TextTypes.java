package lu.pcy113.pclib.db.autobuild.column;

import java.sql.Types;

import org.json.JSONObject;

import lu.pcy113.pclib.db.autobuild.column.ColumnType.FixedColumnType;
import lu.pcy113.pclib.impl.DependsOn;

public final class TextTypes {

	public static class CharType implements ColumnType {

		private final int length;

		public CharType(int length) {
			this.length = length;
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
		public Object variableValue() {
			return length;
		}

		@Override
		public int getSQLType() {
			return Types.CHAR;
		}

	}

	public static class VarcharType implements ColumnType {

		private final int length;

		public VarcharType(int length) {
			this.length = length;
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
			return length;
		}

		@Override
		public int getSQLType() {
			return Types.VARCHAR;
		}

	}

	public static class TextType implements FixedColumnType {

		@Override
		public String getTypeName() {
			return "TEXT";
		}

	}

	@DependsOn("org.json.JSONObject")
	public static class JsonType implements FixedColumnType {

		@Override
		public String getTypeName() {
			return "JSON";
		}

		@Override
		public Object encode(Object value) {
			if (value == null)
				return null;

			if (value.getClass() != JSONObject.class) {
				return ((JSONObject) value).toString();
			} else if (value.getClass() == String.class) {
				return new JSONObject((String) value).toString();
			}
			throw new IllegalArgumentException("Unsupported type: " + value.getClass());
		}

	}

}

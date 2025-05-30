package lu.pcy113.pclib.db.autobuild.column;

import lu.pcy113.pclib.db.autobuild.column.ColumnType.FixedColumnType;

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

	}

	public static class TextType implements FixedColumnType {

		@Override
		public String getTypeName() {
			return "TEXT";
		}

	}

	public static class JsonType implements FixedColumnType {

		@Override
		public String getTypeName() {
			return "JSON";
		}

	}

}

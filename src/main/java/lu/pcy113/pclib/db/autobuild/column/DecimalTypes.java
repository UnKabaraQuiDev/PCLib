package lu.pcy113.pclib.db.autobuild.column;

import lu.pcy113.pclib.db.autobuild.column.ColumnType.FixedColumnType;

public final class DecimalTypes {

	public static class DecimalType implements ColumnType {

		private final int precision;
		private final int scale;

		public DecimalType(int precision, int scale) {
			this.precision = precision;
			this.scale = scale;
		}

		@Override
		public String getTypeName() {
			return "DECIMAL";
		}

		@Override
		public boolean isVariable() {
			return true;
		}

		@Override
		public Object variableValue() {
			return precision + ", " + scale;
		}

	}

	public static class DoubleType implements FixedColumnType {

		@Override
		public String getTypeName() {
			return "DOUBLE";
		}

	}

	public static class FloatType implements FixedColumnType {

		@Override
		public String getTypeName() {
			return "FLOAT";
		}

	}

}

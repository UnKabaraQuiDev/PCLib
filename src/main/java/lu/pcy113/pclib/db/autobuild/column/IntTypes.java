package lu.pcy113.pclib.db.autobuild.column;

import java.sql.Types;

import lu.pcy113.pclib.db.autobuild.column.ColumnType.FixedColumnType;

public final class IntTypes {

	public static class BitType implements FixedColumnType {

		@Override
		public String getTypeName() {
			return "BIT";
		}

		@Override
		public int getSQLType() {
			return Types.BIT;
		}

	}

	public static class SmallIntType implements FixedColumnType {

		@Override
		public String getTypeName() {
			return "SMALLINT";
		}

		@Override
		public int getSQLType() {
			return Types.SMALLINT;
		}

	}

	public static class IntType implements FixedColumnType {

		@Override
		public String getTypeName() {
			return "INT";
		}

		@Override
		public int getSQLType() {
			return Types.INTEGER;
		}

	}

	public static class BigIntType implements FixedColumnType {

		@Override
		public String getTypeName() {
			return "BIGINT";
		}

		@Override
		public int getSQLType() {
			return Types.BIGINT;
		}

	}

}

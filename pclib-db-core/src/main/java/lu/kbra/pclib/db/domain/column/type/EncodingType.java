package lu.kbra.pclib.db.domain.column.type;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public interface EncodingType<Tjdbc> {

	public interface FixedEncodingType<Tjdbc> extends EncodingType<Tjdbc> {

		@Override
		default boolean isVariable() {
			return false;
		}

		@Override
		default Object variableValue() {
			return null;
		}

		public interface FixedUnsignedEncodingType<Tjdbc> extends FixedEncodingType<Tjdbc> {

			boolean isUnsigned();

			String getRawTypeName();

			@Override
			default String getTypeName() {
				return getRawTypeName() + (isUnsigned() ? " UNSIGNED" : "");
			}

		}

	}

	public interface VariableEncodingType<Tjdbc> extends EncodingType<Tjdbc> {

		@Override
		default boolean isVariable() {
			return true;
		}

		public interface VariableUnsignedEncodingType<Tjdbc> extends VariableEncodingType<Tjdbc> {

			boolean isUnsigned();

			String getRawTypeName();

			@Override
			default String getTypeName() {
				return getRawTypeName() + "(" + variableValue() + ")" + (isUnsigned() ? " UNSIGNED" : "");
			}

		}

	}

	Tjdbc getObject(final ResultSet rs, final int columnIndex) throws SQLException;

	Tjdbc getObject(final ResultSet rs, final String columnName) throws SQLException;

	default int getSQLType() {
		return -1;
	}

	void setObject(final PreparedStatement stmt, final int index, final Tjdbc value) throws SQLException;

	String getTypeName();

	boolean isVariable();

	Object variableValue();

	default String build() {
		return this.getTypeName() + (this.isVariable() ? "(" + this.variableValue() + ")" : "");
	}

}

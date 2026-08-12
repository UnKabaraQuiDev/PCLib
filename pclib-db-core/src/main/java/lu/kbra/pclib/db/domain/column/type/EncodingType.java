package lu.kbra.pclib.db.domain.column.type;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public interface EncodingType<Tjdbc> {

	int UNDEFINED_SQL_TYPE = -1;

	public interface FixedEncodingType<Tjdbc> extends EncodingType<Tjdbc> {

		@Override
		default boolean isVariable() {
			return false;
		}

		@Override
		default Object getVariableValue() {
			return null;
		}

		public interface FixedUnsignedEncodingType<Tjdbc> extends FixedEncodingType<Tjdbc> {

			boolean isUnsigned();

			String getRawTypeName();

			@Override
			default String getTypeName() {
				return this.getRawTypeName() + (this.isUnsigned() ? " UNSIGNED" : "");
			}

		}

	}

	public interface VariableEncodingType<Tjdbc> extends EncodingType<Tjdbc> {

		@Override
		default boolean isVariable() {
			return true;
		}

		@Override
		default String build() {
			return this.getTypeName() + (this.isVariable() ? "(" + this.getVariableValue() + ")" : "");
		}

		public interface VariableUnsignedEncodingType<Tjdbc> extends VariableEncodingType<Tjdbc> {

			boolean isUnsigned();

			String getRawTypeName();

			@Override
			default String getTypeName() {
				return this.getRawTypeName() + "(" + this.getVariableValue() + ")" + (this.isUnsigned() ? " UNSIGNED" : "");
			}

			@Override
			default String build() {
				return this.getTypeName();
			}

			@Override
			default String cast() {
				return this.getRawTypeName() + (this.isUnsigned() ? " UNSIGNED" : "");
			}

		}

	}

	Tjdbc getObject(final ResultSet rs, final int columnIndex) throws SQLException;

	Tjdbc getObject(final ResultSet rs, final String columnName) throws SQLException;

	int getSQLType();

	void setObject(final PreparedStatement stmt, final int index, final Tjdbc value) throws SQLException;

	String getTypeName();

	boolean isVariable();

	Object getVariableValue();

	default String build() {
		return this.getTypeName();
	}

	default String cast() {
		return this.getTypeName();
	}

}

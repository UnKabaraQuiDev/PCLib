package lu.kbra.pclib.db.domain.column.type;

import java.lang.reflect.Type;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.OptionalInt;

public interface ColumnType<Tjava, Tjdbc> {

	public interface IdentityColumnType<T> extends ColumnType<T, T> {

		@Override
		default T encode(final T value) {
			return value;
		}

		@Override
		default T decode(final T value, final Type type) {
			return value;
		}

	}

	static int asInt(final Object object) {
		if (object instanceof String) {
			return Integer.parseInt((String) object);
		} else if (object.getClass() == int.class) {
			return (int) object;
		} else if (object.getClass() == Integer.class) {
			return (Integer) object;
		} else if (object instanceof OptionalInt) {
			return ((OptionalInt) object).getAsInt();
		} else {
			throw new IllegalArgumentException("Unsupported type: " + object.getClass() + " for: " + object);
		}
	}

	static <T> T unsupported(final Class<?> clazz) throws IllegalArgumentException {
		throw new IllegalArgumentException("Unsupported type: " + clazz.getName());
	}

	static <T> T unsupported(final Object value) throws IllegalArgumentException {
		throw new IllegalArgumentException("Unsupported type: " + value.getClass().getName());
	}

	static <T> T unsupported(final Type type) throws IllegalArgumentException {
		throw new IllegalArgumentException("Unsupported type: " + type);
	}

	Tjava decode(final Tjdbc value, final Type type);

	EncodingType<Tjdbc> getEncodingType();

	Tjdbc encode(final Tjava value);

	default Tjava load(final ResultSet rs, final int columnIndex, final Type type) throws SQLException {
		final Tjdbc obj = this.getEncodingType().getObject(rs, columnIndex);
		return obj == null ? null : this.decode(obj, type);
	}

	default Tjava load(final ResultSet rs, final String columnName, final Type type) throws SQLException {
		final Tjdbc obj = this.getEncodingType().getObject(rs, columnName);
		return obj == null ? null : this.decode(obj, type);
	}

	default void store(final PreparedStatement stmt, final int index, final Tjava value) throws SQLException {
		if (value == null) {
			if (this.getEncodingType().getSQLType() == EncodingType.UNDEFINED_SQL_TYPE) {
				stmt.setObject(index, null);
			} else {
				stmt.setNull(index, this.getEncodingType().getSQLType());
			}
		} else {
			this.getEncodingType().setObject(stmt, index, this.encode(value));
		}
	}

	default int storeLength(final PreparedStatement stmt, final int index, final Tjava value) {
		return 1;
	}

	default int loadLength(final ResultSet rs, final int columnIndex, final Type type) {
		return 1;
	}

}

package lu.kbra.pclib.db.domain.column.type;

import java.lang.reflect.Type;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.OptionalInt;

import lombok.NonNull;

public interface ColumnType<Tjava, Tjdbc> {

	public interface IdentityColumnType<T> extends ColumnType<T, T> {

		@Override
		default T encode(T value) {
			return value;
		}

		@Override
		default T decode(T value, Type type) {
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

	@NonNull
	Tjava decode(final @NonNull Tjdbc value, final Type type);

	EncodingType<Tjdbc> getEncodingType();

	@NonNull
	Tjdbc encode(final @NonNull Tjava value);

	default Tjava load(final ResultSet rs, final int columnIndex, final Type type) throws SQLException {
		return this.decode(this.getEncodingType().getObject(rs, columnIndex), type);
	}

	default Tjava load(final ResultSet rs, final String columnName, final Type type) throws SQLException {
		return this.decode(this.getEncodingType().getObject(rs, columnName), type);
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

}

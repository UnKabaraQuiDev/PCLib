package lu.kbra.pclib.db.query;

import java.lang.reflect.Type;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import lu.kbra.pclib.db.domain.column.type.ColumnType;
import lu.kbra.pclib.db.domain.column.type.EncodingType;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
class DelegatingArrayColumnType<Tjava, Tjdbc> implements ColumnType<Tjava[], Tjdbc> {

	private final ColumnType<Tjava, Tjdbc> delegate;

	@Override
	public Tjava[] decode(final Tjdbc value, final Type type) {
		throw new UnsupportedOperationException("ArrayColumnType can only be used to encode Java -> JDBC.");
	}

	@Override
	public EncodingType<Tjdbc> getEncodingType() {
		return this.delegate.getEncodingType();
	}

	@Override
	public Tjdbc encode(final Tjava[] value) {
		throw new UnsupportedOperationException("Use " + this.getClass().getSimpleName() + ".store(...).");
	}

	@Override
	public Tjava[] load(final ResultSet rs, final int columnIndex, final Type type) throws SQLException {
		throw new UnsupportedOperationException("ArrayColumnType can only be used to encode Java -> JDBC.");
	}

	@Override
	public Tjava[] load(final ResultSet rs, final String columnName, final Type type) throws SQLException {
		throw new UnsupportedOperationException("ArrayColumnType can only be used to encode Java -> JDBC.");
	}

	@Override
	public void store(final PreparedStatement stmt, final int index, final Tjava[] value) throws SQLException {
		int i = 0;
		for (final Tjava v : value) {
			this.delegate.store(stmt, index + i, v);
			i += this.delegate.storeLength(stmt, index + i, v);
		}
	}
}

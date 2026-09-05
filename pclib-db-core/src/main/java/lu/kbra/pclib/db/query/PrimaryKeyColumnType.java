package lu.kbra.pclib.db.query;

import java.lang.reflect.Type;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;

import lu.kbra.pclib.db.domain.column.ColumnData;
import lu.kbra.pclib.db.domain.column.type.ColumnType;
import lu.kbra.pclib.db.domain.column.type.EncodingType;
import lu.kbra.pclib.db.domain.table.SQLQueryableStructure;
import lu.kbra.pclib.db.impl.DatabaseEntry;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
class PrimaryKeyColumnType<T extends DatabaseEntry> implements ColumnType<T, Void> {

	private final ColumnData[] primaryKeys;

	public PrimaryKeyColumnType(final SQLQueryableStructure structure) {
		this.primaryKeys = Arrays.stream(structure.getColumns()).filter(ColumnData::isPrimaryKey).toArray(ColumnData[]::new);
	}

	@Override
	public T decode(final Void value, final Type type) {
		throw new UnsupportedOperationException("PrimaryKeyColumnType can only be used to encode Java -> JDBC.");
	}

	@Override
	public EncodingType<Void> getEncodingType() {
		throw new UnsupportedOperationException("PrimaryKeyColumnType doesn't have an EncodingType.");
	}

	@Override
	public Void encode(final T value) {
		throw new UnsupportedOperationException("Use " + this.getClass().getSimpleName() + ".store(...).");
	}

	@Override
	public T load(final ResultSet rs, final int columnIndex, final Type type) throws SQLException {
		throw new UnsupportedOperationException("PrimaryKeyColumnType can only be used to encode Java -> JDBC.");
	}

	@Override
	public T load(final ResultSet rs, final String columnName, final Type type) throws SQLException {
		throw new UnsupportedOperationException("PrimaryKeyColumnType can only be used to encode Java -> JDBC.");
	}

	@Override
	public void store(final PreparedStatement stmt, final int index, final T value) throws SQLException {
		int i = 0;
		for (final ColumnData pk : this.primaryKeys) {
			final Object v = pk.getStorageBinding().get(value);
			pk.getType().store(stmt, index + i, v);
			i += pk.getType().storeLength(stmt, index + i, v);
		}
	}

}

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

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
@ToString
public class PrimaryKeyColumnType<T extends DatabaseEntry> implements ColumnType<T, Void> {

	private final ColumnData[] keyColumns;

	/**
	 * PKS in {@code from}
	 */
	PrimaryKeyColumnType(final SQLQueryableStructure target) {
		this.keyColumns = Arrays.stream(target.getColumns()).filter(ColumnData::isPrimaryKey).toArray(ColumnData[]::new);
	}

	@Override
	public T decode(final Void value, final Type type) {
		throw new UnsupportedOperationException(this.getClass().getSimpleName() + " can only be used to encode Java -> JDBC.");
	}

	@Override
	public EncodingType<Void> getEncodingType() {
		return null;
	}

	@Override
	public Void encode(final T value) {
		throw new UnsupportedOperationException("Use " + this.getClass().getSimpleName() + ".store(...).");
	}

	@Override
	public T load(final ResultSet rs, final int columnIndex, final Type type) throws SQLException {
		throw new UnsupportedOperationException(this.getClass().getSimpleName() + " can only be used to encode Java -> JDBC.");
	}

	@Override
	public T load(final ResultSet rs, final String columnName, final Type type) throws SQLException {
		throw new UnsupportedOperationException(this.getClass().getSimpleName() + " can only be used to encode Java -> JDBC.");
	}

	@Override
	public void store(final PreparedStatement stmt, final int index, final T value) throws SQLException {
		int i = 0;
		for (final ColumnData pk : this.keyColumns) {
			final Object v = pk.getStorageBinding().get(value);
			pk.getType().store(stmt, index + i, v);
			i += pk.getType().storeLength(index + i, v);
		}
	}

	@Override
	public int storeLength(final int index, final T value) {
		int i = 0;
		for (final ColumnData pk : this.keyColumns) {
			final Object v = pk.getStorageBinding().get(value);
			i += pk.getType().storeLength(index + i, v);
		}
		return i;
	}

	@Override
	public Class<T> getJavaType() {
		return null;
	}

	@Override
	public Class<Void> getJdbcType() {
		return Void.class;
	}

	@Override
	public boolean isMultiColumn() {
		return true;
	}

}

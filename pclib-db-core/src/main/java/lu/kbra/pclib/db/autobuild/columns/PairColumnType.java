package lu.kbra.pclib.db.autobuild.columns;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import lu.kbra.pclib.datastructure.tuple.Pair;
import lu.kbra.pclib.datastructure.tuple.Tuples;
import lu.kbra.pclib.db.domain.column.type.ColumnType;
import lu.kbra.pclib.db.domain.column.type.EncodingType;
import lu.kbra.pclib.db.utils.impl.EncodingTypeProvider;

import lombok.Getter;

@Getter
class PairColumnType<K, V> implements ColumnType<Pair<?, ?>, Void> {

	private final EncodingType<?>[] encodingTypes;

	public PairColumnType(final Type type, final EncodingTypeProvider etp) {
		if (!(type instanceof ParameterizedType)) {
			throw new IllegalArgumentException("Expected a parameterized Pair<K, V>: " + type);
		}

		final Type[] args = ((ParameterizedType) type).getActualTypeArguments();

		this.encodingTypes = new EncodingType[2];
		this.encodingTypes[0] = etp.getTypeFor((Class<?>) args[0]);
		this.encodingTypes[1] = etp.getTypeFor((Class<?>) args[1]);
	}

	@Override
	public Pair<?, ?> load(final ResultSet rs, final int columnIndex, final Type type) throws SQLException {
		final K key = (K) this.encodingTypes[0].getObject(rs, columnIndex + 0);
		final V value = (V) this.encodingTypes[1].getObject(rs, columnIndex + 1);
		return Tuples.readOnly(key, value);
	}

	@Override
	public Pair<?, ?> load(final ResultSet rs, final String columnName, final Type type) throws SQLException {
		throw new UnsupportedOperationException("Multi-column ColumnType cannot be loaded by column name.");
	}

	@Override
	public void store(final PreparedStatement stmt, final int index, final Pair<?, ?> value) throws SQLException {
		((EncodingType<Object>) this.encodingTypes[0]).setObject(stmt, index, value == null ? null : value.getKey());
		((EncodingType<Object>) this.encodingTypes[1]).setObject(stmt, index + 1, value == null ? null : value.getValue());
	}

	@Override
	public Class<Pair<?, ?>> getJavaType() {
		return (Class) Pair.class;
	}

	@Override
	public Class<Void> getJdbcType() {
		return Void.class;
	}

	@Override
	public int storeLength(final int index, final Pair<?, ?> value) {
		return 2;
	}

	@Override
	public int loadLength(final int columnIndex, final Type type) {
		return 2;
	}

	@Override
	public Pair<?, ?> decode(final Void value, final Type type) {
		throw new UnsupportedOperationException("Use " + this.getClass().getSimpleName() + ".load(...).");
	}

	@Override
	public EncodingType<Void> getEncodingType() {
		return null;
	}

	@Override
	public Void encode(final Pair<?, ?> value) {
		throw new UnsupportedOperationException("Use " + this.getClass().getSimpleName() + ".store(...).");
	}

	@Override
	public boolean isMultiColumn() {
		return true;
	}

}

package lu.kbra.pclib.db.migration;

import java.lang.reflect.Type;

import lu.kbra.pclib.db.domain.column.type.ColumnType;

import lombok.Getter;

@Getter
public class MigrationColumnType implements ColumnType<Void, Void> {

	private final MigrationEncodingType encodingType;

	public MigrationColumnType(final String typeName) {
		this.encodingType = new MigrationEncodingType(typeName);
	}

	@Override
	public Void decode(final Void value, final Type type) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Void encode(final Void value) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Class<Void> getJavaType() {
		return Void.class;
	}

	@Override
	public Class<Void> getJdbcType() {
		return Void.class;
	}

	@Override
	public String toString() {
		return this.getClass().getSimpleName() + "(type=" + (this.encodingType == null ? "<null>" : this.encodingType.build()) + ")";
	}

}

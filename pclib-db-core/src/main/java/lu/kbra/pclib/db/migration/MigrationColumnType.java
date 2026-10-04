package lu.kbra.pclib.db.migration;

import java.lang.reflect.Type;

import lu.kbra.pclib.db.domain.column.type.ColumnType;

import lombok.Getter;

@Getter
public class MigrationColumnType implements ColumnType<Void, Void> {

	private final MigrationEncodingType encodingType;

	public MigrationColumnType(String typeName) {
		this.encodingType = new MigrationEncodingType(typeName);
	}

	@Override
	public Void decode(Void value, Type type) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Void encode(Void value) {
		throw new UnsupportedOperationException();
	}

}

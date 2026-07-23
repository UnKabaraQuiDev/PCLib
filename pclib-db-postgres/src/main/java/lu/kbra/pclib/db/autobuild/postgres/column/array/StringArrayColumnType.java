package lu.kbra.pclib.db.autobuild.postgres.column.array;

import lu.kbra.pclib.db.autobuild.postgres.encoding.array.ObjectArrayEncodingType;
import lu.kbra.pclib.db.domain.column.type.ColumnType.IdentityColumnType;
import lu.kbra.pclib.db.domain.column.type.EncodingType;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class StringArrayColumnType implements IdentityColumnType<String[]> {

	private final EncodingType<String[]> encodingType;

	public StringArrayColumnType(int dimensions) {
		encodingType = new ObjectArrayEncodingType<>("TEXT", String[].class, dimensions);
	}

}

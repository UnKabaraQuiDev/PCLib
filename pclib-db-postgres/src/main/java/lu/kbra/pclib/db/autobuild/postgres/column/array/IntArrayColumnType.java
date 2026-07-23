package lu.kbra.pclib.db.autobuild.postgres.column.array;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lu.kbra.pclib.db.autobuild.postgres.encoding.array.ObjectArrayEncodingType;
import lu.kbra.pclib.db.domain.column.type.ColumnType.IdentityColumnType;
import lu.kbra.pclib.db.domain.column.type.EncodingType;

@Getter
@RequiredArgsConstructor
public class IntArrayColumnType implements IdentityColumnType<int[]> {

	private final EncodingType<int[]> encodingType;

	public IntArrayColumnType(int dimensions) {
		encodingType = new ObjectArrayEncodingType<>("INTEGER", int[].class, dimensions);
	}

}

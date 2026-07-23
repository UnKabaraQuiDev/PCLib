package lu.kbra.pclib.db.autobuild.postgres.column.array;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lu.kbra.pclib.db.autobuild.postgres.encoding.array.IntArrayEncodingType;
import lu.kbra.pclib.db.domain.column.type.ColumnType.IdentityColumnType;
import lu.kbra.pclib.db.domain.column.type.EncodingType;
import lu.kbra.pclib.db.utils.registry.EncodingTypeRegistry;

@Getter
@RequiredArgsConstructor
public class IntArrayColumnType implements IdentityColumnType<int[]> {

	private final EncodingType<int[]> encodingType = EncodingTypeRegistry.getFixedEncodingType(IntArrayEncodingType.class,
			IntArrayEncodingType::new);

}

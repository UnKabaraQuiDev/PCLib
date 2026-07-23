package lu.kbra.pclib.db.autobuild.postgres.column.array;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lu.kbra.pclib.db.autobuild.postgres.encoding.array.StringArrayEncodingType;
import lu.kbra.pclib.db.domain.column.type.ColumnType.IdentityColumnType;
import lu.kbra.pclib.db.domain.column.type.EncodingType;
import lu.kbra.pclib.db.utils.registry.EncodingTypeRegistry;

@Getter
@RequiredArgsConstructor
public class StringArrayColumnType implements IdentityColumnType<String[]> {

	private final EncodingType<String[]> encodingType = EncodingTypeRegistry.getFixedEncodingType(StringArrayEncodingType.class,
			StringArrayEncodingType::new);

}

package lu.kbra.pclib.db.autobuild.sqlite.column.integer;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lu.kbra.pclib.db.autobuild.sqlite.encoding.integer.IntEncodingType;
import lu.kbra.pclib.db.domain.column.type.ColumnType.IdentityColumnType;
import lu.kbra.pclib.db.domain.column.type.EncodingType;
import lu.kbra.pclib.db.utils.registry.EncodingTypeRegistry;

@Getter
@RequiredArgsConstructor
public class LongColumnType implements IdentityColumnType<Long> {

	private final EncodingType<Long> encodingType = EncodingTypeRegistry.getFixedEncodingType(IntEncodingType.class, IntEncodingType::new);

}

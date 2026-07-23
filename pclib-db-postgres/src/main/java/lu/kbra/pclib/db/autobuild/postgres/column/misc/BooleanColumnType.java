package lu.kbra.pclib.db.autobuild.postgres.column.misc;

import lu.kbra.pclib.db.autobuild.postgres.encoding.bool.BooleanEncodingType;
import lu.kbra.pclib.db.domain.column.type.ColumnType.IdentityColumnType;
import lu.kbra.pclib.db.domain.column.type.EncodingType;
import lu.kbra.pclib.db.utils.registry.EncodingTypeRegistry;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class BooleanColumnType implements IdentityColumnType<Boolean> {

	private final EncodingType<Boolean> encodingType = EncodingTypeRegistry.getFixedEncodingType(BooleanEncodingType.class,
			BooleanEncodingType::new);

}

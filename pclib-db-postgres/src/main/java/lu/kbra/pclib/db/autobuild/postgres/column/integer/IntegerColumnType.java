package lu.kbra.pclib.db.autobuild.postgres.column.integer;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lu.kbra.pclib.db.autobuild.postgres.encoding.integer.IntEncodingType;
import lu.kbra.pclib.db.domain.column.type.ColumnType.IdentityColumnType;
import lu.kbra.pclib.db.domain.column.type.EncodingType;
import lu.kbra.pclib.db.utils.registry.EncodingTypeRegistry;

@Getter
@RequiredArgsConstructor
public class IntegerColumnType implements IdentityColumnType<Integer> {

	private final EncodingType<Integer> encodingType;

	public IntegerColumnType(final boolean unsigned) {
		this.encodingType = EncodingTypeRegistry.getFixedEncodingType(IntEncodingType.class, unsigned, IntEncodingType::new);
	}

	public IntegerColumnType() {
		this(false);
	}

}

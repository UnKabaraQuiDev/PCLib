package lu.kbra.pclib.db.autobuild.mysql.column.integer;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lu.kbra.pclib.db.autobuild.mysql.encoding.integer.IntEncodingType;
import lu.kbra.pclib.db.domain.column.type.ColumnType.IdentityColumnType;
import lu.kbra.pclib.db.domain.column.type.EncodingType;
import lu.kbra.pclib.db.utils.registry.EncodingTypeRegistry;

@Getter
@RequiredArgsConstructor
public class IntegerType implements IdentityColumnType<Integer> {

	private final EncodingType<Integer> encodingType;

	public IntegerType(final boolean unsigned) {
		this.encodingType = EncodingTypeRegistry.getFixedEncodingType(IntEncodingType.class, unsigned, IntEncodingType::new);
	}

	public IntegerType() {
		this(false);
	}

}

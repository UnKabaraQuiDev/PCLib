package lu.kbra.pclib.db.autobuild.mysql.integer;

import lu.kbra.pclib.db.autobuild.mysql.encoding.integer.SmallIntEncodingType;
import lu.kbra.pclib.db.domain.column.type.ColumnType.IdentityColumnType;
import lu.kbra.pclib.db.domain.column.type.EncodingType;
import lu.kbra.pclib.db.utils.registry.MySQLColumnTypeRegistry;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class ShortType implements IdentityColumnType<Short> {

	private final EncodingType<Short> encodingType;

	public ShortType(final boolean unsigned) {
		this.encodingType = MySQLColumnTypeRegistry.getFixedEncodingType(SmallIntEncodingType.class, unsigned, SmallIntEncodingType::new);
	}

	public ShortType() {
		this(false);
	}

}

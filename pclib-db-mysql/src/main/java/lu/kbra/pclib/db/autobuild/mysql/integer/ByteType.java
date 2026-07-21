package lu.kbra.pclib.db.autobuild.mysql.integer;

import lu.kbra.pclib.db.autobuild.mysql.encoding.integer.TinyIntEncodingType;
import lu.kbra.pclib.db.domain.column.type.ColumnType.IdentityColumnType;
import lu.kbra.pclib.db.domain.column.type.EncodingType;
import lu.kbra.pclib.db.utils.registry.MySQLColumnTypeRegistry;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class ByteType implements IdentityColumnType<Byte> {

	private final EncodingType<Byte> encodingType;

	public ByteType(final boolean unsigned) {
		this.encodingType = MySQLColumnTypeRegistry.getFixedEncodingType(TinyIntEncodingType.class, unsigned, TinyIntEncodingType::new);
	}

	public ByteType() {
		this(false);
	}

}

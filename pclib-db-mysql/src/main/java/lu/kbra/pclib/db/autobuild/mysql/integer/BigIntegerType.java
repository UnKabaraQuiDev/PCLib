package lu.kbra.pclib.db.autobuild.mysql.integer;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lu.kbra.pclib.db.autobuild.mysql.encoding.integer.BigIntEncodingType;
import lu.kbra.pclib.db.domain.column.type.ColumnType.IdentityColumnType;
import lu.kbra.pclib.db.domain.column.type.EncodingType;
import lu.kbra.pclib.db.utils.registry.MySQLColumnTypeRegistry;

@Getter
@RequiredArgsConstructor
public class BigIntegerType implements IdentityColumnType<Long> {

	private final EncodingType<Long> encodingType;

	public BigIntegerType(final boolean unsigned) {
		this.encodingType = MySQLColumnTypeRegistry.getFixedEncodingType(BigIntEncodingType.class, unsigned, BigIntEncodingType::new);
	}

}
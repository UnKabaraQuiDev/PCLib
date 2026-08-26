package lu.kbra.pclib.db.autobuild.mysql.column.integer;

import java.lang.reflect.Type;
import java.math.BigInteger;

import lu.kbra.pclib.db.autobuild.mysql.encoding.integer.BigIntEncodingType;
import lu.kbra.pclib.db.domain.column.type.ColumnType;
import lu.kbra.pclib.db.domain.column.type.EncodingType;
import lu.kbra.pclib.db.utils.registry.EncodingTypeRegistry;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class BigIntegerColumnType implements ColumnType<BigInteger, Long> {

	private final EncodingType<Long> encodingType;

	public BigIntegerColumnType(final boolean unsigned) {
		this.encodingType = EncodingTypeRegistry.getFixedEncodingType(BigIntEncodingType.class, unsigned, BigIntEncodingType::new);
	}

	public BigIntegerColumnType() {
		this(false);
	}

	@Override
	public BigInteger decode(final Long value, final Type type) {
		return BigInteger.valueOf(value);
	}

	@Override
	public Long encode(final BigInteger value) {
		return value.longValueExact();
	}

}

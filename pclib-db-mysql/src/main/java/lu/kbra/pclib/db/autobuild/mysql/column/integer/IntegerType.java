package lu.kbra.pclib.db.autobuild.mysql.column.integer;

import java.lang.reflect.Type;
import java.math.BigInteger;

import lu.kbra.pclib.db.autobuild.mysql.encoding.integer.BigIntEncodingType;
import lu.kbra.pclib.db.domain.column.type.ColumnType;
import lu.kbra.pclib.db.domain.column.type.EncodingType;
import lu.kbra.pclib.db.utils.registry.EncodingTypeRegistry;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class IntegerType implements ColumnType<BigInteger, Long> {

	private final EncodingType<Long> encodingType;

	public IntegerType(final boolean unsigned) {
		this.encodingType = EncodingTypeRegistry.getFixedEncodingType(BigIntEncodingType.class, unsigned, BigIntEncodingType::new);
	}

	public IntegerType() {
		this(false);
	}

	@Override
	public @NonNull BigInteger decode(@NonNull Long value, Type type) {
		return BigInteger.valueOf(value);
	}

	@Override
	public @NonNull Long encode(@NonNull BigInteger value) {
		return value.longValueExact();
	}

}

package lu.kbra.pclib.db.autobuild.sqlite.column.integer;

import java.lang.reflect.Type;
import java.math.BigInteger;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lu.kbra.pclib.db.autobuild.sqlite.encoding.integer.IntEncodingType;
import lu.kbra.pclib.db.domain.column.type.ColumnType;
import lu.kbra.pclib.db.domain.column.type.EncodingType;
import lu.kbra.pclib.db.utils.registry.EncodingTypeRegistry;

@Getter
@RequiredArgsConstructor
public class BigIntegerType implements ColumnType<BigInteger, Long> {

	private final EncodingType<Long> encodingType;

	public BigIntegerType() {
		this.encodingType = EncodingTypeRegistry.getFixedEncodingType(IntEncodingType.class, IntEncodingType::new);
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

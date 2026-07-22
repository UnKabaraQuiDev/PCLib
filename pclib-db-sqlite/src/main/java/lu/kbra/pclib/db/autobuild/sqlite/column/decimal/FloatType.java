package lu.kbra.pclib.db.autobuild.sqlite.column.decimal;

import java.lang.reflect.Type;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lu.kbra.pclib.db.autobuild.sqlite.encoding.decimal.RealEncodingType;
import lu.kbra.pclib.db.domain.column.type.ColumnType;
import lu.kbra.pclib.db.domain.column.type.EncodingType;
import lu.kbra.pclib.db.utils.registry.EncodingTypeRegistry;

@Getter
@RequiredArgsConstructor
public class FloatType implements ColumnType<Float, Double> {

	private final EncodingType<Double> encodingType;

	public FloatType() {
		this.encodingType = EncodingTypeRegistry.getFixedEncodingType(RealEncodingType.class, RealEncodingType::new);
	}

	@Override
	public @NonNull Float decode(@NonNull Double value, Type type) {
		return value.floatValue();
	}

	@Override
	public @NonNull Double encode(@NonNull Float value) {
		return value.doubleValue();
	}

}

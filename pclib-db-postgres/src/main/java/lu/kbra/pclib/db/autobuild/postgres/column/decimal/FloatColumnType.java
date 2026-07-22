package lu.kbra.pclib.db.autobuild.postgres.column.decimal;

import lu.kbra.pclib.db.autobuild.postgres.encoding.decimal.RealEncodingType;
import lu.kbra.pclib.db.domain.column.type.ColumnType.IdentityColumnType;
import lu.kbra.pclib.db.domain.column.type.EncodingType;
import lu.kbra.pclib.db.utils.registry.EncodingTypeRegistry;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class FloatColumnType implements IdentityColumnType<Float> {

	private final EncodingType<Float> encodingType;

	public FloatColumnType() {
		this.encodingType = EncodingTypeRegistry.getFixedEncodingType(RealEncodingType.class, RealEncodingType::new);
	}

}

package lu.kbra.pclib.db.autobuild.sqlite.column.decimal;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lu.kbra.pclib.db.autobuild.sqlite.encoding.decimal.RealEncodingType;
import lu.kbra.pclib.db.domain.column.type.ColumnType.IdentityColumnType;
import lu.kbra.pclib.db.domain.column.type.EncodingType;
import lu.kbra.pclib.db.utils.registry.EncodingTypeRegistry;

@Getter
@RequiredArgsConstructor
public class DoubleType implements IdentityColumnType<Double> {

	private final EncodingType<Double> encodingType;

	public DoubleType() {
		this.encodingType = EncodingTypeRegistry.getFixedEncodingType(RealEncodingType.class, RealEncodingType::new);
	}

}

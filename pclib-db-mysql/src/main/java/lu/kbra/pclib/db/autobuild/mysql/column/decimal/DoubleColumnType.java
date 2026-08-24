package lu.kbra.pclib.db.autobuild.mysql.column.decimal;

import lu.kbra.pclib.db.autobuild.mysql.encoding.decimal.DoubleEncodingType;
import lu.kbra.pclib.db.domain.column.type.ColumnType.IdentityColumnType;
import lu.kbra.pclib.db.domain.column.type.EncodingType;
import lu.kbra.pclib.db.utils.registry.EncodingTypeRegistry;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class DoubleColumnType implements IdentityColumnType<Double> {

	private final EncodingType<Double> encodingType;

	public DoubleColumnType() {
		this.encodingType = EncodingTypeRegistry.getFixedEncodingType(DoubleEncodingType.class, DoubleEncodingType::new);
	}

}

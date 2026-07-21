package lu.kbra.pclib.db.autobuild.mysql.decimal;

import lu.kbra.pclib.db.autobuild.mysql.encoding.decimal.DoubleEncodingType;
import lu.kbra.pclib.db.domain.column.type.ColumnType.IdentityColumnType;
import lu.kbra.pclib.db.domain.column.type.EncodingType;
import lu.kbra.pclib.db.utils.registry.MySQLColumnTypeRegistry;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class DoubleType implements IdentityColumnType<Double> {

	private final EncodingType<Double> encodingType;

	public DoubleType() {
		this.encodingType = MySQLColumnTypeRegistry.getFixedEncodingType(DoubleEncodingType.class, DoubleEncodingType::new);
	}

}

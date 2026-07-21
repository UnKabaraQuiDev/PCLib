package lu.kbra.pclib.db.autobuild.mysql.decimal;

import lu.kbra.pclib.db.autobuild.mysql.encoding.decimal.FloatEncodingType;
import lu.kbra.pclib.db.domain.column.type.ColumnType.IdentityColumnType;
import lu.kbra.pclib.db.domain.column.type.EncodingType;
import lu.kbra.pclib.db.utils.registry.MySQLColumnTypeRegistry;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class FloatType implements IdentityColumnType<Float> {

	private final EncodingType<Float> encodingType;

	public FloatType() {
		this.encodingType = MySQLColumnTypeRegistry.getFixedEncodingType(FloatEncodingType.class, FloatEncodingType::new);
	}

}

package lu.kbra.pclib.db.autobuild.mysql.text;

import lombok.Getter;
import lu.kbra.pclib.db.autobuild.mysql.encoding.text.CharEncodingType;
import lu.kbra.pclib.db.autobuild.mysql.encoding.text.VarcharEncodingType;
import lu.kbra.pclib.db.domain.column.type.ColumnType;
import lu.kbra.pclib.db.domain.column.type.ColumnType.IdentityColumnType;
import lu.kbra.pclib.db.domain.column.type.EncodingType;
import lu.kbra.pclib.db.utils.registry.MySQLColumnTypeRegistry;

@Getter
public class StringType implements IdentityColumnType<String> {

	private final EncodingType<String> encodingType;

	public StringType(final int length, final boolean max) {
		if (max) {
			this.encodingType = MySQLColumnTypeRegistry.getFixedEncodingType(VarcharEncodingType.class, length, VarcharEncodingType::new);
		} else {
			this.encodingType = MySQLColumnTypeRegistry.getFixedEncodingType(CharEncodingType.class, length, CharEncodingType::new);
		}
	}

	public StringType(final Object object, final boolean max) {
		this(ColumnType.asInt(object), max);
	}

}
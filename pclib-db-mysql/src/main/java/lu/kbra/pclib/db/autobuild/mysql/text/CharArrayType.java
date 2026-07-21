package lu.kbra.pclib.db.autobuild.mysql.text;

import java.lang.reflect.Type;

import lombok.Getter;
import lombok.NonNull;
import lu.kbra.pclib.db.autobuild.mysql.encoding.text.CharEncodingType;
import lu.kbra.pclib.db.autobuild.mysql.encoding.text.VarcharEncodingType;
import lu.kbra.pclib.db.domain.column.type.ColumnType;
import lu.kbra.pclib.db.domain.column.type.EncodingType;
import lu.kbra.pclib.db.utils.registry.MySQLColumnTypeRegistry;

@Getter
public class CharArrayType implements ColumnType<char[], String> {

	private final EncodingType<String> encodingType;

	public CharArrayType(final int length, final boolean max) {
		if (max) {
			this.encodingType = MySQLColumnTypeRegistry.getFixedEncodingType(VarcharEncodingType.class, length, VarcharEncodingType::new);
		} else {
			this.encodingType = MySQLColumnTypeRegistry.getFixedEncodingType(CharEncodingType.class, length, CharEncodingType::new);
		}
	}

	public CharArrayType(final Object object, final boolean max) {
		this(ColumnType.asInt(object), max);
	}

	@Override
	public @NonNull char[] decode(@NonNull String value, Type type) {
		return value.toCharArray();
	}

	@Override
	public @NonNull String encode(@NonNull char[] value) {
		return new String(value);
	}

}
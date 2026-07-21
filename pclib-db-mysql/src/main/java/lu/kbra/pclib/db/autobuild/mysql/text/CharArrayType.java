package lu.kbra.pclib.db.autobuild.mysql.text;

import java.lang.reflect.Type;

import lu.kbra.pclib.db.autobuild.mysql.encoding.text.CharEncodingType;
import lu.kbra.pclib.db.autobuild.mysql.encoding.text.TextEncodingType;
import lu.kbra.pclib.db.autobuild.mysql.encoding.text.VarcharEncodingType;
import lu.kbra.pclib.db.domain.column.type.ColumnType;
import lu.kbra.pclib.db.domain.column.type.EncodingType;
import lu.kbra.pclib.db.domain.column.type.SizeClass;
import lu.kbra.pclib.db.utils.registry.MySQLColumnTypeRegistry;

import lombok.Getter;
import lombok.NonNull;

@Getter
public class CharArrayType implements ColumnType<char[], String> {

	private final EncodingType<String> encodingType;

	public CharArrayType(final int length, final boolean max) {
		if (max) {
			this.encodingType = new VarcharEncodingType(length);
		} else {
			this.encodingType = new CharEncodingType(length);
		}
	}

	public CharArrayType(final Object object, final boolean max) {
		this(ColumnType.asInt(object), max);
	}

	public CharArrayType(SizeClass sizeClass) {
		this.encodingType = MySQLColumnTypeRegistry.getFixedEncodingType(TextEncodingType.class, sizeClass, TextEncodingType::new);
	}

	public CharArrayType() {
		this(SizeClass.NORMAL);
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

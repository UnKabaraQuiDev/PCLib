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
public class CharSequenceType implements ColumnType<CharSequence, String> {

	private final EncodingType<String> encodingType;

	public CharSequenceType(final int length, final boolean max) {
		if (max) {
			this.encodingType = MySQLColumnTypeRegistry.getFixedEncodingType(VarcharEncodingType.class, length, VarcharEncodingType::new);
		} else {
			this.encodingType = MySQLColumnTypeRegistry.getFixedEncodingType(CharEncodingType.class, length, CharEncodingType::new);
		}
	}

	public CharSequenceType(final Object object, final boolean max) {
		this(ColumnType.asInt(object), max);
	}

	@Override
	public @NonNull CharSequence decode(@NonNull String value, Type type) {
		return value;
	}

	@Override
	public @NonNull String encode(@NonNull CharSequence value) {
		return value.toString();
	}

}
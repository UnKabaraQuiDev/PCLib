package lu.kbra.pclib.db.autobuild.mysql.column.text;

import lu.kbra.pclib.db.autobuild.mysql.encoding.text.CharEncodingType;
import lu.kbra.pclib.db.autobuild.mysql.encoding.text.TextEncodingType;
import lu.kbra.pclib.db.autobuild.mysql.encoding.text.VarcharEncodingType;
import lu.kbra.pclib.db.autobuild.mysql.meta.SizeClass;
import lu.kbra.pclib.db.domain.column.type.ColumnType;
import lu.kbra.pclib.db.domain.column.type.ColumnType.IdentityColumnType;
import lu.kbra.pclib.db.domain.column.type.EncodingType;
import lu.kbra.pclib.db.utils.registry.EncodingTypeRegistry;
import lombok.Getter;

@Getter
public class StringType implements IdentityColumnType<String> {

	private final EncodingType<String> encodingType;

	public StringType(final int length, final boolean max) {
		if (max) {
			this.encodingType = new VarcharEncodingType(length);
		} else {
			this.encodingType = new CharEncodingType(length);
		}
	}

	public StringType(final Object object, final boolean max) {
		this(ColumnType.asInt(object), max);
	}

	public StringType(SizeClass sizeClass) {
		this.encodingType = EncodingTypeRegistry.getFixedEncodingType(TextEncodingType.class, sizeClass, TextEncodingType::new);
	}

	public StringType() {
		this(SizeClass.NORMAL);
	}

}

package lu.kbra.pclib.db.autobuild.mysql.text;

import lombok.Getter;
import lu.kbra.pclib.db.domain.column.type.ColumnType.IdentityColumnType;
import lu.kbra.pclib.db.autobuild.mysql.encoding.text.TextEncodingType;
import lu.kbra.pclib.db.domain.column.type.SizeClass;
import lu.kbra.pclib.db.utils.registry.MySQLColumnTypeRegistry;

@Getter
public class StringColumnType implements IdentityColumnType<String> {

	private final TextEncodingType encodingType;

	public StringColumnType(SizeClass sizeClass) {
		encodingType = MySQLColumnTypeRegistry.getFixedEncodingType(TextEncodingType.class, sizeClass, TextEncodingType::new);
	}

}
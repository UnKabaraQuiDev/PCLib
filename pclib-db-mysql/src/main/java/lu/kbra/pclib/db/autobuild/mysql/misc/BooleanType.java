package lu.kbra.pclib.db.autobuild.mysql.misc;

import lu.kbra.pclib.db.autobuild.mysql.encoding.bool.BooleanEncodingType;
import lu.kbra.pclib.db.domain.column.type.ColumnType.IdentityColumnType;
import lu.kbra.pclib.db.domain.column.type.EncodingType;
import lu.kbra.pclib.db.utils.registry.MySQLColumnTypeRegistry;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class BooleanType implements IdentityColumnType<Boolean> {

	private final EncodingType<Boolean> encodingType = MySQLColumnTypeRegistry.getFixedEncodingType(BooleanEncodingType.class,
			BooleanEncodingType::new);

}

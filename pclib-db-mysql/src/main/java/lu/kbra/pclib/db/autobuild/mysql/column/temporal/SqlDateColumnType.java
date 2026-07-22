package lu.kbra.pclib.db.autobuild.mysql.column.temporal;

import java.sql.Date;

import lu.kbra.pclib.db.autobuild.mysql.encoding.temporal.DateEncodingType;
import lu.kbra.pclib.db.domain.column.type.ColumnType.IdentityColumnType;
import lu.kbra.pclib.db.domain.column.type.EncodingType;
import lu.kbra.pclib.db.utils.registry.EncodingTypeRegistry;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class SqlDateColumnType implements IdentityColumnType<Date> {

	private final EncodingType<Date> encodingType = EncodingTypeRegistry.getFixedEncodingType(DateEncodingType.class,
			DateEncodingType::new);

}

package lu.kbra.pclib.db.autobuild.mysql.temporal;

import java.sql.Timestamp;

import lu.kbra.pclib.db.autobuild.mysql.encoding.temporal.TimestampEncodingType;
import lu.kbra.pclib.db.domain.column.type.ColumnType.IdentityColumnType;
import lu.kbra.pclib.db.domain.column.type.EncodingType;
import lu.kbra.pclib.db.utils.registry.MySQLColumnTypeRegistry;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class TimestampColumnType implements IdentityColumnType<Timestamp> {

	private final EncodingType<Timestamp> encodingType = MySQLColumnTypeRegistry.getFixedEncodingType(TimestampEncodingType.class,
			TimestampEncodingType::new);

}

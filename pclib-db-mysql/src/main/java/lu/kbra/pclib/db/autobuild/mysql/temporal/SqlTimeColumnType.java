package lu.kbra.pclib.db.autobuild.mysql.temporal;

import java.sql.Time;

import lu.kbra.pclib.db.autobuild.mysql.encoding.temporal.TimeEncodingType;
import lu.kbra.pclib.db.domain.column.type.ColumnType.IdentityColumnType;
import lu.kbra.pclib.db.domain.column.type.EncodingType;
import lu.kbra.pclib.db.utils.registry.MySQLColumnTypeRegistry;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class SqlTimeColumnType implements IdentityColumnType<Time> {

	private final EncodingType<Time> encodingType = MySQLColumnTypeRegistry.getFixedEncodingType(TimeEncodingType.class,
			TimeEncodingType::new);

}

package lu.kbra.pclib.db.autobuild.mysql.column.temporal;

import java.lang.reflect.Type;
import java.sql.Date;
import java.time.LocalDate;

import lu.kbra.pclib.db.autobuild.mysql.encoding.temporal.DateEncodingType;
import lu.kbra.pclib.db.domain.column.type.ColumnType;
import lu.kbra.pclib.db.domain.column.type.EncodingType;
import lu.kbra.pclib.db.utils.registry.EncodingTypeRegistry;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class LocalDateColumnType implements ColumnType<LocalDate, Date> {

	private final EncodingType<Date> encodingType = EncodingTypeRegistry.getFixedEncodingType(DateEncodingType.class,
			DateEncodingType::new);

	@Override
	public @NonNull LocalDate decode(@NonNull Date value, Type type) {
		return value.toLocalDate();
	}

	@Override
	public @NonNull Date encode(@NonNull LocalDate value) {
		return Date.valueOf(value);
	}

}

package lu.kbra.pclib.db.base;

import java.lang.reflect.Type;
import java.util.List;

import lu.kbra.pclib.db.dbms.MySQLDbmsProvider;
import lu.kbra.pclib.db.dbms.SQLiteDbmsProvider;
import lu.kbra.pclib.db.domain.column.type.ColumnType;
import lu.kbra.pclib.db.domain.column.type.EncodingType;
import lu.kbra.pclib.db.type.factory.DatabaseTypeFactory;
import lu.kbra.pclib.db.utils.registry.ColumnTypeFactory;
import lu.kbra.pclib.db.utils.registry.ColumnTypeRegistry;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

public class MoreTypeFactory implements DatabaseTypeFactory {

	@RequiredArgsConstructor
	public class AgeType implements ColumnType<Age, Long> {

		@Getter
		private final EncodingType<Long> encodingType;

		@Override
		public @NonNull Age decode(@NonNull Long value, Type type) {
			return new Age(value.byteValue());
		}

		@Override
		public @NonNull Long encode(@NonNull Age value) {
			return (long) value.value();
		}

	}

	public record Age(byte value) {

	}

	@Override
	public void registerColumnTypes(final List<ColumnTypeFactory<?>> typeMap) {
		ColumnTypeRegistry.registerType(AgeType.class,
				(clazz, typeHints, etp) -> clazz == Age.class ? ColumnTypeRegistry.TYPE_CATCH_ALL_SCORE : ColumnTypeRegistry.EXCLUDE,
				(optType, typeHints, etp) -> new AgeType(etp.getTypeFor(Long.class)), // TODO this should be Byte.class
				typeMap);
	}

	@Override
	public boolean matches(final String protocol) {
		return MySQLDbmsProvider.DBMS_QUALIFIER_NAME.equalsIgnoreCase(protocol) || SQLiteDbmsProvider.DBMS_QUALIFIER_NAME.equals(protocol);
	}

}

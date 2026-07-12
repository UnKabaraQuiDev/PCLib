package lu.kbra.pclib.db.base;

import java.lang.reflect.Type;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import lu.kbra.pclib.db.dbms.MySQLDbmsProvider;
import lu.kbra.pclib.db.dbms.SQLiteDbmsProvider;
import lu.kbra.pclib.db.domain.column.type.ColumnType;
import lu.kbra.pclib.db.domain.column.type.ColumnType.FixedColumnType;
import lu.kbra.pclib.db.type.factory.DatabaseTypeFactory;
import lu.kbra.pclib.db.utils.registry.ColumnTypeFactory;
import lu.kbra.pclib.db.utils.registry.ColumnTypeRegistry;

public class MoreTypeFactory implements DatabaseTypeFactory {

	public class AgeType implements FixedColumnType {

		@Override
		public String getTypeName() {
			return "SMALLINT";
		}

		@Override
		public Object decode(final Object value, final Type type) {
			if (type == Age.class) {
				return new Age((short) value);
			}

			return ColumnType.unsupported(type);
		}

		@Override
		public Object encode(final Object value) {
			if (value instanceof Age) {
				return ((Age) value).value();
			}

			return ColumnType.unsupported(value);
		}

		@Override
		public Object getObject(final ResultSet rs, final int columnIndex) throws SQLException {
			return rs.getShort(columnIndex);
		}

		@Override
		public Object getObject(final ResultSet rs, final String columnName) throws SQLException {
			return rs.getShort(columnName);
		}

		@Override
		public void setObject(final PreparedStatement stmt, final int index, final Object value) throws SQLException {
			stmt.setShort(index, (short) value);
		}

	}

	public record Age(short value) {

	}

	@Override
	public void registerTypes(final List<ColumnTypeFactory> typeMap) {
		ColumnTypeRegistry.registerType(AgeType.class,
				(clazz, typeHints) -> clazz == Age.class ? ColumnTypeRegistry.TYPE_CATCH_ALL_SCORE : ColumnTypeRegistry.EXCLUDE,
				(optType, typeHints) -> new AgeType(),
				typeMap);
	}

	@Override
	public boolean matches(final String protocol) {
		return MySQLDbmsProvider.DBMS_QUALIFIER_NAME.equalsIgnoreCase(protocol) || SQLiteDbmsProvider.DBMS_QUALIFIER_NAME.equals(protocol);
	}

}

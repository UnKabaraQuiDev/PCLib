package test;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.Test;

import lu.kbra.pclib.db.base.Database;
import lu.kbra.pclib.db.exception.DataAccessException;
import lu.kbra.pclib.db.impl.DatabaseEntry;
import lu.kbra.pclib.db.utils.DatabaseBuilder;
import lu.kbra.pclib.db.utils.DatabaseBuilder.TablePlan.ColumnPlan;
import lu.kbra.pclib.db.utils.impl.DatabaseEntryUtils;
import lu.kbra.pclib.db.utils.impl.StorageBinding;

import lombok.Data;
import lombok.NoArgsConstructor;

public interface DBBuilderTest extends GenericDBTest {

	@Data
	@NoArgsConstructor
	public static class MapContainer {

		private final Map<String, Object> map = new HashMap<>();

	}

	@Data
	public static class MapStorageBinding implements StorageBinding {

		private final String memberName;
		private final Type genericType;

		public MapStorageBinding(final ColumnPlan cp) {
			this.memberName = cp.getName();
			this.genericType = cp.getType().getJavaType();
		}

		@Override
		public void set(final DatabaseEntry entry, final Object val) throws DataAccessException {
			((MapContainer) entry).map.put(this.memberName, val);
		}

		@Override
		public String getMemberName() {
			return this.memberName;
		}

		@Override
		public Type getGenericType() {
			return this.genericType;
		}

		@Override
		public Object get(final DatabaseEntry entry) throws DataAccessException {
			return ((MapContainer) entry).map.get(this.memberName);
		}

	}

	@Test
	default void testBuild() {

		final Database db = this.getDatabase();
		final DatabaseBuilder builder = new DatabaseBuilder(db);
		final DatabaseEntryUtils dbEntryUtils = db.getDatabaseEntryUtils();

		//@formatter:off
		builder.newTable()
				.name("mainTable")
				.newColumn()
					.name("id")
					.type(dbEntryUtils.getColumnTypeProvider().getTypeFor(Long.class))
					.storagebinding(MapStorageBinding::new)
					.build()
				.build();
		//@formatter:on
	}

}

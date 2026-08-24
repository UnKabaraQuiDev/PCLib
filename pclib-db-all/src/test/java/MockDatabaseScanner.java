import java.lang.reflect.Field;
import java.util.Map;

import lu.kbra.pclib.db.base.Database;
import lu.kbra.pclib.db.domain.column.ColumnData;
import lu.kbra.pclib.db.domain.table.SQLQueryableStructure;
import lu.kbra.pclib.db.domain.table.TableStructure;
import lu.kbra.pclib.db.domain.view.ViewStructure;
import lu.kbra.pclib.db.impl.DatabaseEntry;
import lu.kbra.pclib.db.impl.SQLQueryable;
import lu.kbra.pclib.db.table.AbstractDBTable;
import lu.kbra.pclib.db.utils.DatabaseScanner;
import lu.kbra.pclib.db.view.AbstractDBView;

public class MockDatabaseScanner extends DatabaseScanner {

	public MockDatabaseScanner(final Database database, final Map<String, Object> hints) {
		super(database, hints);
	}

	public MockDatabaseScanner(final Database database) {
		super(database);
	}

	@Override
	public void scanSelfStructure() {
		super.scanSelfStructure();
	}

	@Override
	public void scanLinks() {
		super.scanLinks();
	}

	@Override
	public TableStructure scanSelfTableStructure(
			final AbstractDBTable<?> instance,
			final Map<String, Object> customHints,
			final Class<? extends AbstractDBTable<?>> tableClazz,
			final Map<String, Object> customEntryHints) {
		return super.scanSelfTableStructure(instance, customHints, tableClazz, customEntryHints);
	}

	@Override
	public void registerSimpleNames(
			final Class<? extends SQLQueryable<?>> tableClazz,
			final Map<String, Object> queryableHints,
			final SQLQueryableStructure tableStructure) {
		super.registerSimpleNames(tableClazz, queryableHints, tableStructure);
	}

	@Override
	public ViewStructure scanSelfViewStructure(
			final AbstractDBView<? extends DatabaseEntry> instance,
			final Map<String, Object> customHints,
			final Class<? extends AbstractDBView<? extends DatabaseEntry>> viewClazz,
			final Map<String, Object> customEntryHints) {
		return super.scanSelfViewStructure(instance, customHints, viewClazz, customEntryHints);
	}

	@Override
	public ColumnData[] computeColumnsFor(
			final SQLQueryable<?> table,
			final SQLQueryableStructure tableStructure,
			final Class<? extends DatabaseEntry> entryClazz) {
		return super.computeColumnsFor(table, tableStructure, entryClazz);
	}

	@Override
	public Field findField(final Class<?> type, final String name) throws NoSuchFieldException {
		return super.findField(type, name);
	}

	@Override
	public Field[] getAllFields(final Class<?> type) {
		return super.getAllFields(type);
	}

}

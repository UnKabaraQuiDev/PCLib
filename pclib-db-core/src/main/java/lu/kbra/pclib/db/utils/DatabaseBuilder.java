package lu.kbra.pclib.db.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import lu.kbra.pclib.PCUtils;
import lu.kbra.pclib.db.base.Database;
import lu.kbra.pclib.db.connector.impl.DatabaseConnector;
import lu.kbra.pclib.db.domain.column.ColumnData;
import lu.kbra.pclib.db.domain.column.type.ColumnType;
import lu.kbra.pclib.db.domain.table.StructureName;
import lu.kbra.pclib.db.domain.table.meta.DefaultQueryableHints;
import lu.kbra.pclib.db.impl.DatabaseEntry;
import lu.kbra.pclib.db.impl.HintsOwner;
import lu.kbra.pclib.db.table.AbstractDBTable;
import lu.kbra.pclib.db.utils.impl.DatabaseEntryUtils;
import lu.kbra.pclib.db.utils.impl.StorageBinding;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@ToString
@EqualsAndHashCode
public class DatabaseBuilder {

	private final Database database;
	private final DatabaseEntryUtils dbEntryUtils;
	private final DatabaseScanner scanner;

	private final List<TablePlan> tablePlans = new ArrayList<>();

	// @formatter:off
	/*newTable()
			.name("")
			.explicitName("")
			.newColumn()
					.name("")
					.explicitName("")
					.type()
					.build()
			.build()*/
	// @formatter:on

	protected DatabaseBuilder(final DatabaseScanner databaseScanner, final Database db) {
		this.scanner = databaseScanner;
		this.database = db;
		this.dbEntryUtils = databaseScanner.getDatabaseEntryUtils();
	}

	public DatabaseBuilder(final Database db) {
		this(new DatabaseScanner(db), db);
	}

	public DatabaseBuilder(final DatabaseConnector connector, final String name) {
		this(new Database(connector, name));
	}

	public DatabaseBuilder(final DatabaseConnector connector, final String name, final DatabaseEntryUtils dbEntryUtils) {
		this(new Database(connector, name, dbEntryUtils));
	}

	public DatabaseBuilder(
			final DatabaseConnector connector,
			final String name,
			final Map<String, Object> dbCustomHints,
			final DatabaseEntryUtils dbEntryUtils) {
		this(new Database(connector, name, dbCustomHints, dbEntryUtils));
	}

	public DatabaseBuilder addTable(final TablePlan tablePlan) {
		this.tablePlans.add(tablePlan);
		return this;
	}

	public TablePlan newTable() {
		return new TablePlan();
	}

	@Data
	@NoArgsConstructor
	@AllArgsConstructor
	public class TablePlan
			implements
				SQLNameOwner<TablePlan>,
				SQLSchemaOwner<TablePlan>,
				ParentedBuilder<DatabaseBuilder>,
				SQLHintsOwner<TablePlan> {

		protected String name;
		protected String explicitName;
		protected Class<? extends DatabaseEntry> entryClass;
		protected List<ColumnPlan> columns = new ArrayList<>();
		protected Map<String, Object> hints = new HashMap<>();

		public ColumnPlan newColumn() {
			return new ColumnPlan();
		}

		public TablePlan addColumn(final ColumnPlan columnPlan) {
			this.columns.add(columnPlan);
			return this;
		}

		public TablePlan tableId(final String tableId) {
			this.hints.put(DefaultQueryableHints.TABLE_ID, tableId);
			return this;
		}

		public TablePlan targetClass(final Class<? extends AbstractDBTable<?>> tableClass) {
			this.hints.put(DefaultQueryableHints.TARGET_CLASS, tableClass);
			return this;
		}

		public TablePlan entryClass(final Class<? extends DatabaseEntry> entryClass) {
			this.hints.put(DefaultQueryableHints.ENTRY_CLASS, entryClass);
			return this;
		}

		@Override
		public String getFinalName() {
			return this.hasExplicitName() ? this.getExplicitName()
					: DatabaseBuilder.this.dbEntryUtils.getStructureVisitor().getQueryableName(this.getName());
		}

		@Override
		public String getFinalSchema() {
			return this.hasSchema() ? this.getSchema() : DatabaseBuilder.this.dbEntryUtils.getStructureVisitor().getDefaultSchema();
		}

		public String[] getNameParts() {
			return this.hasSchema() && DatabaseBuilder.this.dbEntryUtils.getStructureVisitor().getDefaultSchema() != null
					? new String[] { this.getFinalSchema(), this.getFinalName() }
					: new String[] { this.getFinalName() };
		}

		@Override
		public DatabaseBuilder build() {
			DatabaseBuilder.this.addTable(this);
			return DatabaseBuilder.this;
		}

		@Data
		@NoArgsConstructor
		@AllArgsConstructor
		public class ColumnPlan
				implements
					SQLBuilder<ColumnData>,
					SQLNameOwner<ColumnPlan>,
					ParentedBuilder<TablePlan>,
					SQLHintsOwner<ColumnPlan> {

			protected String memberName;
			protected ColumnType<?, ?> type;
			protected StorageBinding storageBinding;
			protected Map<String, Object> hints = new HashMap<>();

			@Override
			public void setName(String name) {
				this.memberName = name;
			}

			@Override
			public String getName() {
				return this.memberName;
			}

			public ColumnPlan type(final ColumnType<?, ?> type) {
				this.type = type;
				return this;
			}

			public ColumnPlan storagebinding(final StorageBinding storageBinding) {
				this.storageBinding = storageBinding;
				return this;
			}

			@Override
			public String getFinalName() {
				return this.hasExplicitName() ? this.getExplicitName()
						: DatabaseBuilder.this.dbEntryUtils.getStructureVisitor().memberToColumnName(this.getName());
			}

			@Override
			public TablePlan build() {
				TablePlan.this.addColumn(this);
				return TablePlan.this;
			}

			@Override
			public ColumnData getNewInstance() {
				final String finalName = this.getFinalName();
				final String[] stringParts = PCUtils.combineArrays(TablePlan.this.getNameParts(), new String[] { finalName });
				return new ColumnData(finalName,
						DatabaseBuilder.this.dbEntryUtils.getStructureVisitor().qualifiedName(finalName),
						new StructureName(Arrays.stream(stringParts).collect(Collectors.joining(".")),
								stringParts,
								DatabaseBuilder.this.dbEntryUtils.getStructureVisitor().qualifiedName(stringParts)),
						this.hints,
						this.type,
						this.storageBinding,
						this.hints);
			}

		}

	}

	interface SQLNameOwner<T extends SQLNameOwner<T>> extends SQLHintsOwner<T> {

		void setName(String name);

		default T name(final String name) {
			this.setName(name);
			return (T) this;
		}

		default T explicitName(final String explicitName) {
			this.put(DefaultQueryableHints.NAME_OVERRIDE, explicitName);
			return (T) this;
		}

		default String getExplicitName() {
			return this.getStringHint(DefaultQueryableHints.NAME_OVERRIDE);
		}

		String getName();

		String getFinalName();

		default boolean hasExplicitName() {
			return this.getExplicitName() != null;
		}

	}

	interface SQLSchemaOwner<T extends SQLSchemaOwner<T>> extends SQLHintsOwner<T> {

		default T schema(final String schema) {
			this.put(DefaultQueryableHints.SCHEMA, schema);
			return (T) this;
		}

		default String getSchema() {
			return this.getStringHint(DefaultQueryableHints.SCHEMA);
		}

		default boolean hasSchema() {
			return this.getSchema() != null;
		}

		String getFinalSchema();

	}

	interface ParentedBuilder<T> {

		T build();

	}

	interface SQLHintsOwner<T extends SQLHintsOwner<T>> extends HintsOwner {

		void setHints(Map<String, Object> hints);

		default T put(final String key, final Object value) {
			this.getHints().put(key, value);
			return (T) this;
		}

		default T putAll(final Map<String, Object> hints) {
			this.getHints().putAll(hints);
			return (T) this;
		}

		default T setAll(final Map<String, Object> hints) {
			this.getHints().clear();
			this.getHints().putAll(hints);
			return (T) this;
		}

	}

	public interface SQLBuilder<T> {

		T getNewInstance();

	}

}

package lu.kbra.pclib.db.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

import lu.kbra.pclib.PCUtils;
import lu.kbra.pclib.db.base.Database;
import lu.kbra.pclib.db.connector.impl.DatabaseConnector;
import lu.kbra.pclib.db.domain.column.ColumnData;
import lu.kbra.pclib.db.domain.column.meta.DefaultColumnHints;
import lu.kbra.pclib.db.domain.column.type.ColumnType;
import lu.kbra.pclib.db.domain.table.StructureName;
import lu.kbra.pclib.db.domain.table.TableStructure;
import lu.kbra.pclib.db.domain.table.meta.DefaultQueryableHints;
import lu.kbra.pclib.db.impl.DatabaseEntry;
import lu.kbra.pclib.db.impl.HintsOwner;
import lu.kbra.pclib.db.impl.SQLQueryable;
import lu.kbra.pclib.db.table.AbstractDBTable;
import lu.kbra.pclib.db.table.DatabaseTable;
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

	private static final Function<Database, AbstractDBTable<?>> DEFAULT_BEAN_PROVIDER = DatabaseTable::new;

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

	public Database build() {
		for (final TablePlan tp : this.tablePlans) {
			final AbstractDBTable<?> table = tp.getNewInstance();
			this.database.registerTable(table);
		}

		return this.database;
	}

	@Data
	@AllArgsConstructor
	public class TablePlan
			implements
				SQLNameOwner<TablePlan>,
				SQLSchemaOwner<TablePlan>,
				ParentedBuilder<DatabaseBuilder>,
				SQLHintsOwner<TablePlan>,
				SQLBuilder<AbstractDBTable<?>> {

		protected String name;
		protected List<ColumnPlan> columns = new ArrayList<>();
		protected Map<String, Object> hints = new HashMap<>();

		protected Function<Database, AbstractDBTable<?>> beanProvider = DatabaseBuilder.DEFAULT_BEAN_PROVIDER;

		public TablePlan() {
			this.targetClass((Class<? extends AbstractDBTable<?>>) (Class) DatabaseTable.class);
		}

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

		public TablePlan definedName(final String definedName) {
			this.hints.put(DefaultQueryableHints.DEFINED_NAME, definedName);
			return this;
		}

		public TablePlan beanProvider(final Function<Database, AbstractDBTable<?>> beanProvider) {
			this.beanProvider = beanProvider;
			return this;
		}

		public TablePlan targetClass(final Class<? extends AbstractDBTable<?>> tableClass) {
			this.hints.put(DefaultQueryableHints.TARGET_CLASS, tableClass.asSubclass(SQLQueryable.class));
			return this;
		}

		public TablePlan entryClass(final Class<? extends DatabaseEntry> entryClass) {
			this.hints.put(DefaultQueryableHints.ENTRY_CLASS, entryClass);
			return this;
		}

		@Override
		public String getFinalName() {
			return this.hasExplicitName() ? this.getExplicitName()
					: DatabaseBuilder.this.dbEntryUtils.getStructureVisitor().getQueryableName(this.getName(), this.hints);
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

		@Override
		public AbstractDBTable<?> getNewInstance() {
			final AbstractDBTable<?> table = this.beanProvider.apply(DatabaseBuilder.this.database);
			this.hints.put(DefaultQueryableHints.MANUAL, true);
			this.hints.put(DefaultQueryableHints.NAME_OVERRIDE, this.getFinalName());
			this.hints.putIfAbsent(DefaultQueryableHints.TABLE_ID, this.getFinalName());
			this.hints.putIfAbsent(DefaultQueryableHints.DEFINED_NAME, name);
			table.getCustomHints().putAll(this.hints);
			final String[] nameParts = this.getNameParts();

			final TableStructure tableStructure = new TableStructure(
					new StructureName(Arrays.stream(nameParts).collect(Collectors.joining(".")),
							nameParts,
							DatabaseBuilder.this.dbEntryUtils.getStructureVisitor().qualifiedName(nameParts)),
					(Class<? extends AbstractDBTable<?>>) this.hints.get(DefaultQueryableHints.TARGET_CLASS),
					(Class<? extends DatabaseEntry>) this.hints.get(DefaultQueryableHints.ENTRY_CLASS),
					this.hints);
			final ColumnData[] columns = this.columns.stream().map(ColumnPlan::getNewInstance).toArray(ColumnData[]::new);
			tableStructure.setColumns(columns);

			table.setTableStructure(tableStructure);
			return table;
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

			protected Function<ColumnPlan, ColumnType<?, ?>> columnTypeProvider;
			protected Function<ColumnPlan, StorageBinding> storageBindingProvider;

			@Override
			public void setName(final String name) {
				this.memberName = name;
			}

			@Override
			public String getName() {
				return this.memberName;
			}

			public ColumnPlan primaryKey(final boolean t) {
				this.hints.put(DefaultColumnHints.PRIMARY_KEY, t);
				return this;
			}

			public ColumnPlan autoIncrement(final boolean t) {
				this.hints.put(DefaultColumnHints.AUTO_INCREMENT, t);
				return this;
			}

			public ColumnPlan foreignKeyTable(Class<? extends SQLQueryable<?>> foreignClass) {
				this.hints.put(DefaultColumnHints.FOREIGN_KEY_TABLE, foreignClass);
				return this;
			}

			public ColumnPlan foreignKeyTableName(final String foreignName) {
				this.hints.put(DefaultColumnHints.FOREIGN_KEY_TABLE_NAME, foreignName);
				return this;
			}

			public ColumnPlan foreignKeyGroupId(final int groupId) {
				this.hints.put(DefaultColumnHints.FOREIGN_KEY_GROUP_ID, groupId);
				return this;
			}

			public ColumnPlan foreignKeyName(final int fkName) {
				this.hints.put(DefaultColumnHints.FOREIGN_KEY_NAME, fkName);
				return this;
			}

			public ColumnPlan uniqueGroupId(final int group) {
				this.hints.put(DefaultColumnHints.UNIQUE_INDEX, group);
				return this;
			}

			public ColumnPlan uniqueName(final String uqName) {
				this.hints.put(DefaultColumnHints.UNIQUE_NAME, uqName);
				return this;
			}

			public ColumnPlan nullable(final boolean t) {
				this.hints.put(DefaultColumnHints.NULLABLE, t);
				return this;
			}

			public ColumnPlan notNull(final boolean t) {
				this.hints.put(DefaultColumnHints.NULLABLE, !t);
				return this;
			}

			public ColumnPlan type(final ColumnType<?, ?> type) {
				this.type = type;
				return this;
			}

			public ColumnPlan storagebinding(final StorageBinding storageBinding) {
				this.storageBinding = storageBinding;
				return this;
			}

			public ColumnPlan type(final Function<ColumnPlan, ColumnType<?, ?>> columnTypeProvider) {
				this.columnTypeProvider = columnTypeProvider;
				return this;
			}

			public ColumnPlan storagebinding(final Function<ColumnPlan, StorageBinding> storageBindingProvider) {
				this.storageBindingProvider = storageBindingProvider;
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
				Objects.requireNonNull(this.memberName, "memberName cannot be null");

				final String finalName = this.getFinalName();
				final String[] stringParts = PCUtils.combineArrays(TablePlan.this.getNameParts(), new String[] { finalName });

				this.type = this.type == null ? this.columnTypeProvider.apply(this) : this.type;
				this.storageBinding = this.storageBinding == null ? this.storageBindingProvider.apply(this) : this.storageBinding;

				Objects.requireNonNull(this.storageBinding, "storageBinding cannot be null");
				Objects.requireNonNull(this.type, "type cannot be null");

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

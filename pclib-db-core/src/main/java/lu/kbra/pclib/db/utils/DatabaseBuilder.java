package lu.kbra.pclib.db.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lu.kbra.pclib.db.base.Database;
import lu.kbra.pclib.db.connector.impl.DatabaseConnector;
import lu.kbra.pclib.db.domain.column.ColumnData;
import lu.kbra.pclib.db.domain.column.type.ColumnType;
import lu.kbra.pclib.db.impl.DatabaseEntry;
import lu.kbra.pclib.db.impl.HintsOwner;
import lu.kbra.pclib.db.utils.impl.DatabaseEntryUtils;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
@EqualsAndHashCode
public class DatabaseBuilder {

	private final DatabaseScanner scanner;

	private final List<TablePlan> tablePlans = new ArrayList<>();

	public DatabaseBuilder(final DatabaseScanner scanner) {
		this.scanner = scanner;
	}

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

	public DatabaseBuilder(final Database db) {
		this(new DatabaseScanner(db));
	}

	public DatabaseBuilder(final DatabaseConnector connector, final String name) {
		this(new DatabaseScanner(new Database(connector, name)));
	}

	public DatabaseBuilder(final DatabaseConnector connector, final String name, final DatabaseEntryUtils dbEntryUtils) {
		this(new DatabaseScanner(new Database(connector, name, dbEntryUtils)));
	}

	public DatabaseBuilder(
			final DatabaseConnector connector,
			final String name,
			final Map<String, Object> customHints,
			final DatabaseEntryUtils dbEntryUtils) {
		this(new DatabaseScanner(new Database(connector, name, customHints, dbEntryUtils)));
	}

	public DatabaseBuilder addTable(final TablePlan tablePlan) {
		this.tablePlans.add(tablePlan);
		return this;
	}

	@Data
	@AllArgsConstructor
	class TablePlan
			implements
				SQLNameOwner<TablePlan>,
				SQLSchemaOwner<TablePlan>,
				ParentedBuilder<DatabaseBuilder>,
				SQLHintsOwner<TablePlan> {

		protected String name;
		protected String explicitName;
		protected String schema;
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

		@Override
		public String getFinalName() {
			return this.hasExplicitName() ? this.getExplicitName()
					: DatabaseBuilder.this.scanner.getStructureVisitor().getQueryableName(this.getName());
		}

		@Override
		public String getFinalSchema() {
			return this.hasSchema() ? this.getSchema() : DatabaseBuilder.this.scanner.getStructureVisitor().getDefaultSchema();
		}

		@Override
		public DatabaseBuilder build() {
			DatabaseBuilder.this.addTable(this);
			return DatabaseBuilder.this;
		}

		@Data
		class ColumnPlan
				implements
					SQLBuilder<ColumnData>,
					SQLNameOwner<ColumnPlan>,
					ParentedBuilder<TablePlan>,
					SQLHintsOwner<ColumnPlan> {

			protected String name;
			protected String explicitName;
			protected ColumnType<?, ?> type;
			protected Map<String, Object> hints = new HashMap<>();

			@Override
			public String getFinalName() {
				return this.hasExplicitName() ? this.getExplicitName()
						: DatabaseBuilder.this.scanner.getStructureVisitor().getQueryableName(this.getName());
			}

			@Override
			public TablePlan build() {
				TablePlan.this.addColumn(this);
				return TablePlan.this;
			}

			@Override
			public ColumnData getNewInstance() {
				return new ColumnData(name, name, null, hints, type, null, hints);
			}

		}

	}

	interface SQLNameOwner<T extends SQLNameOwner<T>> {

		void setName(String name);

		void setExplicitName(String name);

		default T name(final String name) {
			this.setName(name);
			return (T) this;
		}

		default T explicitName(final String explicitName) {
			this.setExplicitName(explicitName);
			return (T) this;
		}

		String getExplicitName();

		String getName();

		String getFinalName();

		default boolean hasExplicitName() {
			return this.getExplicitName() != null;
		}

	}

	interface SQLSchemaOwner<T extends SQLSchemaOwner<T>> {

		void setSchema(String schema);

		default T schema(final String schema) {
			this.setSchema(schema);
			return (T) this;
		}

		String getSchema();

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

		default T putAll(Map<String, Object> hints) {
			getHints().putAll(hints);
			return (T) this;
		}

		default T setAll(Map<String, Object> hints) {
			getHints().clear();
			getHints().putAll(hints);
			return (T) this;
		}

	}

	public interface SQLBuilder<T> {

		T getNewInstance();

	}

}

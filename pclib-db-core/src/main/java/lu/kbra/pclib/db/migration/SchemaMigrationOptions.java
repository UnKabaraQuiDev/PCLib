package lu.kbra.pclib.db.migration;

public class SchemaMigrationOptions {

	public static final SchemaMigrationOptions NONE = new SchemaMigrationOptions(false, false);

	public static SchemaMigrationOptions autoAddAndRemoveColumns() {
		return new SchemaMigrationOptions(true, true);
	}

	public static SchemaMigrationOptions autoAddColumns() {
		return new SchemaMigrationOptions(true, false);
	}

	private final boolean autoAddColumns;

	private final boolean autoRemoveColumns;

	public SchemaMigrationOptions() {
		this(false, false);
	}

	public SchemaMigrationOptions(final boolean autoAddColumns, final boolean autoRemoveColumns) {
		this.autoAddColumns = autoAddColumns;
		this.autoRemoveColumns = autoRemoveColumns;
	}

	public boolean isAutoAddColumns() {
		return this.autoAddColumns;
	}

	public boolean isAutoRemoveColumns() {
		return this.autoRemoveColumns;
	}

}

package lu.kbra.pclib.db.migration;

public class SchemaMigrationOptions {

	public static final SchemaMigrationOptions NONE = new SchemaMigrationOptions(false, false);

	private final boolean autoAddColumns;
	private final boolean autoRemoveColumns;

	public SchemaMigrationOptions() {
		this(false, false);
	}

	public SchemaMigrationOptions(final boolean autoAddColumns, final boolean autoRemoveColumns) {
		this.autoAddColumns = autoAddColumns;
		this.autoRemoveColumns = autoRemoveColumns;
	}

	public static SchemaMigrationOptions autoAddColumns() {
		return new SchemaMigrationOptions(true, false);
	}

	public static SchemaMigrationOptions autoAddAndRemoveColumns() {
		return new SchemaMigrationOptions(true, true);
	}

	public boolean isAutoAddColumns() {
		return this.autoAddColumns;
	}

	public boolean isAutoRemoveColumns() {
		return this.autoRemoveColumns;
	}

}

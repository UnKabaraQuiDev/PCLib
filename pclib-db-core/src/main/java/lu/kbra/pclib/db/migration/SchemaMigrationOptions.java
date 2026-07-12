package lu.kbra.pclib.db.migration;

import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Builder
@Getter
@RequiredArgsConstructor
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

}

package lu.kbra.pclib.db.migration;

import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@Builder
@RequiredArgsConstructor
public class SchemaMigrationOptions {

	public static final SchemaMigrationOptions NONE = new SchemaMigrationOptions(false, false);
	public static final SchemaMigrationOptions ADD_AND_REMOVE = new SchemaMigrationOptions(true, true);
	public static final SchemaMigrationOptions ADD = new SchemaMigrationOptions(true, false);

	private final boolean autoAddColumns;
	private final boolean autoRemoveColumns;

}

package lu.kbra.pclib.db.migration.schema.data;

import java.sql.Timestamp;
import java.time.Duration;

import lu.kbra.pclib.db.annotations.entry.AutoIncrement;
import lu.kbra.pclib.db.annotations.entry.Column;
import lu.kbra.pclib.db.annotations.entry.Nullable;
import lu.kbra.pclib.db.annotations.entry.PrimaryKey;
import lu.kbra.pclib.db.annotations.entry.Unique;
import lu.kbra.pclib.db.annotations.entry.def.MaxLength;
import lu.kbra.pclib.db.impl.DatabaseEntry;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MigrationHistoryData implements DatabaseEntry {

	@Column
	@PrimaryKey
	@AutoIncrement
	private Long id;

	/**
	 * Stable identifier of the manual migration.
	 */
	@Column
	@Unique
	private @MaxLength(64) String migrationId;

	@Column
	private int order;

	@Column
	private String name;

	@Column
	@Nullable
	private String beforeSchemaHash;

	@Column
	@Nullable
	private String afterSchemaHash;

	@Column
	private Timestamp appliedAt;

	@Column
	private Duration executionTimeMs;

	@Column
	@Nullable
	private @MaxLength(10) String applicationVersion;

	@Column
	@Nullable
	private String description;

}

package lu.kbra.pclib.db.migration.schema.data;

import java.sql.Timestamp;
import java.time.Duration;

import lu.kbra.pclib.PCUtils;
import lu.kbra.pclib.db.annotations.entry.AutoIncrement;
import lu.kbra.pclib.db.annotations.entry.Column;
import lu.kbra.pclib.db.annotations.entry.Nullable;
import lu.kbra.pclib.db.annotations.entry.PrimaryKey;
import lu.kbra.pclib.db.annotations.entry.Unique;
import lu.kbra.pclib.db.annotations.entry.def.MaxLength;
import lu.kbra.pclib.db.impl.DatabaseEntry;
import lu.kbra.pclib.db.migration.MigrationType;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MigrationData implements DatabaseEntry {

	@Column
	@PrimaryKey
	@AutoIncrement
	private Long id;

	/**
	 * Monotonically increasing schema version.
	 */
	@Column
	@Unique
	private int version;

	/**
	 * Stable identifier for this schema state.
	 */
	@Column
	@Unique
	private @MaxLength(PCUtils.SHA_256_CHAR_LENGTH) String schemaHash;

	/**
	 * When this migration was applied.
	 */
	@Column
	private Timestamp appliedAt;

	/**
	 * How the migration was created.
	 *
	 * e.g. AUTOMATIC, EXPLICIT, INITIAL
	 */
	@Column
	private MigrationType type;

	/**
	 * Application/library version that created it.
	 */
	@Column
	@Nullable
	private @MaxLength(10) String applicationVersion;

	/**
	 * Optional human-readable description.
	 */
	@Column
	@Nullable
	private String description;

	/**
	 * How long the migration took, in milliseconds.
	 */
	@Column
	@Nullable
	private Duration executionTime;

}

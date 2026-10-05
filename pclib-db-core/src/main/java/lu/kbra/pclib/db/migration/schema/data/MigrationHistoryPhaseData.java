package lu.kbra.pclib.db.migration.schema.data;

import java.sql.Timestamp;
import java.time.Duration;

import lu.kbra.pclib.db.annotations.entry.AutoIncrement;
import lu.kbra.pclib.db.annotations.entry.Column;
import lu.kbra.pclib.db.annotations.entry.ForeignKey;
import lu.kbra.pclib.db.annotations.entry.Nullable;
import lu.kbra.pclib.db.annotations.entry.PrimaryKey;
import lu.kbra.pclib.db.annotations.entry.Unique;
import lu.kbra.pclib.db.annotations.entry.def.MaxLength;
import lu.kbra.pclib.db.impl.DatabaseEntry;
import lu.kbra.pclib.db.migration.MigrationPhase;
import lu.kbra.pclib.db.migration.schema.table.MigrationHistoryTable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MigrationHistoryPhaseData implements DatabaseEntry {

	@Column
	@PrimaryKey
	@AutoIncrement
	private Long id;

	@Column
	@Unique
	@ForeignKey(table = MigrationHistoryTable.class)
	private Long migrationId;

	/**
	 * Stable identifier of the manual migration.
	 */
	@Column
	@Unique
	private @MaxLength(64) String phaseId;

	@Column
	private int order;

	@Column
	private @MaxLength(24) MigrationPhase phase;

	@Column
	@Nullable
	private String name;

	@Column
	private Timestamp appliedAt;

	@Column
	private Duration executionTime;

	@Column
	@Nullable
	private @MaxLength(10) String applicationVersion;

}

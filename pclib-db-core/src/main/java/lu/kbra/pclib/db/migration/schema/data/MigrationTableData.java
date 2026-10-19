package lu.kbra.pclib.db.migration.schema.data;

import lu.kbra.pclib.db.annotations.entry.AutoIncrement;
import lu.kbra.pclib.db.annotations.entry.Column;
import lu.kbra.pclib.db.annotations.entry.ForeignKey;
import lu.kbra.pclib.db.annotations.entry.Nullable;
import lu.kbra.pclib.db.annotations.entry.PrimaryKey;
import lu.kbra.pclib.db.impl.DatabaseEntry;
import lu.kbra.pclib.db.migration.schema.table.MigrationTable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MigrationTableData implements DatabaseEntry {

	@Column
	@PrimaryKey
	@AutoIncrement
	private Long id;

	@Column
	@ForeignKey(table = MigrationTable.class)
	private long migrationId;

	@Column
	private String name;

	@Column
	private String qualifiedName;

	/**
	 * Original table structure name, if useful for diagnostics.
	 */
	@Column
	@Nullable
	private String structureName;

	@Column
	private String tableClassName;

	@Column
	private String tableId;

}

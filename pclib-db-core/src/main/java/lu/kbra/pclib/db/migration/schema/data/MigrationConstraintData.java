package lu.kbra.pclib.db.migration.schema.data;

import lu.kbra.pclib.db.annotations.entry.AutoIncrement;
import lu.kbra.pclib.db.annotations.entry.Column;
import lu.kbra.pclib.db.annotations.entry.ForeignKey;
import lu.kbra.pclib.db.annotations.entry.PrimaryKey;
import lu.kbra.pclib.db.impl.DatabaseEntry;
import lu.kbra.pclib.db.migration.schema.ConstraintType;
import lu.kbra.pclib.db.migration.schema.table.MigrationTableTable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MigrationConstraintData implements DatabaseEntry {

	@Column
	@PrimaryKey
	@AutoIncrement
	private long id;

	@Column
	@ForeignKey(table = MigrationTableTable.class)
	private long tableId;

	@Column
	private String name;

	@Column
	private ConstraintType type;

	/**
	 * Serialized constraint definition.
	 */
	@Column
	private String definition;

}

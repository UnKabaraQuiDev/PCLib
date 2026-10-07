package lu.kbra.pclib.db.migration.schema.data;

import lu.kbra.pclib.db.annotations.entry.AutoIncrement;
import lu.kbra.pclib.db.annotations.entry.Column;
import lu.kbra.pclib.db.annotations.entry.ForeignKey;
import lu.kbra.pclib.db.annotations.entry.Nullable;
import lu.kbra.pclib.db.annotations.entry.PrimaryKey;
import lu.kbra.pclib.db.impl.DatabaseEntry;
import lu.kbra.pclib.db.migration.schema.table.MigrationTableTable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MigrationColumnData implements DatabaseEntry {

	@Column
	@PrimaryKey
	@AutoIncrement
	private Long id;

	@Column
	@ForeignKey(table = MigrationTableTable.class)
	private long tableId;

	@Column
	private String name;

	@Column
	private String qualifiedName;

	/**
	 * Database type name, e.g. VARCHAR(20), INTEGER, TEXT.
	 */
	@Column
	private String typeName;

	@Column
	private boolean nullable;

	@Column
	private boolean primaryKey;

	@Column
	private boolean unique;

	@Column
	private boolean autoIncrement;

	@Column
	@Nullable
	private String defaultValue;

}

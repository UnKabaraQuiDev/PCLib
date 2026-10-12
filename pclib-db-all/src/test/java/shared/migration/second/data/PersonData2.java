package shared.migration.second.data;

import lu.kbra.pclib.db.annotations.entry.AutoIncrement;
import lu.kbra.pclib.db.annotations.entry.Column;
import lu.kbra.pclib.db.annotations.entry.ForeignKey;
import lu.kbra.pclib.db.annotations.entry.Nullable;
import lu.kbra.pclib.db.annotations.entry.PrimaryKey;
import lu.kbra.pclib.db.annotations.entry.def.MaxLength;
import lu.kbra.pclib.db.impl.DatabaseEntry;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import shared.migration.second.table.GarageTable2;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PersonData2 implements DatabaseEntry {

	@Column
	@PrimaryKey
	@AutoIncrement
	private Long id;

	@Column
	@Nullable
	@ForeignKey(table = GarageTable2.class)
	private Long garageId;

	@Column
	@MaxLength(200)
	private String email;

	@Column
	@MaxLength(100)
	private String name;

}

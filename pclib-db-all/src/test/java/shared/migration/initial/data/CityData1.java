package shared.migration.initial.data;

import lu.kbra.pclib.db.annotations.entry.AutoIncrement;
import lu.kbra.pclib.db.annotations.entry.Column;
import lu.kbra.pclib.db.annotations.entry.ForeignKey;
import lu.kbra.pclib.db.annotations.entry.PrimaryKey;
import lu.kbra.pclib.db.annotations.entry.def.MaxLength;
import lu.kbra.pclib.db.impl.DatabaseEntry;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import shared.migration.initial.table.GarageTable1;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CityData1 implements DatabaseEntry {

	@Column
	@PrimaryKey
	@AutoIncrement
	private Long id;

	@Column
	@ForeignKey(table = GarageTable1.class)
	private Long garageId;

	@Column
	@MaxLength(80)
	private String name;

	@Column
	@MaxLength(10)
	private String postalCode;

}

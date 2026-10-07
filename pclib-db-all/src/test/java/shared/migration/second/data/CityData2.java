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
import shared.migration.second.table.CountryTable2;
import shared.migration.second.table.GarageTable2;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CityData2 implements DatabaseEntry {

	@Column
	@PrimaryKey
	@AutoIncrement
	private Long id;

	@Column
	@ForeignKey(table = GarageTable2.class)
	private Long garageId;

	@Column
	@MaxLength(120)
	private String name;

	@Column
	@MaxLength(10)
	private String zipCode;

	@Column
	@MaxLength(2)
	private String countryCode;

	@Column
	@ForeignKey(table = CountryTable2.class)
	@Nullable
	private Long countryId;

}

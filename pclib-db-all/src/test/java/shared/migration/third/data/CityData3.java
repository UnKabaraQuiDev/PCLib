package shared.migration.third.data;

import lu.kbra.pclib.db.annotations.entry.AutoIncrement;
import lu.kbra.pclib.db.annotations.entry.Column;
import lu.kbra.pclib.db.annotations.entry.ForeignKey;
import lu.kbra.pclib.db.annotations.entry.PrimaryKey;
import lu.kbra.pclib.db.annotations.entry.def.MaxLength;
import lu.kbra.pclib.db.impl.DatabaseEntry;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import shared.migration.third.table.CountryTable3;
import shared.migration.third.table.GarageTable3;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CityData3 implements DatabaseEntry {

	@Column
	@PrimaryKey
	@AutoIncrement
	private Long id;

	@Column
	@ForeignKey(table = GarageTable3.class)
	private Long garageId;

	@Column
	@MaxLength(200)
	private String name;

	@Column
	@MaxLength(10)
	private String zipCode;

	@Column
	@ForeignKey(table = CountryTable3.class)
	private Long countryId;

}

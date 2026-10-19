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
import shared.migration.third.table.AddressTable3;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AddressData3 implements DatabaseEntry {

	@Column
	@PrimaryKey
	@AutoIncrement
	private Long id;

	@Column
	@ForeignKey(table = AddressTable3.class)
	private Long cityId;

	@Column
	@MaxLength(200)
	private String street;

	@Column
	@MaxLength(10)
	private String postalCode;

}

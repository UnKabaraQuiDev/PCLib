package sqlite;

import java.sql.Timestamp;

import lu.kbra.pclib.PCUtils;
import lu.kbra.pclib.db.annotations.entry.AutoIncrement;
import lu.kbra.pclib.db.annotations.entry.Column;
import lu.kbra.pclib.db.annotations.entry.ForeignKey;
import lu.kbra.pclib.db.annotations.entry.PrimaryKey;
import lu.kbra.pclib.db.annotations.entry.def.MaxLength;
import lu.kbra.pclib.db.annotations.entry.def.TimeVersion;
import lu.kbra.pclib.db.impl.DatabaseEntry;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CarData implements DatabaseEntry {

	@Column
	@AutoIncrement
	@PrimaryKey
	protected int id;

	@Column(name = "person_id")
	@ForeignKey(table = PersonTable.class)
	protected int personId;

	@Column
	protected @MaxLength(50) String brand;

	@Column
	@TimeVersion
	protected Timestamp version;

	public CarData(final int personId, final String brand) {
		this.personId = personId;
		this.brand = brand;
	}

	@Override
	public CarData clone() {
		return PCUtils.safeClone(super::clone);
	}

}

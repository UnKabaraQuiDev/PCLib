package postgres;

import lu.kbra.pclib.PCUtils;
import lu.kbra.pclib.db.autobuild.column.AutoIncrement;
import lu.kbra.pclib.db.autobuild.column.Column;
import lu.kbra.pclib.db.autobuild.column.ForeignKey;
import lu.kbra.pclib.db.autobuild.column.PrimaryKey;
import lu.kbra.pclib.db.autobuild.column.type.meta.MaxLength;
import lu.kbra.pclib.db.impl.DataBaseEntry;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CarData implements DataBaseEntry {

	@Column
	@AutoIncrement
	@PrimaryKey
	protected int id;

	@Column(name = "person_id")
	@ForeignKey(table = PersonTable.class)
	protected int personId;

	@Column
	protected @MaxLength(50) String brand;

	public CarData() {
	}

	public CarData(final int personId, final String brand) {
		this.personId = personId;
		this.brand = brand;
	}

	@Override
	public CarData clone() {
		return PCUtils.safeClone(super::clone);
	}

}

package postgres;

import lu.kbra.pclib.PCUtils;
import lu.kbra.pclib.db.annotations.entry.AutoIncrement;
import lu.kbra.pclib.db.annotations.entry.Column;
import lu.kbra.pclib.db.annotations.entry.ForeignKey;
import lu.kbra.pclib.db.annotations.entry.PrimaryKey;
import lu.kbra.pclib.db.annotations.entry.def.MaxLength;
import lu.kbra.pclib.db.impl.DataBaseEntry;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GarageData implements DataBaseEntry {

	@Column
	@AutoIncrement
	@PrimaryKey
	protected int id;

	@Column(name = "car_id")
	@ForeignKey(table = CarTable.class)
	protected int carId;

	@Column
	protected @MaxLength(80) String name;

	public GarageData(final int carId, final String name) {
		this.carId = carId;
		this.name = name;
	}

	@Override
	public GarageData clone() {
		return PCUtils.safeClone(super::clone);
	}

}

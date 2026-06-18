package mysql;

import lu.kbra.pclib.db.autobuild.column.AutoIncrement;
import lu.kbra.pclib.db.autobuild.column.Column;
import lu.kbra.pclib.db.autobuild.column.ForeignKey;
import lu.kbra.pclib.db.autobuild.column.PrimaryKey;
import lu.kbra.pclib.db.autobuild.column.type.meta.MaxLength;
import lu.kbra.pclib.db.impl.DataBaseEntry;

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

	public GarageData() {
	}

	public GarageData(final int carId, final String name) {
		this.carId = carId;
		this.name = name;
	}

}

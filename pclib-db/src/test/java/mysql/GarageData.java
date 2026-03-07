package mysql;

import lu.kbra.pclib.db.autobuild.column.AutoIncrement;
import lu.kbra.pclib.db.autobuild.column.Column;
import lu.kbra.pclib.db.autobuild.column.ForeignKey;
import lu.kbra.pclib.db.autobuild.column.PrimaryKey;
import lu.kbra.pclib.db.impl.DataBaseEntry;

public class GarageData implements DataBaseEntry {

	@Column
	@AutoIncrement
	@PrimaryKey
	protected int id;

	@Column(name = "car_id")
	@ForeignKey(table = CarTable.class)
	protected int carId;

	@Column(length = 80)
	protected String name;

	public GarageData() {
	}

	public GarageData(int carId, String name) {
		this.carId = carId;
		this.name = name;
	}

}
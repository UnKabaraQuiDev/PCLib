package mysql;

import lu.kbra.pclib.db.autobuild.column.AutoIncrement;
import lu.kbra.pclib.db.autobuild.column.Column;
import lu.kbra.pclib.db.autobuild.column.ForeignKey;
import lu.kbra.pclib.db.autobuild.column.PrimaryKey;
import lu.kbra.pclib.db.impl.DataBaseEntry;

public class CityData implements DataBaseEntry {

	@Column
	@AutoIncrement
	@PrimaryKey
	protected int id;

	@Column(name = "garage_id")
	@ForeignKey(table = GarageTable.class)
	protected int garageId;

	@Column(length = 80)
	protected String name;

	public CityData() {
	}

	public CityData(int garageId, String name) {
		this.garageId = garageId;
		this.name = name;
	}
	
}
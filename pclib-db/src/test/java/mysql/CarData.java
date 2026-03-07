package mysql;

import lu.kbra.pclib.db.autobuild.column.AutoIncrement;
import lu.kbra.pclib.db.autobuild.column.Column;
import lu.kbra.pclib.db.autobuild.column.ForeignKey;
import lu.kbra.pclib.db.autobuild.column.PrimaryKey;
import lu.kbra.pclib.db.impl.DataBaseEntry;

public class CarData implements DataBaseEntry {

	@Column
	@AutoIncrement
	@PrimaryKey
	protected int id;

	@Column(name = "person_id")
	@ForeignKey(table = PersonTable.class)
	protected int personId;

	@Column(length = 50)
	protected String brand;

	public CarData() {
	}

	public CarData(int personId, String brand) {
		this.personId = personId;
		this.brand = brand;
	}

}
package mysql;

import lu.kbra.pclib.PCUtils;
import lu.kbra.pclib.db.annotations.entry.AutoIncrement;
import lu.kbra.pclib.db.annotations.entry.Column;
import lu.kbra.pclib.db.annotations.entry.ForeignKey;
import lu.kbra.pclib.db.annotations.entry.PrimaryKey;
import lu.kbra.pclib.db.autobuild.column.meta.MaxLength;
import lu.kbra.pclib.db.impl.DataBaseEntry;

public class CityData implements DataBaseEntry {

	@Column
	@AutoIncrement
	@PrimaryKey
	protected int id;

	@Column(name = "garage_id")
	@ForeignKey(table = GarageTable.class)
	protected int garageId;

	@Column
	protected @MaxLength(80) String name;

	public CityData(final int garageId, final String name) {
		this.garageId = garageId;
		this.name = name;
	}

	@Override
	public CityData clone() {
		return PCUtils.safeClone(super::clone);
	}

}

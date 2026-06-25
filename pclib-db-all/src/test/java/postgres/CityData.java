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

	public CityData() {
	}

	public CityData(final int garageId, final String name) {
		this.garageId = garageId;
		this.name = name;
	}

	@Override
	public CityData clone() {
		return PCUtils.safeClone(super::clone);
	}

}

package mysql;

import java.util.Objects;

import lu.kbra.pclib.PCUtils;
import lu.kbra.pclib.db.autobuild.column.AutoIncrement;
import lu.kbra.pclib.db.autobuild.column.Column;
import lu.kbra.pclib.db.autobuild.column.ForeignKey;
import lu.kbra.pclib.db.autobuild.column.PrimaryKey;
import lu.kbra.pclib.db.autobuild.column.type.meta.MaxLength;
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

	@Override
	public boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null || this.getClass() != obj.getClass()) {
			return false;
		}
		final CityData other = (CityData) obj;
		return this.garageId == other.garageId && this.id == other.id && Objects.equals(this.name, other.name);
	}

	@Override
	public int hashCode() {
		return Objects.hash(Integer.valueOf(this.garageId), Integer.valueOf(this.id), this.name);
	}

	@Override
	public String toString() {
		return "CityData@" + System.identityHashCode(this) + " [id=" + this.id + ", garageId=" + this.garageId + ", name=" + this.name
				+ "]";
	}

}

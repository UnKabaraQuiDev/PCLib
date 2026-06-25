package mysql;

import java.util.Objects;

import lu.kbra.pclib.PCUtils;
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

	@Override
	public GarageData clone() {
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
		final GarageData other = (GarageData) obj;
		return this.carId == other.carId && this.id == other.id && Objects.equals(this.name, other.name);
	}

	public int getCarId() {
		return this.carId;
	}

	public int getId() {
		return this.id;
	}

	public String getName() {
		return this.name;
	}

	@Override
	public int hashCode() {
		return Objects.hash(Integer.valueOf(this.carId), Integer.valueOf(this.id), this.name);
	}

	public void setCarId(final int carId) {
		this.carId = carId;
	}

	public void setName(final String name) {
		this.name = name;
	}

	@Override
	public String toString() {
		return "GarageData@" + System.identityHashCode(this) + " [id=" + this.id + ", carId=" + this.carId + ", name=" + this.name + "]";
	}

}

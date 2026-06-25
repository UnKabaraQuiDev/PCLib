package mysql;

import java.util.Objects;

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

	@Override
	public boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null || this.getClass() != obj.getClass()) {
			return false;
		}
		final CarData other = (CarData) obj;
		return Objects.equals(this.brand, other.brand) && this.id == other.id && this.personId == other.personId;
	}

	public String getBrand() {
		return this.brand;
	}

	public int getId() {
		return this.id;
	}

	public int getPersonId() {
		return this.personId;
	}

	@Override
	public int hashCode() {
		return Objects.hash(this.brand, Integer.valueOf(this.id), Integer.valueOf(this.personId));
	}

	public void setBrand(final String brand) {
		this.brand = brand;
	}

	public void setPersonId(final int personId) {
		this.personId = personId;
	}

	@Override
	public String toString() {
		return "CarData@" + System.identityHashCode(this) + " [id=" + this.id + ", personId=" + this.personId + ", brand=" + this.brand
				+ "]";
	}

}

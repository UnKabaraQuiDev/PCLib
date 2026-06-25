package mysql;

import java.util.Objects;

import lu.kbra.pclib.PCUtils;
import lu.kbra.pclib.db.autobuild.column.Column;
import lu.kbra.pclib.db.impl.DataBaseEntry;

public class PersonCarROData implements DataBaseEntry {

	@Column
	protected Integer personId;

	@Column
	protected String personName;

	@Column
	protected Integer carId;

	@Column
	protected String carBrand;

	public PersonCarROData() {
	}

	@Override
	public PersonCarROData clone() {
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
		final PersonCarROData other = (PersonCarROData) obj;
		return Objects.equals(this.carBrand, other.carBrand) && Objects.equals(this.carId, other.carId)
				&& Objects.equals(this.personId, other.personId) && Objects.equals(this.personName, other.personName);
	}

	@Override
	public int hashCode() {
		return Objects.hash(this.carBrand, this.carId, this.personId, this.personName);
	}

}

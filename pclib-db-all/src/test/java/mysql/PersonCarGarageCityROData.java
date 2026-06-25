package mysql;

import java.util.Objects;

import lu.kbra.pclib.PCUtils;
import lu.kbra.pclib.db.autobuild.column.Column;
import lu.kbra.pclib.db.impl.DataBaseEntry.ReadOnlyDataBaseEntry;

public class PersonCarGarageCityROData implements ReadOnlyDataBaseEntry {

	@Column(name = "person_name")
	protected String personName;

	@Column(name = "car_brand")
	protected String carBrand;

	@Column(name = "garage_name")
	protected String garageName;

	@Column(name = "city_name")
	protected String cityName;

	public PersonCarGarageCityROData() {
	}

	@Override
	public PersonCarGarageCityROData clone() {
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
		final PersonCarGarageCityROData other = (PersonCarGarageCityROData) obj;
		return Objects.equals(this.carBrand, other.carBrand) && Objects.equals(this.cityName, other.cityName)
				&& Objects.equals(this.garageName, other.garageName) && Objects.equals(this.personName, other.personName);
	}

	public String getCarBrand() {
		return this.carBrand;
	}

	public String getCityName() {
		return this.cityName;
	}

	public String getGarageName() {
		return this.garageName;
	}

	public String getPersonName() {
		return this.personName;
	}

	@Override
	public int hashCode() {
		return Objects.hash(this.carBrand, this.cityName, this.garageName, this.personName);
	}

	public void setCarBrand(final String carBrand) {
		this.carBrand = carBrand;
	}

	public void setCityName(final String cityName) {
		this.cityName = cityName;
	}

	public void setGarageName(final String garageName) {
		this.garageName = garageName;
	}

	public void setPersonName(final String personName) {
		this.personName = personName;
	}

}

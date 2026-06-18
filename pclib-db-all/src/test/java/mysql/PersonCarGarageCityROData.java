package mysql;

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

}

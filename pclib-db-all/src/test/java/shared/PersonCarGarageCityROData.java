package shared;

import lu.kbra.pclib.PCUtils;
import lu.kbra.pclib.db.annotations.entry.Column;
import lu.kbra.pclib.db.impl.DatabaseEntry.ReadOnlyDatabaseEntry;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PersonCarGarageCityROData implements ReadOnlyDatabaseEntry {

	@Column(name = "person_name")
	protected String personName;

	@Column(name = "car_brand")
	protected String carBrand;

	@Column(name = "garage_name")
	protected String garageName;

	@Column(name = "city_name")
	protected String cityName;

	@Override
	public PersonCarGarageCityROData clone() {
		return PCUtils.safeClone(super::clone);
	}

}

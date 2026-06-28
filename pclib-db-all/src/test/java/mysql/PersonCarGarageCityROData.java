package mysql;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lu.kbra.pclib.PCUtils;
import lu.kbra.pclib.db.annotations.entry.Column;
import lu.kbra.pclib.db.impl.DataBaseEntry.ReadOnlyDataBaseEntry;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PersonCarGarageCityROData implements ReadOnlyDataBaseEntry {

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

package mysql;

import lu.kbra.pclib.PCUtils;
import lu.kbra.pclib.db.autobuild.column.Column;
import lu.kbra.pclib.db.impl.DataBaseEntry;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PersonCarROData implements DataBaseEntry {

	@Column
	protected Integer personId;

	@Column
	protected String personName;

	@Column
	protected Integer carId;

	@Column
	protected String carBrand;

	@Override
	public PersonCarROData clone() {
		return PCUtils.safeClone(super::clone);
	}

}

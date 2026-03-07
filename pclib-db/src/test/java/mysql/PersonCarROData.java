package mysql;

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

}
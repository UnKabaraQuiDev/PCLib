package postgres;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lu.kbra.pclib.PCUtils;
import lu.kbra.pclib.db.annotations.entry.Column;
import lu.kbra.pclib.db.impl.DataBaseEntry;

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

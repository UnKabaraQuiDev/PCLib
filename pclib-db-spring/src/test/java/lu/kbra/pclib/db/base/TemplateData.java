package lu.kbra.pclib.db.base;

import lu.kbra.pclib.PCUtils;
import lu.kbra.pclib.db.annotations.entry.AutoIncrement;
import lu.kbra.pclib.db.annotations.entry.Column;
import lu.kbra.pclib.db.annotations.entry.PrimaryKey;
import lu.kbra.pclib.db.annotations.entry.Unique;
import lu.kbra.pclib.db.annotations.entry.def.MaxLength;
import lu.kbra.pclib.db.impl.DatabaseEntry;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TemplateData implements DatabaseEntry {

	@Column
	@PrimaryKey
	@AutoIncrement
	protected long id;

	@Column
	@Unique
	protected @MaxLength(64) String event;

	public TemplateData(final String event) {
		this.event = event;
	}

	@Override
	public TemplateData clone() {
		return PCUtils.safeClone(super::clone);
	}

}

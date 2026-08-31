package lu.kbra.pclib.db.base;

import lu.kbra.pclib.PCUtils;
import lu.kbra.pclib.db.annotations.entry.AutoIncrement;
import lu.kbra.pclib.db.annotations.entry.Column;
import lu.kbra.pclib.db.annotations.entry.ForeignKey;
import lu.kbra.pclib.db.annotations.entry.Nullable;
import lu.kbra.pclib.db.annotations.entry.PrimaryKey;
import lu.kbra.pclib.db.domain.table.ForeignKeyData.OnAction;
import lu.kbra.pclib.db.impl.DatabaseEntry;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CarData implements DatabaseEntry {

	@Column
	@PrimaryKey
	@AutoIncrement
	private long carId;

	@Column
	@ForeignKey(table = AuditLogTable.class, onDelete = OnAction.CASCADE, onUpdate = OnAction.CASCADE)
	@Nullable
	private Long personId;

	@Override
	public CarData clone() {
		return PCUtils.safeClone(super::clone);
	}

}

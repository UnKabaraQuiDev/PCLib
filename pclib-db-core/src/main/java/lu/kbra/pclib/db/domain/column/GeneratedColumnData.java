package lu.kbra.pclib.db.domain.column;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lu.kbra.pclib.PCUtils;
import lu.kbra.pclib.db.annotations.entry.Generated;
import lu.kbra.pclib.db.annotations.entry.Generated.Type;

@Data
@EqualsAndHashCode(callSuper = true)
public class GeneratedColumnData extends ColumnData {

	protected Type storageType;

	public GeneratedColumnData(final ColumnData cd, final Generated gen) {
		super(cd.name,
				cd.typeHints,
				cd.type,
				cd.autoIncrement,
				cd.nullable,
				cd.defaultValue,
				cd.onUpdate,
				cd.primaryKey,
				cd.unique,
				cd.foreignKey,
				cd.field,
				cd.hints);

		this.storageType = gen.value();
	}

	@Override
	public GeneratedColumnData clone() {
		return PCUtils.safeClone(super::clone);
	}

	@Override
	public boolean isGenerated() {
		return true;
	}

}

package lu.kbra.pclib.db.autobuild.column;

import lu.kbra.pclib.db.annotations.entry.Generated;
import lu.kbra.pclib.db.annotations.entry.Generated.Type;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class GeneratedColumnData extends ColumnData {

	protected Type storageType;

	public GeneratedColumnData(final ColumnData cd, final Generated gen) {
		super.name = cd.name;
		super.type = cd.type;
		super.autoIncrement = cd.autoIncrement;
		super.nullable = cd.nullable;
		super.defaultValue = cd.defaultValue;
		super.onUpdate = cd.onUpdate;
		super.primaryKey = cd.primaryKey;
		super.unique = cd.unique;
		super.foreignKey = cd.foreignKey;
		super.field = cd.field;

		this.storageType = gen.value();
	}

	@Override
	public boolean isGenerated() {
		return true;
	}

}

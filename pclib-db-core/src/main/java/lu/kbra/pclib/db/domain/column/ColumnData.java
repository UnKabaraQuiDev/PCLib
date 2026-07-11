package lu.kbra.pclib.db.domain.column;

import java.lang.reflect.Field;
import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Data;
import lu.kbra.pclib.PCUtils;
import lu.kbra.pclib.db.domain.column.meta.DefaultColumnHints;
import lu.kbra.pclib.db.domain.column.type.ColumnType;
import lu.kbra.pclib.db.domain.table.StructureName;
import lu.kbra.pclib.db.domain.table.StructureNameOwner;
import lu.kbra.pclib.db.impl.HintsOwner;

@Data
@AllArgsConstructor
public class ColumnData implements Cloneable, StructureNameOwner, HintsOwner {

	protected final String localName;
	protected final String localQualifiedName;
	protected final StructureName structureName;
	protected final Map<String, Object> typeHints;
	protected final ColumnType type;
	protected final Field field;
	protected final Map<String, Object> hints;

	@Override
	public ColumnData clone() {
		return PCUtils.safeClone(super::clone);
	}

	public boolean hasDefaultValue() {
		return hasHint(DefaultColumnHints.DEFAULT_VALUE);
	}

	public boolean hasOnUpdate() {
		return hasHint(DefaultColumnHints.ON_UPDATE);
	}

	public <V> V getTypeHint(final String key) {
		return (V) typeHints.get(key);
	}

	public <V> V getTypeHint(final String key, final V default_) {
		return (V) typeHints.getOrDefault(key, default_);
	}

	public <V> boolean hasTypeHint(final String key) {
		return this.typeHints.containsKey(key);
	}

	public boolean isGenerated() {
		return hasHint(DefaultColumnHints.GENERATED_STORAGE_TYPE);
	}

	public boolean isPrimaryKey() {
		return hasHint(DefaultColumnHints.PRIMARY_KEY);
	}

	public boolean isUnique() {
		return hasHint(DefaultColumnHints.UNIQUE);
	}

	public boolean isForeignKey() {
		return hasHint(DefaultColumnHints.FOREIGN_KEY_TABLE);
	}

	public boolean isNullable() {
		return hasHint(DefaultColumnHints.NULLABLE);
	}

	public boolean isAutoIncrement() {
		return hasHint(DefaultColumnHints.AUTO_INCREMENT);
	}

}

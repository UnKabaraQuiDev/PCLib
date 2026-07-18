package lu.kbra.pclib.db.domain.column;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

import lu.kbra.pclib.PCUtils;
import lu.kbra.pclib.db.domain.column.meta.DefaultColumnHints;
import lu.kbra.pclib.db.domain.column.type.ColumnType;
import lu.kbra.pclib.db.domain.table.StructureName;
import lu.kbra.pclib.db.domain.table.StructureNameOwner;
import lu.kbra.pclib.db.impl.HintsOwner;
import lu.kbra.pclib.impl.MapConvertible;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ColumnData implements Cloneable, StructureNameOwner, HintsOwner, MapConvertible {

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
		return this.hasHint(DefaultColumnHints.DEFAULT_VALUE);
	}

	public boolean hasOnUpdate() {
		return this.hasHint(DefaultColumnHints.ON_UPDATE);
	}

	public <V> V getTypeHint(final String key) {
		return (V) this.typeHints.get(key);
	}

	public <V> V getTypeHint(final String key, final V default_) {
		return (V) this.typeHints.getOrDefault(key, default_);
	}

	public <V> boolean hasTypeHint(final String key) {
		return this.typeHints.containsKey(key);
	}

	public boolean isGenerated() {
		return this.hasHint(DefaultColumnHints.GENERATED_STORAGE_TYPE);
	}

	public boolean isPrimaryKey() {
		return this.hasHint(DefaultColumnHints.PRIMARY_KEY) && this.<Boolean>getHint(DefaultColumnHints.PRIMARY_KEY);
	}

	public boolean isUnique() {
		return this.hasHint(DefaultColumnHints.UNIQUE);
	}

	public boolean isForeignKey() {
		return this.hasHint(DefaultColumnHints.FOREIGN_KEY_TABLE);
	}

	public boolean isNullable() {
		return this.hasHint(DefaultColumnHints.NULLABLE) && this.<Boolean>getHint(DefaultColumnHints.NULLABLE);
	}

	public boolean isAutoIncrement() {
		return this.hasHint(DefaultColumnHints.AUTO_INCREMENT) && this.<Boolean>getHint(DefaultColumnHints.AUTO_INCREMENT);
	}

	public boolean hasUpdateExpression() {
		return this.hasHint(DefaultColumnHints.UPDATE_EXPR);
	}

	public boolean needsUpdateExpressionValue() {
		return this.hasHint(DefaultColumnHints.UPDATE_EXPR_VALUE) && this.<Boolean>getHint(DefaultColumnHints.UPDATE_EXPR_VALUE);
	}

	@Override
	public Map<String, Object> toMap() {
		final Map<String, Object> map = new HashMap<>();

		map.put("localName", localName);
		map.put("localQualifiedName", localQualifiedName);
		map.put("structureName", structureName);
		map.put("typeHints", typeHints);
		map.put("type", type);
		map.put("field", field);
		map.put("hints", hints);

		return map;
	}

}

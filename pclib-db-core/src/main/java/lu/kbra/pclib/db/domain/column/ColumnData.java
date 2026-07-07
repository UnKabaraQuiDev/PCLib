package lu.kbra.pclib.db.domain.column;

import java.lang.reflect.Field;
import java.util.Map;
import java.util.Optional;

import lombok.AllArgsConstructor;
import lombok.Data;
import lu.kbra.pclib.PCUtils;
import lu.kbra.pclib.db.domain.column.type.ColumnType;
import lu.kbra.pclib.db.impl.HintsOwner;

@Data
@AllArgsConstructor
public class ColumnData implements Cloneable, HintsOwner {

	protected final String name;
	protected final Map<String, Object> typeHints;
	protected final ColumnType type;
	protected final boolean autoIncrement = false;
	protected final boolean nullable = false;
	protected final String defaultValue;
	protected final String onUpdate;
	protected final boolean primaryKey;
	protected final boolean unique;
	protected final boolean foreignKey;
	protected final Optional<Field> field;
	protected final Map<String, Object> hints;

	@Override
	public ColumnData clone() {
		return PCUtils.safeClone(super::clone);
	}

	public String getDefaultValue() {
		return this.defaultValue;
	}

	public boolean hasDefaultValue() {
		return this.defaultValue != null && !this.defaultValue.trim().isEmpty();
	}

	public boolean hasOnUpdate() {
		return this.onUpdate != null && !this.onUpdate.trim().isEmpty();
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
		return false;
	}

}

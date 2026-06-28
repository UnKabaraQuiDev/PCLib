package lu.kbra.pclib.db.domain.column;

import java.lang.reflect.Field;
import java.util.Map;
import java.util.Optional;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lu.kbra.pclib.PCUtils;
import lu.kbra.pclib.db.domain.column.type.ColumnType;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ColumnData implements Cloneable {

	protected String name;
	protected Map<String, Object> typeHints;
	protected ColumnType type;
	protected boolean autoIncrement = false;
	protected boolean nullable = false;
	protected String defaultValue = null;
	protected String onUpdate = null;
	protected boolean primaryKey;
	protected boolean unique;
	protected boolean foreignKey;
	protected Optional<Field> field;

	public ColumnData(final Optional<Field> field, final String name, final Map<String, Object> typeHints, final ColumnType type) {
		this.name = name;
		this.type = type;
		this.typeHints = typeHints;
		this.field = field;
	}

	public ColumnData(
			final Optional<Field> field,
			final String name,
			final Map<String, Object> typeHints,
			final ColumnType type,
			final boolean autoIncrement,
			final boolean nullable,
			final String defaultValue,
			final String onUpdate) {
		this.field = field;
		this.name = name;
		this.typeHints = typeHints;
		this.type = type;
		this.autoIncrement = autoIncrement;
		this.nullable = nullable;
		this.defaultValue = defaultValue;
		this.onUpdate = onUpdate;
	}

	@Override
	public ColumnData clone() {
		return PCUtils.safeClone(super::clone);
	}

	public String getDefaultValue() {
		return this.defaultValue;
	}

	public <V> V getTypeHint(final String key) {
		return (V) this.typeHints.get(key);
	}

	public <V> V getTypeHint(final String key, final V default_) {
		return (V) this.typeHints.getOrDefault(key, default_);
	}

	public boolean hasDefaultValue() {
		return this.defaultValue != null && !this.defaultValue.trim().isEmpty();
	}

	public boolean hasOnUpdate() {
		return this.onUpdate != null && !this.onUpdate.trim().isEmpty();
	}

	public <V> boolean hasTypeHint(final String key) {
		return this.typeHints.containsKey(key);
	}

	public boolean isGenerated() {
		return false;
	}

}

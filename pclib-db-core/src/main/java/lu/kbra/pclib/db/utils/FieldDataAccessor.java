package lu.kbra.pclib.db.utils;

import java.lang.reflect.Field;
import java.lang.reflect.Type;

import lu.kbra.pclib.PCUtils;
import lu.kbra.pclib.db.exception.DataAccessException;
import lu.kbra.pclib.db.exception.FieldReadFailedException;
import lu.kbra.pclib.db.exception.FieldStoreFailedException;
import lu.kbra.pclib.db.impl.DatabaseEntry;
import lu.kbra.pclib.db.utils.impl.StorageBinding;

import lombok.Data;

@Data
public class FieldDataAccessor implements StorageBinding {

	private final Field field;

	public FieldDataAccessor(final Field field) {
		this.field = field;
		field.setAccessible(true);
	}

	@Override
	public Object get(final DatabaseEntry entry) throws DataAccessException {
		try {
			return this.field.get(entry);
		} catch (IllegalArgumentException | IllegalAccessException e) {
			throw new DataAccessException(new FieldReadFailedException(
					"Couldn't access field: " + this.field + " on object: " + PCUtils.toSimpleIdentityString(entry),
					e));
		}
	}

	@Override
	public void set(final DatabaseEntry entry, final Object val) throws DataAccessException {
		try {
			this.field.set(entry, val);
		} catch (IllegalArgumentException | IllegalAccessException e) {
			throw new DataAccessException(new FieldStoreFailedException(
					"Couldn't access field: " + this.field + " on object: " + PCUtils.toSimpleIdentityString(val),
					e));
		}
	}

	@Override
	public Type getGenericType() {
		return this.field.getGenericType();
	}

}

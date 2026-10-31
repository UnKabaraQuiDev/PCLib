package lu.kbra.pclib.datastructure;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public interface DeepCloneable extends Cloneable {

	static Field[] getAllFields(final Class<?> clazz) {
		final List<Field> fields = new ArrayList<>();
		Class<?> current = clazz;
		while (current != null && current != Object.class) {
			Collections.addAll(fields, current.getDeclaredFields());
			current = current.getSuperclass();
		}
		return fields.toArray(new Field[0]);
	}

	DeepCloneable clone();

	@SuppressWarnings("unchecked")
	static <K> K deepClone(final K val) {
		if (val == null) {
			return null;
		}
		if (val instanceof DeepCloneable) {
			return (K) ((DeepCloneable) val).deepClone();
		} else if (val instanceof Cloneable) {
			try {
				final Method cloneMethod = val.getClass().getMethod("clone");
				return (K) cloneMethod.invoke(val);
			} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException
					| SecurityException e) {
				throw new AssertionError(null, e);
			}
		}
		return val;
	}

	default DeepCloneable deepClone() {
		final DeepCloneable dc = this.clone();

		final Class<?> clazz = dc.getClass();
		final Field[] fields = DeepCloneable.getAllFields(clazz);

		for (final Field f : fields) {
			try {
				f.setAccessible(true);
				final Object val = f.get(dc);
				f.set(val, DeepCloneable.deepClone(val));
			} catch (AssertionError | IllegalArgumentException | IllegalAccessException e) {
				throw new RuntimeException("Field: " + f.getName(), e);
			}
		}

		return dc;
	}

}

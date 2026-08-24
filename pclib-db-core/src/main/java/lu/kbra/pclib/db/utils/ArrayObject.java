package lu.kbra.pclib.db.utils;

import java.util.Arrays;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public final class ArrayObject<T> {

	private final T[] values;

	@Override
	public boolean equals(final Object o) {
		return o instanceof ArrayObject && Arrays.equals(this.values, ((ArrayObject<?>) o).values);
	}

	@Override
	public int hashCode() {
		return Arrays.hashCode(this.values);
	}

	@Override
	public String toString() {
		return Arrays.toString(this.values);
	}

}

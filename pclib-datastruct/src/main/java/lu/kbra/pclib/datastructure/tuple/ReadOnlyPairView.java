package lu.kbra.pclib.datastructure.tuple;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Delegate;

@Getter
@RequiredArgsConstructor
public final class ReadOnlyPairView<K, V> implements Pair<K, V>, ReadOnlyTupleView {

	@Delegate
	private final Pair<K, V> parent;

	@Override
	public Pair<K, V> clone() {
		return this.parent.clone();
	}

	@Override
	public boolean isView() {
		return true;
	}

	@Override
	public boolean isMutable() {
		return false;
	}

	@Override
	public boolean equals(final Object other) {
		if (this == other) {
			return true;
		}
		return other instanceof TupleView && this.parent.equals(((TupleView) other).getParent());
	}

	@Override
	public int hashCode() {
		return this.parent.hashCode();
	}

	@Override
	public String toString() {
		return this.parent.toString();
	}
}

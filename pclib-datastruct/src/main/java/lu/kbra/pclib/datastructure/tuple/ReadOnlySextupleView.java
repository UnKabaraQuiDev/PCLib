package lu.kbra.pclib.datastructure.tuple;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Delegate;

@Getter
@RequiredArgsConstructor
public final class ReadOnlySextupleView<A, B, C, D, E, F> implements Sextuple<A, B, C, D, E, F>, ReadOnlyTupleView {

	@Delegate
	private final Sextuple<A, B, C, D, E, F> parent;

	@Override
	public Sextuple<A, B, C, D, E, F> clone() {
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

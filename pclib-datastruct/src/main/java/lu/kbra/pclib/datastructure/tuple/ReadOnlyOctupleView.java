package lu.kbra.pclib.datastructure.tuple;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Delegate;

@Getter
@RequiredArgsConstructor
public final class ReadOnlyOctupleView<A, B, C, D, E, F, G, H> implements Octuple<A, B, C, D, E, F, G, H>, ReadOnlyTupleView {

	@Delegate
	private final Octuple<A, B, C, D, E, F, G, H> parent;

	@Override
	public Octuple<A, B, C, D, E, F, G, H> clone() {
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

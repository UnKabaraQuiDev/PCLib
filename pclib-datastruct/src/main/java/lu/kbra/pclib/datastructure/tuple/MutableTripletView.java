package lu.kbra.pclib.datastructure.tuple;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Delegate;

@Getter
@RequiredArgsConstructor
public class MutableTripletView<A, B, C> implements MutableTriplet<A, B, C>, MutableTupleView {

	@Delegate
	private final MutableTriplet<A, B, C> parent;

	@Override
	public Triplet<A, B, C> clone() {
		return this.parent.clone();
	}

	@Override
	public boolean isView() {
		return true;
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

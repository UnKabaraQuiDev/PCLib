package lu.kbra.pclib.datastructure.tuple;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Delegate;

@Getter
@RequiredArgsConstructor
public class MutableSeptupleView<A, B, C, D, E, F, G> implements MutableSeptuple<A, B, C, D, E, F, G>, MutableTupleView {

	@Delegate
	private final MutableSeptuple<A, B, C, D, E, F, G> parent;

	@Override
	public Septuple<A, B, C, D, E, F, G> clone() {
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

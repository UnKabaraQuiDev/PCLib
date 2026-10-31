package lu.kbra.pclib.datastructure.tuple;

/**
 * @deprecated Use {@link MutableOctupleImpl} instead.
 */
@Deprecated
public class EditableOctupleImpl<A, B, C, D, E, F, G, H> extends MutableOctupleImpl<A, B, C, D, E, F, G, H>
		implements
			EditableOctuple<A, B, C, D, E, F, G, H> {

	@Deprecated
	public EditableOctupleImpl() {
	}

	@Deprecated
	public EditableOctupleImpl(
			final A first,
			final B second,
			final C third,
			final D fourth,
			final E fifth,
			final F sixth,
			final G seventh,
			final H eighth) {
		super(first, second, third, fourth, fifth, sixth, seventh, eighth);
	}
}

package lu.kbra.pclib.datastructure.tuple;

/**
 * @deprecated Use {@link MutableSeptupleImpl} instead.
 */
@Deprecated
public class EditableSeptupleImpl<A, B, C, D, E, F, G> extends MutableSeptupleImpl<A, B, C, D, E, F, G>
		implements
			EditableSeptuple<A, B, C, D, E, F, G> {

	@Deprecated
	public EditableSeptupleImpl() {
	}

	@Deprecated
	public EditableSeptupleImpl(
			final A first,
			final B second,
			final C third,
			final D fourth,
			final E fifth,
			final F sixth,
			final G seventh) {
		super(first, second, third, fourth, fifth, sixth, seventh);
	}
}

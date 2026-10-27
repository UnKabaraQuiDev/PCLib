package lu.kbra.pclib.datastructure.tuple;

/**
 * @deprecated Use {@link MutableSextupleImpl} instead.
 */
@Deprecated
public class EditableSextupleImpl<A, B, C, D, E, F> extends MutableSextupleImpl<A, B, C, D, E, F>
		implements
			EditableSextuple<A, B, C, D, E, F> {

	@Deprecated
	public EditableSextupleImpl() {
	}

	@Deprecated
	public EditableSextupleImpl(final A first, final B second, final C third, final D fourth, final E fifth, final F sixth) {
		super(first, second, third, fourth, fifth, sixth);
	}
}

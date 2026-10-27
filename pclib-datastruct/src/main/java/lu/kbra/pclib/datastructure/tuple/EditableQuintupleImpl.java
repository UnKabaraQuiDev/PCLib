package lu.kbra.pclib.datastructure.tuple;

/**
 * @deprecated Use {@link MutableQuintupleImpl} instead.
 */
@Deprecated
public class EditableQuintupleImpl<A, B, C, D, E> extends MutableQuintupleImpl<A, B, C, D, E> implements EditableQuintuple<A, B, C, D, E> {

	@Deprecated
	public EditableQuintupleImpl() {
	}

	@Deprecated
	public EditableQuintupleImpl(final A first, final B second, final C third, final D fourth, final E fifth) {
		super(first, second, third, fourth, fifth);
	}
}

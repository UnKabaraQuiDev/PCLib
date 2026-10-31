package lu.kbra.pclib.datastructure.tuple;

/**
 * @deprecated Use {@link MutableTripletImpl} instead.
 */
@Deprecated
public class EditableTripletImpl<A, B, C> extends MutableTripletImpl<A, B, C> implements EditableTriplet<A, B, C> {

	@Deprecated
	public EditableTripletImpl() {
	}

	@Deprecated
	public EditableTripletImpl(final A first, final B second, final C third) {
		super(first, second, third);
	}
}

package lu.kbra.pclib.datastructure.tuple;

/**
 * @deprecated Use {@link MutableQuadrupleImpl} instead.
 */
@Deprecated
public class EditableQuadrupleImpl<A, B, C, D> extends MutableQuadrupleImpl<A, B, C, D> implements EditableQuadruple<A, B, C, D> {

	@Deprecated
	public EditableQuadrupleImpl() {
	}

	@Deprecated
	public EditableQuadrupleImpl(final A first, final B second, final C third, final D fourth) {
		super(first, second, third, fourth);
	}
}

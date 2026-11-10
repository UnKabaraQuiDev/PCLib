package lu.kbra.pclib.geo.result;

/** The empty alternative of a valid, resolved geometric operation. */
public final class Empty {

	public static final Empty INSTANCE = new Empty();

	private Empty() {
	}

	@Override
	public String toString() {
		return "Empty";
	}

}

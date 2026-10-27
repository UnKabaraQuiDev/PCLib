package lu.kbra.pclib.datastructure.tuple;

/**
 * @deprecated Use {@link MutablePairImpl} instead.
 */
@Deprecated
public class EditablePairImpl<K, V> extends MutablePairImpl<K, V> implements EditablePair<K, V> {

	@Deprecated
	public EditablePairImpl() {
	}

	@Deprecated
	public EditablePairImpl(final K key, final V value) {
		super(key, value);
	}
}

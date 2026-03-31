package lu.kbra.jbcodec.encoder;

import java.nio.ByteBuffer;

import lu.kbra.pclib.datastructure.pair.Pair;
import lu.kbra.pclib.datastructure.pair.ReadOnlyPair;

public class PairEncoder extends DefaultObjectEncoder<Pair<?, ?>> {

	public PairEncoder() {
		super(Pair.class);
	}

	@Override
	public ByteBuffer encode(final boolean head, final Pair<?, ?> obj) {
		final int length = this.estimateSize(head, obj);
		final ByteBuffer bb = ByteBuffer.allocate(length);
		super.putHeader(head, bb);

		bb.put(this.cm.encode(false, obj instanceof ReadOnlyPair));

		bb.put(this.cm.encode(true, obj.getKey()));
		bb.put(this.cm.encode(true, obj.getValue()));

		bb.flip();

		return bb;
	}

	@Override
	public int estimateSize(final boolean head, final Pair<?, ?> obj) {
		return super.estimateHeaderSize(head) + this.cm.estimateSize(false, obj instanceof ReadOnlyPair)
				+ this.cm.estimateSize(true, obj.getKey()) + this.cm.estimateSize(true, obj.getValue());
	}

	@Override
	public boolean confirmClassType(final Class<?> clazz) {
		return Pair.class.isAssignableFrom(clazz);
	}

	@Override
	public boolean confirmType(final Object obj) {
		return obj instanceof Pair;
	}
}

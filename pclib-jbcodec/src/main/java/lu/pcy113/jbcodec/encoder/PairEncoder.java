package lu.pcy113.jbcodec.encoder;

import java.nio.ByteBuffer;

import lu.pcy113.pclib.datastructure.pair.Pair;
import lu.pcy113.pclib.datastructure.pair.ReadOnlyPair;

public class PairEncoder extends DefaultObjectEncoder<Pair<?, ?>> {

	public PairEncoder() {
		super(Pair.class);
	}

	@Override
	public ByteBuffer encode(boolean head, Pair<?, ?> obj) {
		final int length = estimateSize(head, obj);
		ByteBuffer bb = ByteBuffer.allocate(length);
		super.putHeader(head, bb);

		bb.put(cm.encode(false, obj instanceof ReadOnlyPair));
		
		bb.put(cm.encode(true, obj.getKey()));
		bb.put(cm.encode(true, obj.getValue()));

		bb.flip();

		return bb;
	}

	@Override
	public int estimateSize(boolean head, Pair<?, ?> obj) {
		return super.estimateHeaderSize(head) + cm.estimateSize(false, obj instanceof ReadOnlyPair) + cm.estimateSize(true, obj.getKey()) + cm.estimateSize(true, obj.getValue());
	}
	
	@Override
	public boolean confirmClassType(Class<?> clazz) {
		return Pair.class.isAssignableFrom(clazz);
	}
	
	@Override
	public boolean confirmType(Object obj) {
		return obj instanceof Pair;
	}
}

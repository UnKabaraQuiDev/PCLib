package lu.kbra.jbcodec.encoder;

import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map.Entry;

public class MultiHashMapEncoder extends DefaultObjectEncoder<HashMap<Object, Object>> {

	public MultiHashMapEncoder() {
		super(HashMap.class);
	}

	@Override
	public ByteBuffer encode(final boolean head, final HashMap<Object, Object> obj) {
		final int length = this.estimateSize(head, obj);
		final ByteBuffer bb = ByteBuffer.allocate(length);
		super.putHeader(head, bb);

		bb.putInt(obj.size());

		for (final Entry<?, ?> o : obj.entrySet()) {
			final Object key = o.getKey();
			final Object value = o.getValue();

			bb.put(this.cm.encode(key));
			bb.put(this.cm.encode(value));
		}

		bb.flip();
		return bb;
	}

	@Override
	public int estimateSize(final boolean head, final HashMap<Object, Object> obj) {
		int length = super.estimateHeaderSize(head) + Integer.BYTES;
		for (final Entry<?, ?> o : obj.entrySet()) {
			length += this.cm.estimateSize(true, o.getKey());
			length += this.cm.estimateSize(true, o.getValue());
		}
		return length;
	}

}

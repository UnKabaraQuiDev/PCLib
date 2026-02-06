package lu.pcy113.jbcodec.encoder;

import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map.Entry;

public class MultiHashMapEncoder extends DefaultObjectEncoder<HashMap<Object, Object>> {

	public MultiHashMapEncoder() {
		super(HashMap.class);
	}

	@Override
	public ByteBuffer encode(boolean head, HashMap<Object, Object> obj) {
		final int length = estimateSize(head, obj);
		final ByteBuffer bb = ByteBuffer.allocate(length);
		super.putHeader(head, bb);

		bb.putInt(obj.size());

		for (Entry<?, ?> o : obj.entrySet()) {
			Object key = o.getKey();
			Object value = o.getValue();

			bb.put(cm.encode(key));
			bb.put(cm.encode(value));
		}

		bb.flip();
		return bb;
	}

	@Override
	public int estimateSize(boolean head, HashMap<Object, Object> obj) {
		int length = super.estimateHeaderSize(head) + Integer.BYTES;
		for (Entry<?, ?> o : obj.entrySet()) {
			length += cm.estimateSize(true, o.getKey());
			length += cm.estimateSize(true, o.getValue());
		}
		return length;
	}

}

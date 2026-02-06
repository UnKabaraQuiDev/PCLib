package lu.pcy113.jbcodec.encoder;

import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map.Entry;

import lu.pcy113.jbcodec.CodecManager;

public class SingleHashMapEncoder extends DefaultObjectEncoder<HashMap<Object, Object>> {

	public SingleHashMapEncoder() {
		super(HashMap.class);
	}

	@Override
	public ByteBuffer encode(boolean head, HashMap<Object, Object> obj) {
		final int length = estimateSize(head, obj);
		final ByteBuffer bb = ByteBuffer.allocate(length);
		super.putHeader(head, bb);

		bb.putInt(obj.size());

		if (obj.size() != 0) {
			final Encoder<Object> keyEncoder = cm.getEncoderByObject(obj.keySet().iterator().next());
			final Encoder<Object> valueEncoder = cm.getEncoderByObject(obj.values().iterator().next());

			bb.putShort(keyEncoder.header());
			bb.putShort(valueEncoder.header());

			for (Entry<?, ?> o : obj.entrySet()) {
				Object key = o.getKey();
				Object value = o.getValue();

				bb.put(keyEncoder.encode(false, key));
				bb.put(valueEncoder.encode(false, value));
			}
		}

		bb.flip();
		return bb;
	}

	@Override
	public int estimateSize(boolean head, HashMap<Object, Object> obj) {
		int length = super.estimateHeaderSize(head) + Integer.BYTES + (obj.size() > 0 ? 2 * CodecManager.HEAD_SIZE : 0);
		for (Entry<?, ?> o : obj.entrySet()) {
			length += cm.estimateSize(false, o.getKey());
			length += cm.estimateSize(false, o.getValue());
		}
		return length;
	}

}

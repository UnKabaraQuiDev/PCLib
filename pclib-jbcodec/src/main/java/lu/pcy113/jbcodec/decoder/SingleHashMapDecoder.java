package lu.pcy113.jbcodec.decoder;

import java.nio.ByteBuffer;
import java.util.HashMap;

public class SingleHashMapDecoder extends DefaultObjectDecoder<HashMap<Object, Object>> {

	public SingleHashMapDecoder() {
		super(HashMap.class);
	}

	@Override
	public HashMap<Object, Object> decode(boolean head, ByteBuffer bb) {
		verifyHeader(head, bb);

		final int length = bb.getInt();

		final HashMap<Object, Object> map = new HashMap<>();

		if (length > 0) {
			final short keyHeader = bb.getShort();
			final short valueHeader = bb.getShort();

			final Decoder<?> keyDecoder = cm.getDecoder(keyHeader);
			final Decoder<?> valueDecoder = cm.getDecoder(valueHeader);

			for (int i = 0; i < length; i++) {
				Object k = keyDecoder.decode(false, bb);
				Object v = valueDecoder.decode(false, bb);
				
				map.put(k, v);
			}
		}

		return map;
	}

}
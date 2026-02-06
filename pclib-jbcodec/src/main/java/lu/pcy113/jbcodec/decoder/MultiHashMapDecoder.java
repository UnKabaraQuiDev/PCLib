package lu.pcy113.jbcodec.decoder;

import java.nio.ByteBuffer;
import java.util.HashMap;

public class MultiHashMapDecoder extends DefaultObjectDecoder<HashMap<Object, Object>> {

	public MultiHashMapDecoder() {
		super(HashMap.class);
	}

	@Override
	public HashMap<Object, Object> decode(boolean head, ByteBuffer bb) {
		verifyHeader(head, bb);

		final int length = bb.getInt();

		final HashMap<Object, Object> map = new HashMap<>();

		for (int i = 0; i < length; i++) {
			Object k = cm.decode(bb);
			Object v = cm.decode(bb);
			
			map.put(k, v);
		}

		return map;
	}

}
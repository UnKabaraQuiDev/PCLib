package lu.kbra.jbcodec.decoder;

import java.nio.ByteBuffer;
import java.util.HashMap;

public class MultiHashMapDecoder extends DefaultObjectDecoder<HashMap<Object, Object>> {

	public MultiHashMapDecoder() {
		super(HashMap.class);
	}

	@Override
	public HashMap<Object, Object> decode(final boolean head, final ByteBuffer bb) {
		this.verifyHeader(head, bb);

		final int length = bb.getInt();

		final HashMap<Object, Object> map = new HashMap<>();

		for (int i = 0; i < length; i++) {
			final Object k = this.cm.decode(bb);
			final Object v = this.cm.decode(bb);

			map.put(k, v);
		}

		return map;
	}

}

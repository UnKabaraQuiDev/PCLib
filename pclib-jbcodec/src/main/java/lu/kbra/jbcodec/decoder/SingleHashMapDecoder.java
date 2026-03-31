package lu.kbra.jbcodec.decoder;

import java.nio.ByteBuffer;
import java.util.HashMap;

public class SingleHashMapDecoder extends DefaultObjectDecoder<HashMap<Object, Object>> {

	public SingleHashMapDecoder() {
		super(HashMap.class);
	}

	@Override
	public HashMap<Object, Object> decode(final boolean head, final ByteBuffer bb) {
		this.verifyHeader(head, bb);

		final int length = bb.getInt();

		final HashMap<Object, Object> map = new HashMap<>();

		if (length > 0) {
			final short keyHeader = bb.getShort();
			final short valueHeader = bb.getShort();

			final Decoder<?> keyDecoder = this.cm.getDecoder(keyHeader);
			final Decoder<?> valueDecoder = this.cm.getDecoder(valueHeader);

			for (int i = 0; i < length; i++) {
				final Object k = keyDecoder.decode(false, bb);
				final Object v = valueDecoder.decode(false, bb);

				map.put(k, v);
			}
		}

		return map;
	}

}

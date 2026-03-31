package lu.kbra.jbcodec.encoder;

import java.nio.ByteBuffer;
import java.util.ArrayList;

import lu.kbra.jbcodec.CodecManager;

public class SingleArrayListEncoder extends DefaultObjectEncoder<ArrayList<?>> {

	public SingleArrayListEncoder() {
		super(ArrayList.class);
	}

	/**
	 * ( HEAD 2b - SIZE 4b - SUB HEAD 2b - DATA xb
	 */
	@Override
	public ByteBuffer encode(final boolean head, final ArrayList<?> obj) {
		final ByteBuffer bb = ByteBuffer.allocate(this.estimateSize(head, obj));

		super.putHeader(head, bb);

		bb.putInt(obj.size());

		if (obj.size() != 0) {
			final Encoder<?> encoder = this.cm.getEncoderByObject(obj.get(0));

			bb.putShort(encoder.header());

			for (final Object o : obj) {
				bb.put(this.cm.encode(false, o));
			}
		}

		bb.flip();
		return bb;
	}

	@Override
	public int estimateSize(final boolean head, final ArrayList<?> obj) {
		return super.estimateHeaderSize(head) + 4 + (obj.size() > 0 ? CodecManager.HEAD_SIZE : 0)
				+ obj.stream().mapToInt(c -> this.cm.estimateSize(false, c)).sum();
	}

	@Override
	public boolean confirmClassType(final Class<?> clazz) {
		return ArrayList.class.isAssignableFrom(clazz);
	}

	@Override
	public boolean confirmType(final Object obj) {
		return obj instanceof ArrayList;
	}

}

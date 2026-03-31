package lu.kbra.jbcodec.encoder;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

public class MultiArrayListEncoder extends DefaultObjectEncoder<ArrayList<?>> {

	public MultiArrayListEncoder() {
		super(ArrayList.class);
	}

	/**
	 * ( HEAD 2b - SIZE 4b - SUB HEAD 2b - DATA xb
	 */
	@Override
	public ByteBuffer encode(final boolean head, final ArrayList<?> obj) {
		final List<ByteBuffer> elements = new ArrayList<>();
		for (final Object o : obj) {
			elements.add(this.cm.encode(true, o));
		}

		final ByteBuffer bb = ByteBuffer.allocate(this.estimateSize(head, obj));

		super.putHeader(head, bb);

		bb.putInt(obj.size());

		for (final ByteBuffer bbb : elements) {
			bb.put(bbb);
		}

		bb.flip();
		return bb;
	}

	@Override
	public int estimateSize(final boolean head, final ArrayList<?> obj) {
		return super.estimateHeaderSize(head) + 4 + obj.stream().mapToInt(c -> this.cm.estimateSize(true, c)).sum();
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

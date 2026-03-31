package lu.kbra.jbcodec.decoder;

import java.nio.ByteBuffer;
import java.util.ArrayList;

public class SingleArrayListDecoder extends DefaultObjectDecoder<ArrayList<?>> {

	public SingleArrayListDecoder() {
		super(ArrayList.class);
	}

	@Override
	public ArrayList<Object> decode(final boolean head, final ByteBuffer bb) {
		this.verifyHeader(head, bb);

		final int length = bb.getInt();
		final ArrayList<Object> array = new ArrayList<>();

		if (length > 0) {
			final Decoder<?> decoder = this.cm.getDecoder(bb.getShort());

			for (int i = 0; i < length; i++) {
				array.add(decoder.decode(false, bb));
			}
		}

		return array;
	}

}

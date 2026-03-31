package lu.kbra.jbcodec.decoder;

import java.nio.ByteBuffer;
import java.util.ArrayList;

public class MultiArrayListDecoder extends DefaultObjectDecoder<ArrayList<?>> {

	public MultiArrayListDecoder() {
		super(ArrayList.class);
	}

	@Override
	public ArrayList<Object> decode(final boolean head, final ByteBuffer bb) {
		this.verifyHeader(head, bb);

		final int length = bb.getInt();

		final ArrayList<Object> array = new ArrayList<>();
		for (int i = 0; i < length; i++) {
			array.add(this.cm.decode(bb));
		}
		return array;
	}

}

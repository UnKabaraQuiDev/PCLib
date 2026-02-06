package lu.pcy113.jbcodec.decoder;

import java.nio.ByteBuffer;
import java.util.ArrayList;

public class SingleArrayListDecoder extends DefaultObjectDecoder<ArrayList<?>> {

	public SingleArrayListDecoder() {
		super(ArrayList.class);
	}

	@Override
	public ArrayList<Object> decode(boolean head, ByteBuffer bb) {
		verifyHeader(head, bb);

		int length = bb.getInt();
		ArrayList<Object> array = new ArrayList<>();

		if (length > 0) {
			Decoder<?> decoder = cm.getDecoder(bb.getShort());

			for (int i = 0; i < length; i++) {
				array.add(decoder.decode(false, bb));
			}
		}

		return array;
	}

}

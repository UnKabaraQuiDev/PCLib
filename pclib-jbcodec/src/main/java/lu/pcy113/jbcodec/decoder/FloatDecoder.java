package lu.pcy113.jbcodec.decoder;

import java.nio.ByteBuffer;

public class FloatDecoder extends DefaultObjectDecoder<Float> {

	@Override
	public Float decode(boolean head, ByteBuffer bb) {
		verifyHeader(head, bb);

		return bb.getFloat();
	}

}

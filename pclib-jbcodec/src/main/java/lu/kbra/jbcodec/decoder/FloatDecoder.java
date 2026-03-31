package lu.kbra.jbcodec.decoder;

import java.nio.ByteBuffer;

public class FloatDecoder extends DefaultObjectDecoder<Float> {

	@Override
	public Float decode(final boolean head, final ByteBuffer bb) {
		this.verifyHeader(head, bb);

		return bb.getFloat();
	}

}

package lu.kbra.jbcodec.decoder;

import java.nio.ByteBuffer;

public class DoubleDecoder extends DefaultObjectDecoder<Double> {

	@Override
	public Double decode(final boolean head, final ByteBuffer bb) {
		this.verifyHeader(head, bb);

		return bb.getDouble();
	}

}

package lu.pcy113.jbcodec.decoder;

import java.nio.ByteBuffer;

public class DoubleDecoder extends DefaultObjectDecoder<Double> {

	@Override
	public Double decode(boolean head, ByteBuffer bb) {
		verifyHeader(head, bb);

		return bb.getDouble();
	}

}

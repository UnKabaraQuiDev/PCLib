package lu.pcy113.jbcodec.decoder;

import java.nio.ByteBuffer;

public class BooleanDecoder extends DefaultObjectDecoder<Boolean> {

	@Override
	public Boolean decode(boolean head, ByteBuffer bb) {
		verifyHeader(head, bb);

		return bb.get() == 0 ? false : true;
	}

}

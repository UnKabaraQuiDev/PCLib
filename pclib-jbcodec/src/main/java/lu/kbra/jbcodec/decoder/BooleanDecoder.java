package lu.kbra.jbcodec.decoder;

import java.nio.ByteBuffer;

public class BooleanDecoder extends DefaultObjectDecoder<Boolean> {

	@Override
	public Boolean decode(final boolean head, final ByteBuffer bb) {
		this.verifyHeader(head, bb);

		return (bb.get() == 0) == false;
	}

}

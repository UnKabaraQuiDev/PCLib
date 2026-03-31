package lu.kbra.jbcodec.decoder;

import java.nio.ByteBuffer;

public class CharacterDecoder extends DefaultObjectDecoder<Character> {

	@Override
	public Character decode(final boolean head, final ByteBuffer bb) {
		this.verifyHeader(head, bb);

		return bb.getChar();
	}

}

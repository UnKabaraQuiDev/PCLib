package lu.kbra.p4j.crypto.encryptor;

import java.nio.ByteBuffer;

public class RawEncryptor implements Encryptor {

	@Override
	public ByteBuffer encrypt(final ByteBuffer in) {
		return in;
	}

}

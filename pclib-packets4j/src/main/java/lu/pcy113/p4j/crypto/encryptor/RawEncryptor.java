package lu.pcy113.p4j.crypto.encryptor;

import java.nio.ByteBuffer;

public class RawEncryptor implements Encryptor {

	@Override
	public ByteBuffer encrypt(ByteBuffer in) {
		return in;
	}

}
package lu.kbra.p4j.crypto.decryptor;

import java.nio.ByteBuffer;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;

import lu.kbra.pclib.PCUtils;

public class AESDecryptor implements Decryptor {
	private static final String ALGORITHM = "AES";

	private SecretKeySpec secretKey;
	private Cipher cipher;

	public AESDecryptor(final byte[] key) throws NoSuchPaddingException, InvalidKeyException {
		try {
			this.secretKey = new SecretKeySpec(key, AESDecryptor.ALGORITHM);
			this.cipher = Cipher.getInstance(AESDecryptor.ALGORITHM);
		} catch (final NoSuchAlgorithmException e) {
			/* probably impossible */
		}
	}

	@Override
	public ByteBuffer decrypt(final ByteBuffer buffer) throws Exception {
		this.cipher.init(Cipher.DECRYPT_MODE, this.secretKey);
		final byte[] decryptedBytes = this.cipher.doFinal(PCUtils.toByteArray(buffer), buffer.position(), buffer.remaining());
		return ByteBuffer.wrap(decryptedBytes);
	}
}

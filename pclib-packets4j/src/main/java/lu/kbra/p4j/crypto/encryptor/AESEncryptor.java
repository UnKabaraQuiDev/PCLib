package lu.kbra.p4j.crypto.encryptor;

import java.nio.ByteBuffer;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;

import lu.kbra.pclib.PCUtils;

public class AESEncryptor implements Encryptor {
	private static final String ALGORITHM = "AES";

	private SecretKeySpec secretKey;
	private Cipher cipher;

	public AESEncryptor(final byte[] key) throws NoSuchPaddingException, InvalidKeyException {
		try {
			this.secretKey = new SecretKeySpec(key, AESEncryptor.ALGORITHM);
			this.cipher = Cipher.getInstance(AESEncryptor.ALGORITHM);
		} catch (final NoSuchAlgorithmException e) {
			/* probably impossible */
		}
	}

	@Override
	public ByteBuffer encrypt(final ByteBuffer buffer) throws Exception {
		this.cipher.init(Cipher.ENCRYPT_MODE, this.secretKey);
		final byte[] encryptedBytes = this.cipher.doFinal(PCUtils.toByteArray(buffer), buffer.position(), buffer.remaining());
		return ByteBuffer.wrap(encryptedBytes);
	}

}

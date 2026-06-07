package lu.kbra.p4j.crypto;

import java.nio.ByteBuffer;
import java.security.InvalidKeyException;

import javax.crypto.NoSuchPaddingException;

import lu.kbra.p4j.crypto.decryptor.AESDecryptor;
import lu.kbra.p4j.crypto.decryptor.Decryptor;
import lu.kbra.p4j.crypto.decryptor.RawDecryptor;
import lu.kbra.p4j.crypto.encryptor.AESEncryptor;
import lu.kbra.p4j.crypto.encryptor.Encryptor;
import lu.kbra.p4j.crypto.encryptor.RawEncryptor;

public class EncryptionManager {

	public static final EncryptionManager aes(final byte[] key) throws InvalidKeyException, NoSuchPaddingException {
		return new EncryptionManager(new AESEncryptor(key), new AESDecryptor(key));
	}

	public static final EncryptionManager raw() {
		return new EncryptionManager(new RawEncryptor(), new RawDecryptor());
	}

	private Encryptor encryptor;

	private Decryptor decryptor;

	public EncryptionManager(final Encryptor e, final Decryptor d) {
		this.encryptor = e;
		this.decryptor = d;
	}

	public ByteBuffer decrypt(final ByteBuffer b) throws Exception {
		return this.decryptor.decrypt(b);
	}

	public ByteBuffer encrypt(final ByteBuffer b) throws Exception {
		return this.encryptor.encrypt(b);
	}

	public Decryptor getDecryptor() {
		return this.decryptor;
	}

	public Encryptor getEncryptor() {
		return this.encryptor;
	}

	public void setDecryptor(final Decryptor decryptor) {
		this.decryptor = decryptor;
	}

	public void setEncryptor(final Encryptor encryptor) {
		this.encryptor = encryptor;
	}

}

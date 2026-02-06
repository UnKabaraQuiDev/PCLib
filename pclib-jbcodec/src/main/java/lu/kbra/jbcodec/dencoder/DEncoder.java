package lu.kbra.jbcodec.dencoder;

import lu.kbra.jbcodec.CodecManager;

public interface DEncoder {
	
	short header();

	CodecManager codecManager();

	Class<?> type();

	String register(CodecManager cm, short header);

	default void verifyRegister() {
		if (codecManager() != null)
			throw new IllegalArgumentException("Cannot register D/Encoder to more than one CodecManager.");
	}
	
}

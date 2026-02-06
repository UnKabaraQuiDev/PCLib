package lu.pcy113.jbcodec.decoder;

import java.nio.ByteBuffer;

public class ClassDecoder extends DefaultObjectDecoder<Class<?>> {

	public ClassDecoder() {
		super(Class.class);
	}
	
	@Override
	public Class<?> decode(boolean head, ByteBuffer bb) {
		verifyHeader(head, bb);

		try {
			return Class.forName(cm.getDecoderByClass(String.class).decode(false, bb));
		} catch (ClassNotFoundException e) {
			throw new RuntimeException(e);
		}
	}

}

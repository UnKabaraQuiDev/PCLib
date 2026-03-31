package lu.kbra.jbcodec.decoder;

import java.nio.ByteBuffer;

public class ClassDecoder extends DefaultObjectDecoder<Class<?>> {

	public ClassDecoder() {
		super(Class.class);
	}

	@Override
	public Class<?> decode(final boolean head, final ByteBuffer bb) {
		this.verifyHeader(head, bb);

		try {
			return Class.forName(this.cm.getDecoderByClass(String.class).decode(false, bb));
		} catch (final ClassNotFoundException e) {
			throw new RuntimeException(e);
		}
	}

}

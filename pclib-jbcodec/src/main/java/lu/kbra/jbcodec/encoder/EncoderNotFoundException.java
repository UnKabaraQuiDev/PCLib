package lu.kbra.jbcodec.encoder;

public class EncoderNotFoundException extends RuntimeException {

	private static final long serialVersionUID = -8247074037198156744L;

	public EncoderNotFoundException(final String msg) {
		super(msg);
	}

	public EncoderNotFoundException(final Exception e, final String string) {
		super(string, e);
	}

}

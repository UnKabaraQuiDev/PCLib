package lu.kbra.jbcodec.decoder;

public class DecoderNotFoundException extends RuntimeException {

	private static final long serialVersionUID = -2385503372171750043L;

	public DecoderNotFoundException() {
	}

	public DecoderNotFoundException(final short header) {
		super("No decoder found for header: " + header);
	}

}

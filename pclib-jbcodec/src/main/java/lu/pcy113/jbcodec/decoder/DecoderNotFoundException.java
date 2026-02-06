package lu.pcy113.jbcodec.decoder;

public class DecoderNotFoundException extends RuntimeException {

	public DecoderNotFoundException() {
	}

	public DecoderNotFoundException(short header) {
		super("No decoder found for header: " + header);
	}

}

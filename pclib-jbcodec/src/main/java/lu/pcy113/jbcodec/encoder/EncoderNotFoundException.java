package lu.pcy113.jbcodec.encoder;

public class EncoderNotFoundException extends RuntimeException {

	public EncoderNotFoundException(String msg) {
		super(msg);
	}

	public EncoderNotFoundException(Exception e, String string) {
		super(string, e);
	}

}

package lu.pcy113.jbcodec.encoder;

import java.nio.charset.Charset;

public class PlatformStringEncoder extends StringEncoder {

	public PlatformStringEncoder() {
		super(Charset.defaultCharset());
	}

}

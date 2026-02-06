package lu.pcy113.pclib.awt;

import java.awt.Composite;
import java.awt.CompositeContext;
import java.awt.RenderingHints;
import java.awt.image.ColorModel;

public class BrightnessComposite implements Composite {

	@Override
	public CompositeContext createContext(ColorModel srcColorModel, ColorModel dstColorModel, RenderingHints hints) {
		return new BrightnessCompositeContext();
	}

}

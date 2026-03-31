package lu.kbra.pclib.awt;

import java.awt.Composite;
import java.awt.CompositeContext;
import java.awt.RenderingHints;
import java.awt.image.ColorModel;

public class BrightnessComposite implements Composite {

	@Override
	public CompositeContext createContext(final ColorModel srcColorModel, final ColorModel dstColorModel, final RenderingHints hints) {
		return new BrightnessCompositeContext();
	}

}

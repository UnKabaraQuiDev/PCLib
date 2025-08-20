import java.awt.Color;

import org.junit.jupiter.api.Test;

import lu.pcy113.pclib.PCUtils;

public class ColorMain {

	@Test
	public void maxContrast() {
		final Color max = PCUtils.maxContrast(Color.BLACK, new Color[] { Color.GRAY, Color.WHITE, Color.DARK_GRAY, Color.BLACK });
		assert max == Color.WHITE : "Found: " + max;
	}

}

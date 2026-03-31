package lu.kbra.pclib.awt;

import java.awt.CompositeContext;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;

public class BrightnessCompositeContext implements CompositeContext {

	@Override
	public void compose(final Raster src, final Raster dstIn, final WritableRaster dstOut) {
		final int[] srcPixels = src.getPixels(0, 0, src.getWidth(), src.getHeight(), (int[]) null);
		final int[] dstPixels = dstIn.getPixels(0, 0, dstIn.getWidth(), dstIn.getHeight(), (int[]) null);

		final int minLength = Math.min(srcPixels.length, dstPixels.length);

		for (int i = 0; i < minLength; i += 4) {
			final int srcR = srcPixels[i];
			final int srcG = srcPixels[i + 1];
			final int srcB = srcPixels[i + 2];
			final int srcA = srcPixels[i + 3];

			final int dstR = dstPixels[i];
			final int dstG = dstPixels[i + 1];
			final int dstB = dstPixels[i + 2];

			final float srcBrightness = this.calculateBrightness(srcR, srcG, srcB);
			final float dstBrightness = this.calculateBrightness(dstR, dstG, dstB);

			if (srcBrightness > dstBrightness) {
				dstPixels[i] = srcR;
				dstPixels[i + 1] = srcG;
				dstPixels[i + 2] = srcB;
				dstPixels[i + 3] = srcA;
			} else {
				dstPixels[i] = dstR;
				dstPixels[i + 1] = dstG;
				dstPixels[i + 2] = dstB;
				dstPixels[i + 3] = 255;
			}
		}

		dstOut.setPixels(0, 0, dstOut.getWidth(), dstOut.getHeight(), dstPixels);
	}

	private float calculateBrightness(final int r, final int g, final int b) {
		return 0.299f * r + 0.587f * g + 0.114f * b;
	}

	@Override
	public void dispose() {
		// No resources to release
	}

}

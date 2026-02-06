package lu.kbra.pclib.awt;

import java.awt.CompositeContext;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;

public class BrightnessCompositeContext implements CompositeContext {

	@Override
	public void compose(Raster src, Raster dstIn, WritableRaster dstOut) {
		int[] srcPixels = src.getPixels(0, 0, src.getWidth(), src.getHeight(), (int[]) null);
		int[] dstPixels = dstIn.getPixels(0, 0, dstIn.getWidth(), dstIn.getHeight(), (int[]) null);

		final int minLength = Math.min(srcPixels.length, dstPixels.length);

		for (int i = 0; i < minLength; i += 4) {
			int srcR = srcPixels[i];
			int srcG = srcPixels[i + 1];
			int srcB = srcPixels[i + 2];
			int srcA = srcPixels[i + 3];

			int dstR = dstPixels[i];
			int dstG = dstPixels[i + 1];
			int dstB = dstPixels[i + 2];

			float srcBrightness = calculateBrightness(srcR, srcG, srcB);
			float dstBrightness = calculateBrightness(dstR, dstG, dstB);

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

	private float calculateBrightness(int r, int g, int b) {
		return 0.299f * r + 0.587f * g + 0.114f * b;
	}

	@Override
	public void dispose() {
		// No resources to release
	}

}
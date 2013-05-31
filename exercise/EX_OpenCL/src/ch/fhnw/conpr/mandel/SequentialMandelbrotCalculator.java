package ch.fhnw.conpr.mandel;

import ch.fhnw.conpr.mandel.AparapiMandelbrotCalculator.MandelbrotKernel;

public class SequentialMandelbrotCalculator implements MandelbrotCalculator {

	@Override
	public int[] calculateMandelbrotIterations(int width, int height) {
		final float RE_PER_PIXEL = (MandelbrotKernel.RE_MAX - MandelbrotKernel.RE_MIN)/ width;
		final float IM_PER_PIXEL = (MandelbrotKernel.IM_MAX - MandelbrotKernel.IM_MIN)/ height;
		int[] iterations = new int[width * height];
		for (int x = 0; x < width; x++) { // x-axis
			for (int y = 0; y < height; y++) { // y-axis
				float re = MandelbrotKernel.RE_MIN + x * RE_PER_PIXEL; // map pixel to complex plane
				float im = MandelbrotKernel.IM_MIN + y * IM_PER_PIXEL; // map pixel to complex plane
				iterations[x*width + y] = MandelbrotKernel.mandel(re, im);
			}
		}
		return iterations;
	}

	@Override
	public String toString() {
		return getClass().getSimpleName();
	}
}

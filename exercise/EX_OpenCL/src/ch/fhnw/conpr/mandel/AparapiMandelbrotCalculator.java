package ch.fhnw.conpr.mandel;

import com.amd.aparapi.Kernel;
import com.amd.aparapi.Range;

public class AparapiMandelbrotCalculator implements MandelbrotCalculator {

	public static class MandelbrotKernel extends Kernel {
		public static final int MAX_ITERATIONS = 255;

		public static final double RE_MIN = -2;
		public static final double RE_MAX = 2;
		public static final double IM_MIN = -2;
		public static final double IM_MAX = 2;
		private final int[] rgb;
		
		public MandelbrotKernel(int[] rgb) {
			this.rgb = rgb;
		}
		
		@Override
		public void run() {
			// TODO Implement Opencl Mandelbrot kernel
		}
		
		/**
		 * z_n+1 = z_n^2 + c starting with z_0 = 0
		 * 
		 * Checks whether c = re + i*im is a member of the Mandelbrot set.
		 * 
		 * @param re
		 *            real part
		 * @param im
		 *            imaginary part
		 * @return the number of iterations
		 */
		public int mandel(double cre, double cim) {
			double re = 0.0;
			double im = 0.0;
			int iterations = 0;
			while (re * re + im * im <= 4 && iterations < MAX_ITERATIONS) {
				double re1 = re * re - im * im + cre;
				double im1 = 2 * re * im + cim;
				re = re1;
				im = im1;
				iterations++;
			}
			return iterations;
		}
		
		public int[] getInterations() {
			return rgb;
		}
	}
	
	@Override
	public int[] calculateMandelbrotIterations(int width, int height) {
		MandelbrotKernel kernel = new MandelbrotKernel(new int[width * height]);
		
		kernel.execute(Range.create2D(width, height));
		
		return kernel.getInterations();
	}
}

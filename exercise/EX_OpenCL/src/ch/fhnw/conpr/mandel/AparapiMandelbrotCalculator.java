package ch.fhnw.conpr.mandel;

import com.amd.aparapi.Device;
import com.amd.aparapi.Kernel;
import com.amd.aparapi.Range;

public class AparapiMandelbrotCalculator implements MandelbrotCalculator {

	public static class MandelbrotKernel extends Kernel {
		public static final int MAX_ITERATIONS = 255;

		public static final float RE_MIN = -2;
		public static final float RE_MAX = 2;
		public static final float IM_MIN = -2;
		public static final float IM_MAX = 2;
		private final int[] rgb;
		private final int width;
		private final int height;
		private final float RE_PER_PIXEL;
		private final float IM_PER_PIXEL;

		public MandelbrotKernel(int width, int height) {
			this.rgb = new int[width * height];
			RE_PER_PIXEL = (MandelbrotKernel.RE_MAX - MandelbrotKernel.RE_MIN)/ width;
			IM_PER_PIXEL = (MandelbrotKernel.IM_MAX - MandelbrotKernel.IM_MIN)/ height;
			this.width = width;
			this.height = height;
		}
		
		@Override
		public void run() {
			int x = getGlobalId(0);
			int y = getGlobalId(1);
			
			if(x > width || y > height) return;
			
			float re = MandelbrotKernel.RE_MIN + x * RE_PER_PIXEL; // map pixel to complex plane
			float im = MandelbrotKernel.IM_MIN + y * IM_PER_PIXEL; // map pixel to complex plane
			
			rgb[x*width + y] = MandelbrotKernel.mandel(re, im);
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
		public static int mandel(float cre, float cim) {
			float re = 0.0f;
			float im = 0.0f;
			int iterations = 0;
			while (re * re + im * im <= 4 && iterations < MAX_ITERATIONS) {
				float re1 = re * re - im * im + cre;
				float im1 = 2 * re * im + cim;
				re = re1;
				im = im1;
				iterations++;
			}
			return iterations;
		}
		
		public int[] getIterations() {
			return rgb;
		}
	}
	
	private final Device device;
	private final String name;
	
	public AparapiMandelbrotCalculator() {
		this("Aprapi on " + Device.best().getType(), Device.best());
	}
	
	public AparapiMandelbrotCalculator(String name, Device device) {
		this.device = device;
		this.name = name;
	}
	
	@Override
	public int[] calculateMandelbrotIterations(int width, int height) {
		MandelbrotKernel kernel = new MandelbrotKernel(width, height);
		
		kernel.execute(Range.create2D(device, width, height, 1, device.getMaxWorkGroupSize()));
		kernel.dispose();
		
		return kernel.getIterations();
	}
	
	@Override
	public String toString() {
		return name;
	}
}

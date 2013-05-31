package ch.fhnw.conpr.mandel;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import ch.fhnw.conpr.mandel.AparapiMandelbrotCalculator.MandelbrotKernel;

public class ParMandelbrotCalculator implements MandelbrotCalculator {

	private static final int N_THREADS = Runtime.getRuntime().availableProcessors();
	private static final int N_TASKS = N_THREADS * 10;
	
	@Override
	public int[] calculateMandelbrotIterations(int width, int height) {
		final float RE_PER_PIXEL = (MandelbrotKernel.RE_MAX - MandelbrotKernel.RE_MIN)/ width;
		final float IM_PER_PIXEL = (MandelbrotKernel.IM_MAX - MandelbrotKernel.IM_MIN)/ height;
		final int widthPerTask = width / N_TASKS;
		final int[] raster = new int[width * height];
		
		ExecutorService executor = Executors.newFixedThreadPool(8);
		for(int i = 0; i < N_TASKS; i++) {
			final int startX = i * widthPerTask;
			final int endX = startX + widthPerTask;
			
			executor.submit(new MandelSlice(RE_PER_PIXEL, IM_PER_PIXEL, startX, endX, raster, width, height));
		}
		executor.shutdown();
		try {
			executor.awaitTermination(1, TimeUnit.HOURS);
		} catch (InterruptedException iex) {
			System.err.println(iex);
		}
		
		return raster;
	}

	private static class MandelSlice implements Callable<Void> {
		private final int startX, endX;
		private final int[] raster; 
		private final float RE_PER_PIXEL;
		private final float IM_PER_PIXEL;
		private final int height;
		private final int width;
		
		public MandelSlice(float RE_PER_PIXEL, float IM_PER_PIXEL, int startX, int endX, int[] raster, int width, int height) {
			this.endX = endX; this.startX = startX;
			this.raster = raster;
			this.IM_PER_PIXEL = IM_PER_PIXEL;
			this.RE_PER_PIXEL = RE_PER_PIXEL;
			this.width = width;
			this.height = height;
		}
		
		@Override
		public Void call() throws Exception {
			for (int x = startX; x < endX; x++) {
				for (int y = 0; y < height; y++) {
					float re = MandelbrotKernel.RE_MIN + x * RE_PER_PIXEL;
					float im = MandelbrotKernel.IM_MIN + y * IM_PER_PIXEL;
					int iterations = MandelbrotKernel.mandel(re, im);
					raster[x*width + y] = iterations;
				}
			}
			return null;
		}
	}
	
	@Override
	public String toString() {
		return getClass().getSimpleName();
	}
}

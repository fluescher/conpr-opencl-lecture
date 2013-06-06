package ch.fhnw.conpr;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import ch.fhnw.conpr.mandel.MandelbrotCalculator;
import ch.fhnw.conpr.mandel.ParMandelbrotCalculator;
import ch.fhnw.conpr.mandel.AparapiMandelbrotCalculator.MandelbrotKernel;

public abstract class MandelbrotTest {
	
	protected abstract MandelbrotCalculator createTestableInstance();
	
	private int[] calculateMandelbrot(int width, int height) {
		ParMandelbrotCalculator calculator = new ParMandelbrotCalculator();
		return calculator.calculateMandelbrotIterations(width, height);
	}
	
	@Test
	public void testMaxIteration() {
		final int maxIterations = MandelbrotKernel.MAX_ITERATIONS;
		
		int[] image = calculateMandelbrot(4096, 4096);
		
		assertEquals(maxIterations, image[4096*2048+2048]);
	}
	
	@Test
	public void testOneIteration() {
		int[] image = calculateMandelbrot(4096, 4096);
		
		assertEquals(1, image[0]);
	}
}

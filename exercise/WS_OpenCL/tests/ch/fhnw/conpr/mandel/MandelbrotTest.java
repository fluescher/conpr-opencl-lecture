package ch.fhnw.conpr.mandel;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import ch.fhnw.conpr.mandel.AparapiMandelbrotCalculator.MandelbrotKernel;
import ch.fhnw.conpr.mandel.MandelbrotCalculator;

public abstract class MandelbrotTest {
	
	protected abstract MandelbrotCalculator createTestableInstance();
	
	private int[] calculateMandelbrot(int width, int height) {
		MandelbrotCalculator calculator = createTestableInstance();
		return calculator.calculateMandelbrotIterations(width, height);
	}
	
	@Test
	public void testMaxIteration() {
		final int maxIterations = MandelbrotKernel.MAX_ITERATIONS;
		
		int[] image = calculateMandelbrot(512, 512);
		
		assertEquals(maxIterations, image[512*256+256]);
	}
	
	@Test
	public void testOneIteration() {
		int[] image = calculateMandelbrot(512, 512);
		
		assertEquals(1, image[0]);
	}
}

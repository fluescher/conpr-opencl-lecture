package ch.fhnw.conpr.mandel;

import ch.fhnw.conpr.mandel.AparapiMandelbrotCalculator;
import ch.fhnw.conpr.mandel.MandelbrotCalculator;

public class AparapiMandelbrotTest extends MandelbrotTest {

	@Override
	protected MandelbrotCalculator createTestableInstance() {
		return new AparapiMandelbrotCalculator();
	}

}

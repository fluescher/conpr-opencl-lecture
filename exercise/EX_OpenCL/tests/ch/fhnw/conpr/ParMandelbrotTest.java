package ch.fhnw.conpr;

import ch.fhnw.conpr.mandel.MandelbrotCalculator;
import ch.fhnw.conpr.mandel.ParMandelbrotCalculator;

public class ParMandelbrotTest extends MandelbrotTest {

	@Override
	protected MandelbrotCalculator createTestableInstance() {
		return new ParMandelbrotCalculator();
	}

}

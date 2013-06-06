package ch.fhnw.conpr;

import ch.fhnw.conpr.mandel.MandelbrotCalculator;
import ch.fhnw.conpr.mandel.SequentialMandelbrotCalculator;

public class SequentialMandelbrotTest extends MandelbrotTest {

	@Override
	protected MandelbrotCalculator createTestableInstance() {
		return new SequentialMandelbrotCalculator();
	}

}

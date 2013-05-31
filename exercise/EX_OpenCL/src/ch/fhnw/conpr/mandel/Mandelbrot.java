package ch.fhnw.conpr.mandel;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.WritableRaster;
import java.io.File;
import java.util.Arrays;
import java.util.List;

import javax.imageio.ImageIO;

import ch.fhnw.conpr.mandel.AparapiMandelbrotCalculator.MandelbrotKernel;

public class Mandelbrot {
	public static final int IMG_WIDTH = 4096;
	public static final int IMG_HEIGHT = IMG_WIDTH;

	public static final double RE_PER_PIXEL = (MandelbrotKernel.RE_MAX - MandelbrotKernel.RE_MIN)/ IMG_WIDTH;
	public static final double IM_PER_PIXEL = (MandelbrotKernel.IM_MAX - MandelbrotKernel.IM_MIN)/ IMG_HEIGHT;

	private static List<MandelbrotCalculator> calculators = Arrays.asList(
			new AparapiMandelbrotCalculator(),
			new SequentialMandelbrotCalculator());

	public static void main(String[] args) throws Exception {
		for (MandelbrotCalculator calculator : calculators) {
			long start = System.currentTimeMillis();
			int[] iterations = calculator.calculateMandelbrotIterations(IMG_WIDTH, IMG_HEIGHT);
			long end = System.currentTimeMillis();
			BufferedImage image = calculateImageFromIterations(iterations);
			System.out.format("seq mandel  ms: %s\n", end - start);
			ImageIO.write(image, "png", new File(calculator.getClass().getSimpleName()+".png"));
		}
	}

	public static BufferedImage calculateImageFromIterations(int[] iterations) {
		BufferedImage image = new BufferedImage(IMG_WIDTH, IMG_HEIGHT,
				BufferedImage.TYPE_INT_ARGB);
		WritableRaster raster = image.getRaster();
		ColorModel model = image.getColorModel();
		for (int x = 0; x < IMG_WIDTH; x++) {
			for (int y = 0; y < IMG_HEIGHT; y++) {
				int iter = iterations[x * IMG_WIDTH + y];
				raster.setDataElements(x, y, getColor(iter, model));
			}
		}
		return image;
	}

	/** Your color computation goes here! */
	public static Color getCustomColor(int iterations) {
		Color c;
		if (iterations == MandelbrotKernel.MAX_ITERATIONS) {
			c = Color.BLACK;
		} else {
			int grey = MandelbrotKernel.MAX_ITERATIONS - iterations;
			c = new Color((4 * grey) % 256, grey, (4 * grey) % 256);
		}
		return c;
	}

	/** Fiddles with awt color model etc. */
	public static Object getColor(int iterations, ColorModel model) {
		return model.getDataElements(getCustomColor(iterations).getRGB(), null);
	}
}

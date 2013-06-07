package ch.fhnw.conpr.grid;

import com.amd.aparapi.Device;
import com.amd.aparapi.Kernel;
import com.amd.aparapi.Range;

public class AparapiGridCalculation {

	private static final int GRID_SIZE = 10;
	
	/**
	 * Calculates
	 * <p><code>
	 * dose[x][y][z] = sum of exp(i) for each i from 0 to densities[x][y][z]
	 * </code></p>
	 * for every voxel (three dimensional pixel) in the dose array.
	 * 
	 * @author Florian Luescher
	 */
	public static final class GridCalculationKernel extends Kernel {
		
		private int[] densities;
		private float[] dose;
		
		public GridCalculationKernel(int[] inputGrid) {
			this.densities = inputGrid;
			this.dose = new float[inputGrid.length];
		}
		
		@Override
		public void run() {
			int index = getGridIndex(getGlobalId(0), getGlobalId(1), getGlobalId(2));
			
			dose[index] = calculateDose(densities[index]);
		}
		
		private float calculateDose(int density) {
			float value = 0;
			for(int i = 0; i < density; i++) {
				value += exp(i);
			}
			return value;
		}
		
		private int getGridIndex(int x, int y, int z) {
			return x * getGlobalSize(0) * getGlobalSize(1) + y * getGlobalSize(1) + z;
		}
		
		public float[] getDose() {
			return dose;
		}
		
	}
	
	/** Switch called function to experiment with different density distributions. */
	public static int densityFunction(int x, int y, int z) {
		return equallyDistributed(x, y, z);
	}

	public static int equallyDistributed(int x, int y, int z) {
		return 200_000;
	}
	
	public static int quadraticDensityFunction(int x, int y, int z) {
		return (int) Math.pow(x+y+z, 4);
	}
	
	public static int exponentialDensityFunction(int x, int y, int z) {
		return (int) Math.pow(2, x+y);
	}
	
	public static int exponentiatedGaussianDistribution(int x, int y, int z) {
		final double EPSILON = 2E-1;
		final double MAX_EXPONENT = 16;
		
		double a = EPSILON*((x+y+z));
		return (int) Math.exp(MAX_EXPONENT * Math.exp(-(a*a)));
	}
	
	public static void main(String[] args) {
		final int ITERATIONS = 1;
		int[] densities = calculateDensityGrid();
		
		printGridPlane(densities, GRID_SIZE/2);
		
		/* let aparapi compile to opencl c code */
		runAlgorithm(Range.create3D(Device.firstCPU(),GRID_SIZE,GRID_SIZE,GRID_SIZE), densities);
		
		for(int i = 0; i < ITERATIONS; i++) {
			long cpuTime = runTime(Range.create3D(Device.firstCPU(),GRID_SIZE,GRID_SIZE,GRID_SIZE), densities);
			long gpuTime = runTime(Range.create3D(Device.firstGPU(),GRID_SIZE,GRID_SIZE,GRID_SIZE), densities);
			System.out.printf("CPU: %4d\tGPU: %4d%n", cpuTime, gpuTime);
		}
	}
	
	public static void printGridPlane(int[] grid, int plane) {
		for(int x = 0; x < GRID_SIZE; x++) {
			for(int y = 0; y < GRID_SIZE; y++) {
				System.out.printf("%10d ", grid[x * GRID_SIZE * GRID_SIZE + y * GRID_SIZE + plane]);
			}
			System.out.println();
		}
	}
	
	public static long runTime(Range range, int[] densities) {
		long start = System.currentTimeMillis();
		
		runAlgorithm(range, densities);
		
		return System.currentTimeMillis() - start;
	}
	
	public static float[] runAlgorithm(Range range, int[] densities) {
		GridCalculationKernel kernel = new GridCalculationKernel(densities);
		kernel.execute(range);
		return kernel.getDose();
	}
	
	public static int[] calculateDensityGrid() {
		int[] densities = new int[GRID_SIZE*GRID_SIZE*GRID_SIZE];
		for(int x = 0; x < GRID_SIZE; x++) {
			for(int y = 0; y < GRID_SIZE; y++) {
				for(int z = 0; z < GRID_SIZE; z++) {
					densities[x * GRID_SIZE * GRID_SIZE + y * GRID_SIZE + z] = densityFunction(x, y, z);
				}
			}
		}
		return densities;
	}
}

package ch.fhnw.conpr;

import static org.junit.Assert.assertNotNull;

import org.junit.Test;

import com.amd.aparapi.Device;
import com.amd.aparapi.OpenCLDevice;
import com.amd.aparapi.OpenCLDevice.DeviceSelector;

public class AvailableDevicesTest {

	@Test
	public void isGPUAvailable() {
		assertNotNull(Device.firstGPU());
	}
	
	@Test
	public void isCPUAvailable() {
		assertNotNull(Device.firstCPU());
	}
	
	@Test
	public void printAvailableDevices() {
		OpenCLDevice.select(new DeviceSelector() {
			@Override
			public OpenCLDevice select(OpenCLDevice currentDevice) {
				System.out.println(currentDevice);
				return null;
			}
		});
	}
}

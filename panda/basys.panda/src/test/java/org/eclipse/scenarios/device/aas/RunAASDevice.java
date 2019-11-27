package org.eclipse.scenarios.device.aas;

import static org.junit.Assert.assertTrue;

import org.eclipse.contexts.BaSyxExamplesContext_1MemoryAASServer_1SQLDirectory;
import org.eclipse.deployment.BaSyxDeployment;
import org.eclipse.examplescenario.BaSyxExampleScenario;
import org.eclipse.mockup.application.ReceiveDeviceMaintenanceApplication;
import org.eclipse.mockup.device.SimpleTCPDeviceMockup;
import org.eclipse.mockup.devicemanager.ManufacturingDeviceActiveAASManager;
import org.eclipse.mockup.servers.SupplierStatusServlet;
import org.eclipse.support.directory.ExamplesPreconfiguredDirectory;
import org.eclipse.basyx.vab.manager.VABConnectionManager;
import org.eclipse.basyx.vab.protocol.http.connector.HTTPConnectorProvider;
import org.junit.ClassRule;
import org.junit.Test;

/**
 * Example that illustrates a simple device (no control component), a manager, an application, an active Asset Administration Shells, and sub models
 * 
 * This example illustrates how an active AAS accesses dynamic information to provide, for example, data about the supply of replacement parts
 * 
 * @author kuhn
 *
 */
public class RunAASDevice extends BaSyxExampleScenario {

	
	/**
	 * VAB connection manager backend
	 */
	protected VABConnectionManager connManager = new VABConnectionManager(new ExamplesPreconfiguredDirectory(), new HTTPConnectorProvider());


	/**
	 * Instantiate and start context elements for this example. BaSyxDeployment contexts instantiate all
	 * components on the IP address of the host. Therefore, all components use the same IP address. 
	 */
	@ClassRule
	public static BaSyxDeployment context = new BaSyxDeployment(
				// Simulated servlets
				// - BaSys topology with one AAS Server and one SQL directory
				new BaSyxExamplesContext_1MemoryAASServer_1SQLDirectory().
					// Define additional scenario specific Servlets
					addServletMapping("/Mockup/Supplier/*", new SupplierStatusServlet()),
				
				// Simulated runnables
				// - Manufacturing device manager, e.g. deployed to additonal device
				new ManufacturingDeviceActiveAASManager(9998).setName("DeviceManager"),
				
				// Simulated mockups
				new SimpleTCPDeviceMockup(9998).setName("Device"),
				new ReceiveDeviceMaintenanceApplication().setName("Application")
			);



	/**
	 * Test sequence: 
	 * - Initialize device --> Device status update (ready)
	 * - Read device status from AAS
	 * - Invoke device service --> Device status update (running)
	 * - Read device status from AAS
	 * - Device service completes execution --> Device status update (complete)
	 * - Read device status from AAS
	 * - Device completes reset --> Device status update (idle)
	 * - Read device status from AAS
	 */
	@Test 
	public void test() throws Exception {
		// Application checks availability of spare parts
		waitfor( () -> ((ReceiveDeviceMaintenanceApplication) context.getRunnable("Application")).getDevicePartSupplyStatus() == 12 );
		assertTrue( ((ReceiveDeviceMaintenanceApplication) context.getRunnable("Application")).getDevicePartSupplyStatus() == 12 );
	}
}

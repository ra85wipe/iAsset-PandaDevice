package org.srfg.panda;

import org.junit.Test;

/********************************************************************************************************
 * This class tests the smart manufacturing robot franka panda
 * 
 * All real members form franka panda lib will be added statically with no respect to real instance
 * if you ever do an update on franca lib you should consider adding new values to this test class
 * 
 * @author mathias.schmoigl
 ********************************************************************************************************/
public class PandaDeviceTest {
	
	// device under test
	PandaDevice testDevice = new PandaDevice(1, "http://localhost:8080/basys.examples/Components/Directory/SQL");
	

	/*********************************************************************************************************
	 * Constructor
	 ********************************************************************************************************/
	public PandaDeviceTest() {}
	
	/*********************************************************************************************************
	 * Test sequence:
	 * this test can be used for demonstration purposes on any PC
	 * fake all members from franka lib statically and test iAsset registration with dummy data
	 * in real environment on panda pc this should work dynamically via ROS lib model transfer
	 ********************************************************************************************************/
	@Test 
	public void test() throws Exception {
		
		// TODO
		//testDevice.start();
	}
}
package org.eclipse.panda;

import org.junit.Test;

/********************************************************************************************************
 * This class tests the panda data communication adapter
 * 
 * All real members from franka panda lib will be added statically with no respect to real instance
 * if you ever do an update on franca lib you should consider adding new values to this test class
 * 
 * @author mathias.schmoigl
 ********************************************************************************************************/
public class PandaAdapterTest {
	
	// device under test
	PandaAdapter testAdapter = new PandaAdapter();
	

	/*********************************************************************************************************
	 * Constructor
	 ********************************************************************************************************/
	public PandaAdapterTest() {}
	
	/*********************************************************************************************************
	 * Test sequence:
	 ********************************************************************************************************/
	@Test 
	public void test() throws Exception {
		
		// TODO
		testAdapter.establishROSConnection();
	}
}
package org.eclipse.panda;

import java.util.Map;

import org.eclipse.basyx.aas.metamodel.map.AssetAdministrationShell;
import org.eclipse.basyx.aas.metamodel.map.descriptor.AASDescriptor;
import org.eclipse.basyx.aas.metamodel.map.descriptor.ModelUrn;
import org.eclipse.basyx.aas.metamodel.map.descriptor.SubmodelDescriptor;
import org.eclipse.basyx.aas.registration.proxy.AASRegistryProxy;
import org.eclipse.basyx.components.device.BaseSmartDevice;
import org.eclipse.basyx.models.controlcomponent.ExecutionState;
import org.eclipse.basyx.submodel.metamodel.map.SubModel;
import org.eclipse.basyx.submodel.metamodel.map.submodelelement.property.Property;
import org.eclipse.basyx.vab.manager.VABConnectionManager;
import org.eclipse.basyx.vab.modelprovider.VABElementProxy;
import org.eclipse.basyx.vab.modelprovider.map.VABMapProvider;
import org.eclipse.basyx.vab.protocol.basyx.server.BaSyxTCPServer;
import org.eclipse.basyx.vab.protocol.http.connector.HTTPConnectorProvider;
import org.eclipse.panda.franka.*;
import org.eclipse.panda.nodes.*;
import org.eclipse.support.directory.ExamplesPreconfiguredDirectory;


/********************************************************************************************************
 * This class implements the smart manufacturing robot franca panda
 * 
 * The device pushes its AAS to an external asset administration shell server
 * - The sub model "statusSM" is pushed to the external asset administration shell server as well
 * - The sub model "controllerSM" is provided by an BaSyx/TCP server of the smart device
 * 
 * @author mathias.schmoigl
 ********************************************************************************************************/
public class PandaDevice extends BaseSmartDevice {
	
	/*********************************************************************************************************
	 * protected members
	 ********************************************************************************************************/
	protected int serverPort = -1; // Server port	
	protected BaSyxTCPServer<VABMapProvider> server = null; // BaSyx/TCP Server that exports the control component
	protected VABElementProxy aasServerConnection = null; // AAS server connection
	
	// contains all franka information
	private FrankaState frankaState;
	
	// is the bridge to kafka world
	private PandaAdapter pandaAdapter;
	
	// ROS Nodes listening to messages from their topics
	private ROSNode_FrankaStates nodeFrankaStates;
	private ROSNode_JointStates nodeJointStates;
	private ROSNode_PCHeartBeat nodePCHeartBeat;
	
	
	/*********************************************************************************************************
	 * Getter/Setter
	 ********************************************************************************************************/
	public FrankaState getFrankaState() {
		return frankaState;
	}

	public void setFrankaState(FrankaState frankaState) {
		this.frankaState = frankaState;
	}
	
	public PandaAdapter getPandaAdapter() {
		return pandaAdapter;
	}

	public void setPandaAdapter(PandaAdapter pandaAdapter) {
		this.pandaAdapter = pandaAdapter;
	}
	
	

	/*********************************************************************************************************
	 * Constructor
	 ********************************************************************************************************/
	public PandaDevice(int port, String registryUrl) {
		
		super();
		serverPort = port;

		// Register URNs of managed VAB objects
		addShortcut("AAS",        new ModelUrn("urn:de.FHG:devices.es.iese:aas:1.0:3:x-509#001"));
		addShortcut("Status",     new ModelUrn("urn:de.FHG:devices.es.iese:statusSM:1.0:3:x-509#001"));
		addShortcut("Controller", new ModelUrn("urn:de.FHG:devices.es.iese:controllerSM:1.0:3:x-509#001"));

		// Configure BaSyx service - registry and connection manager
		setRegistry(new AASRegistryProxy(registryUrl));
		setConnectionManager(new VABConnectionManager(new ExamplesPreconfiguredDirectory(), new HTTPConnectorProvider()));
	}
	
	/*********************************************************************************************************
	 * Indicate a service invocation
	 ********************************************************************************************************/
	@SuppressWarnings("unchecked")
	@Override
	protected void onServiceInvocation() {

		super.onServiceInvocation();
		
		// Implement the device invocation counter - read and increment invocation counter
		Map<String, Object> property = (Map<String, Object>) aasServerConnection
				.getModelPropertyValue("/aas/submodels/Status/" + SubModel.PROPERTIES + "/invocations");
		int invocations = (int) property.get("value");
		aasServerConnection.setModelPropertyValue("/aas/submodels/Status/dataElements/invocations/value", ++invocations);
	}
	
	/*********************************************************************************************************
	 * Smart device control component indicates an execution state change
	 ********************************************************************************************************/
	@Override
	public void onChangedExecutionState(ExecutionState newExecutionState) {

		super.onChangedExecutionState(newExecutionState);
		
		// Update property "properties/status" in external AAS
		aasServerConnection.setModelPropertyValue("/aas/submodels/Status/dataElements/status/value",
				newExecutionState.getValue());
	}
	
	/*********************************************************************************************************
	 * CreateSubModel
	 * Create generic sub model and add properties
	 ********************************************************************************************************/
	private void CreatePandaSubModel() {
		
		SubModel sub = new SubModel();
		sub.setIdShort("FrankaPanda");
		
		
		// add property for robot mode
		Property modeProp = new Property(frankaState.robot_mode);
		modeProp.setIdShort("robotMode");
		sub.addSubModelElement(modeProp);
		
		
		// add properties for positions in 3D working env
		Property positionXProp = new Property(frankaState.O_T_EE[12]);
		positionXProp.setIdShort("posX");
		sub.addSubModelElement(positionXProp);
		
		Property positionYProp = new Property(frankaState.O_T_EE[13]);
		positionYProp.setIdShort("posY");
		sub.addSubModelElement(positionYProp);
		
		Property positionZProp = new Property(frankaState.O_T_EE[14]);
		positionZProp.setIdShort("posZ");
		sub.addSubModelElement(positionZProp);
		
		
		// add property for panda force 
		Property forceProp = new Property(frankaState.O_F_ext_hat_K[2]);
		forceProp.setIdShort("force");
		sub.addSubModelElement(forceProp);
		
		
		// TODO: add property for gripper states
		//actual_panda_gripper_states = rostopic/joint_states position[8]+position[9]; // ???	
		//Property gripperProp = new Property(actual_panda_gripper_states);
		//gripperProp.setIdShort("gripper");
		//sub.addSubModelElement(gripperProp);
		

		// TEST:
		// Property statistics: export invocation statistics for every service
		// invocations: indicate total service invocations. 
		// Properties are not persisted in this example, therefore start counting always at 0.
		Property invocationsProp = new Property(0);
		invocationsProp.setIdShort("invocations");
		sub.addSubModelElement(invocationsProp);
				
		
		// Transfer device sub model to server
		aasServerConnection.createValue("/aas/submodels", sub);
	}
	
	/*********************************************************************************************************
	 * RegisterSubModelsInDirectory
	 * Build an AAS descriptor, add sub model descriptors to it and push AAS descriptor to server
	 * @param aasRepoURL
	 ********************************************************************************************************/
	private void RegisterSubModelsInDirectory(String aasRepoURL) {
		
		AASDescriptor deviceAASDescriptor = new AASDescriptor(lookupURN("AAS"), aasRepoURL);
				
		SubmodelDescriptor statusSMDescriptor = 
				new SubmodelDescriptor("Status", lookupURN("Status"), aasRepoURL + "/submodels/Status");
		
		SubmodelDescriptor controllerSMDescriptor = 
				new SubmodelDescriptor("Controller", lookupURN("Controller"), "basyx://127.0.0.1:" + serverPort + "/submodels/Controller");
				
		deviceAASDescriptor.addSubmodelDescriptor(statusSMDescriptor);
		deviceAASDescriptor.addSubmodelDescriptor(controllerSMDescriptor);
				
		getRegistry().register(deviceAASDescriptor); // Push AAS descriptor to server
	}

	/*********************************************************************************************************
	 * Start panda device
	 ********************************************************************************************************/
	@Override
	public void start() {

		super.start();
		
		aasServerConnection = this.getConnectionManager().connectToVABElement("AASServer"); // Connect to AAS server
		AssetAdministrationShell aas = new AssetAdministrationShell().putPath("idShort", "DeviceIDShort"); // Create device AAS
		aasServerConnection.createValue("/aas", aas); // - Transfer device AAS to server

		// The device also brings a sub model structure with an own ID that is being pushed on the server
		CreatePandaSubModel();
		
		// Register control component as local sub model (This sub model will stay with the device) 
		server = new BaSyxTCPServer<>(new VABMapProvider(getControlComponent()), serverPort);
		
		// - Start local BaSyx/TCP server
		server.start();
		
		// Register AAS and sub models in directory (push AAS descriptor to server)
		RegisterSubModelsInDirectory("http://localhost:8080/basys.examples/Components/BaSys/1.0/aasServer/aas");			
	}

	/*********************************************************************************************************
	 * Stop panda device
	 ********************************************************************************************************/
	@Override
	public void stop() {		
		server.stop(); // Stop local BaSyx/TCP server
	}

	/*********************************************************************************************************
	 * Wait for completion of all threads
	 ********************************************************************************************************/
	@Override
	public void waitFor() {
		server.waitFor(); // Wait for server end
	}
}

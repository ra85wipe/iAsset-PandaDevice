package org.srfg.panda;

import java.util.Map;

import org.eclipse.basyx.aas.metamodel.map.AssetAdministrationShell;
import org.eclipse.basyx.aas.metamodel.map.descriptor.AASDescriptor;
import org.eclipse.basyx.aas.metamodel.map.descriptor.ModelUrn;
import org.eclipse.basyx.aas.metamodel.map.descriptor.SubmodelDescriptor;
import org.eclipse.basyx.aas.registration.proxy.AASRegistryProxy;
import org.eclipse.basyx.components.device.BaseSmartDevice;
import org.eclipse.basyx.models.controlcomponent.ExecutionState;
import org.eclipse.basyx.submodel.metamodel.api.reference.IReference;
import org.eclipse.basyx.submodel.metamodel.map.SubModel;
import org.eclipse.basyx.submodel.metamodel.map.qualifier.Description;
import org.eclipse.basyx.submodel.metamodel.map.reference.Reference;
import org.eclipse.basyx.submodel.metamodel.map.submodelelement.property.Property;
import org.eclipse.basyx.vab.exception.ServerException;
import org.eclipse.basyx.vab.manager.VABConnectionManager;
import org.eclipse.basyx.vab.modelprovider.VABElementProxy;
import org.eclipse.basyx.vab.protocol.http.connector.HTTPConnectorProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.srfg.panda.franka.*;
import org.srfg.panda.nodes.ROSNodeManager;
import org.srfg.support.directory.ExamplesPreconfiguredDirectory;


/********************************************************************************************************
 * This class implements the smart manufacturing robot franka panda
 * 
 * The device pushes its AAS to an external asset administration shell server
 * - The sub model "FrankaPandaSubModel" is pushed to the aas server as well
 * 
 * @author mathias.schmoigl
 ********************************************************************************************************/
public class PandaDevice extends BaseSmartDevice {

	private static Logger logger = LoggerFactory.getLogger(PandaDevice.class);
	
	/*********************************************************************************************************
	 * protected members required for server client communication
	 ********************************************************************************************************/
	protected VABElementProxy aasServerConnection = null; // AAS server connection

	/*********************************************************************************************************
	 * private members required for panda communication via ROS
	 ********************************************************************************************************/
	private ROSNodeManager nodeManager = new ROSNodeManager();
	private FrankaState frankaState;
	private JointState jointState;
	
	/*********************************************************************************************************
	 * Getter/Setter
	 ********************************************************************************************************/
	public FrankaState getFrankaState() { return frankaState; }
	public void setFrankaState(FrankaState frankaState) {
		this.frankaState = frankaState;
	}

	public JointState getJointState() {
		return jointState;
	}
	public void setJointState(JointState jointState) {
		this.jointState = jointState;
	}
	
	public ROSNodeManager getROSNodeManager() {
		return nodeManager;
	}
	public void setROSNodeManager(ROSNodeManager nodeManager) {
		this.nodeManager = nodeManager;
	}

	public void establishROSConnection()
	{
		nodeManager.executeROSNodes();
	}

	/*********************************************************************************************************
	 * Constructor
	 ********************************************************************************************************/
	public PandaDevice(String registryUrl) {
		
		super();

		// Register URNs of managed VAB objects
		addShortcut("FrankaPandaAAS",        new ModelUrn("urn:de.FHG:devices.es.iese:FrankaPandaAAS:1.0:3:x-509#001"));
		addShortcut("FrankaPandaSubModel",     new ModelUrn("urn:de.FHG:devices.es.iese:FrankaPandaSubModel:1.0:3:x-509#001"));

		// Configure BaSyx service - registry and connection manager
		setRegistry(new AASRegistryProxy(registryUrl));
		setConnectionManager(new VABConnectionManager(new ExamplesPreconfiguredDirectory(), new HTTPConnectorProvider()));
	}

	/*********************************************************************************************************
	 * Start panda device
	 ********************************************************************************************************/
	@Override
	public void start() {

		super.start();

		// the device creates a value if a server connection can be established
		if(CreateAASDeviceOnServer("AASServer", "AASPath", "AASValue", "/aas"))
		{
			// create the device's sub model structure with own ID and push it to server
			CreatePandaSubModel();

			// Register AAS and sub models in directory (push AAS descriptor to server)
			String aasRepoURL = "http://localhost:8085/assetregistry"; // http://localhost:8080/basys.examples/Components/BaSys/1.0/aasServer/aas
			RegisterSubModelsInDirectory(aasRepoURL, aasRepoURL + "/submodels/FrankaPandaSubModel");
		}
	}

	/*********************************************************************************************************
	 * Stop panda device
	 ********************************************************************************************************/
	@Override
	public void stop() {
		//TODO: stop ROS subscriber threads one after another
	}

	/*********************************************************************************************************
	 * Wait for completion of all threads
	 ********************************************************************************************************/
	@Override
	public void waitFor() {
		//TODO: wait for ROS subscriber threads
	}


	/*********************************************************************************************************
	 * Smart device control component indicates an execution state change
	 ********************************************************************************************************/
	@Override
	public void onChangedExecutionState(ExecutionState newExecutionState) {

		super.onChangedExecutionState(newExecutionState);

		// Update property "properties/status" in external AAS
		String elementPath = "/aas/submodels/FrankaPandaSubModel/dataElements/status/value";
		aasServerConnection.setModelPropertyValue(elementPath, newExecutionState.getValue());
	}

	/*********************************************************************************************************
	 * CreateValueOnConnectedServer
	 * Create this AAS device as a value on a connected server
	 ********************************************************************************************************/
	private boolean CreateAASDeviceOnServer(String urnVABElement, String aasPath, String aasValue, String elementPath) {

		try
		{
			aasServerConnection = this.getConnectionManager().connectToVABElement(urnVABElement); // Connect to AAS server
			AssetAdministrationShell aas = new AssetAdministrationShell().putPath(aasPath, aasValue); // Create device AAS
			aasServerConnection.createValue(elementPath, aas); // Transfer device AAS to server -> fails if not connected
			return true;
		}
		catch (ServerException e)
		{
			logger.debug("An exception occured while attempting to create aas device on server! Message was: " +  e.getMessage());
			return false; // no connection
		}
	}

	/*********************************************************************************************************
	 * CreateSubModel
	 * Create generic sub model and add properties
	 ********************************************************************************************************/
	private void CreatePandaSubModel() {

		SubModel sub = new SubModel();
		sub.setIdShort("FrankaPandaSubModel");
		sub.setSemanticID((IReference) new Reference().put("27380107", null)); // e-class-ID "Roboterarm"
		sub.setDescription(new Description("en", "Franka Panda submodel implements aas example for the iAsset Platform"));

		// add property for robot mode
		Property modeProp = new Property(frankaState.robot_mode);
		modeProp.setIdShort("robotMode");
		modeProp.setSemanticID((IReference) new Reference().put("0173-1#02-AAK543#004", null)); // e-class-ID "anwenderrelevante Ausführung"
		modeProp.setDescription(new Description("en", "robot mode represents current state of franka panda robot"));
		sub.addSubModelElement(modeProp);

		// add properties for positions in 3D working env
		Property positionXProp = new Property(frankaState.O_T_EE[12]);
		positionXProp.setIdShort("posX");
		positionXProp.setSemanticID((IReference) new Reference().put("0173-1#02-AAZ424#001", null)); // e-class-ID "Positionserkennung"
		positionXProp.setDescription(new Description("en", "franka panda robot end effector position X"));
		positionXProp.addDataSpecificationReference((IReference) new Reference().put("Centimeters", null));
		sub.addSubModelElement(positionXProp);

		Property positionYProp = new Property(frankaState.O_T_EE[13]);
		positionYProp.setIdShort("posY");
		positionYProp.setSemanticID((IReference) new Reference().put("0173-1#02-AAZ424#001", null)); // e-class-ID "Positionserkennung"
		positionYProp.setDescription(new Description("en", "franka panda robot end effector position Y"));
		positionYProp.addDataSpecificationReference((IReference) new Reference().put("Newton", null));
		sub.addSubModelElement(positionYProp);

		Property positionZProp = new Property(frankaState.O_T_EE[14]);
		positionZProp.setIdShort("posZ");
		positionZProp.setSemanticID((IReference) new Reference().put("0173-1#02-AAZ424#001", null)); // e-class-ID "Positionserkennung"
		positionZProp.setDescription(new Description("en", "franka panda robot end effector position Z"));
		positionZProp.addDataSpecificationReference((IReference) new Reference().put("Centimeters", null));
		sub.addSubModelElement(positionZProp);

		// add property for panda force
		Property forceProp = new Property(frankaState.O_F_ext_hat_K[3]);
		forceProp.setIdShort("z-force");
		forceProp.setSemanticID((IReference) new Reference().put("0173-1#02-AAI621#002", null)); // e-class-ID "Hebekraft"
		forceProp.setDescription(new Description("en", "franka panda robot force for z-axis"));
		forceProp.addDataSpecificationReference((IReference) new Reference().put("Centimeters", null));
		sub.addSubModelElement(forceProp);

		// add property for gripper states
		Property gripperProp = new Property(jointState.position[8] + jointState.position[9]); // gripper distance
		gripperProp.setIdShort("gripper distance");
		gripperProp.setSemanticID((IReference) new Reference().put("0173-1#02-AAZ424#001", null)); // e-class-ID "Positionserkennung"
		gripperProp.setDescription(new Description("en", "distance of gripper parts to each other"));
		gripperProp.addDataSpecificationReference((IReference) new Reference().put("Centimeters", null));
		sub.addSubModelElement(gripperProp);

		// TODO: test access increment on server requests as soon as server works
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
	 * Indicate a service invocation
	 ********************************************************************************************************/
	@SuppressWarnings("unchecked")
	@Override
	protected void onServiceInvocation() {

		super.onServiceInvocation();
		
		// Implement the device invocation counter - read and increment invocation counter
		Map<String, Object> property
				= (Map<String, Object>) aasServerConnection.getModelPropertyValue("/aas/submodels/FrankaPandaSubModel/" + SubModel.PROPERTIES + "/invocations");
		int invocations = (int) property.get("value");
		aasServerConnection.setModelPropertyValue("/aas/submodels/FrankaPandaSubModel/dataElements/invocations/value", ++invocations);
	}
	
	/*********************************************************************************************************
	 * RegisterSubModelsInDirectory
	 * Build an AAS descriptor, add sub model descriptors to it and push AAS descriptor to server
	 * @param aasRepoURL
	 ********************************************************************************************************/
	private void RegisterSubModelsInDirectory(String aasRepoURL, String endpoint) {
		
		AASDescriptor deviceDesc = new AASDescriptor(lookupURN("FrankaPandaAAS"), aasRepoURL);
		deviceDesc.addSubmodelDescriptor(new SubmodelDescriptor("FrankaPandaSubModel", lookupURN("FrankaPandaSubModel"), endpoint));
		getRegistry().register(deviceDesc); // Push AAS descriptor to server
	}
}

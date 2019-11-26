package org.eclipse.mockup.devicemanager;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.basyx.aas.metamodel.map.AssetAdministrationShell;
import org.eclipse.basyx.aas.metamodel.map.descriptor.AASDescriptor;
import org.eclipse.basyx.aas.metamodel.map.descriptor.ModelUrn;
import org.eclipse.basyx.aas.registration.proxy.AASRegistryProxy;
import org.eclipse.basyx.components.configuration.CFGBaSyxProtocolType;
import org.eclipse.basyx.components.devicemanager.TCPDeviceManagerComponent;
import org.eclipse.support.directory.ExamplesPreconfiguredDirectory;
import org.eclipse.basyx.submodel.metamodel.map.SubModel;
import org.eclipse.basyx.submodel.metamodel.map.submodelelement.property.Property;
import org.eclipse.basyx.vab.manager.VABConnectionManager;
import org.eclipse.basyx.vab.modelprovider.VABElementProxy;
import org.eclipse.basyx.vab.protocol.http.connector.HTTPConnectorProvider;

/**
 * Example manufacturing device manager code
 * 
 * This example code illustrates a basic device manager component. It implements the interaction between a device and the BaSyx infrastructure.
 * This code is for example deployed on the device (in case of availability of a Java runtime environment) or to an explicit connector device.
 * The Asset Administration Shell is not kept on the device, but transferred to an AAS server during registration. This ensures its presence also
 * if the device itself is not available, e.g. due to a failure. Important asset data, such as manufacturer, and support contacts remain available
 * in this case.
 * 
 * This code implements the following:
 * - Registration of device the AAS and sub models with the BaSyx infrastructure
 * - Updating of sub model properties to reflect the device status
 * - TCP connection to legacy device
 * 
 * 
 * @author kuhn
 *
 */
public class ManufacturingDeviceManager extends TCPDeviceManagerComponent {

	
	/**
	 * AAS server connection
	 */
	protected VABElementProxy aasServerConnection = null;






	/**
	 * Constructor
	 */
	public ManufacturingDeviceManager(int port) {
		// Invoke base constructor
		super(port);
		
		
		// Configure this device manager
		configure()
			.registryURL("http://localhost:8080/basys.examples/Components/Directory/SQL")
			.connectionManagerType(CFGBaSyxProtocolType.HTTP)
				.directoryService(new ExamplesPreconfiguredDirectory())
			.end();
		
		// configure()
		//   .registryURL()
		//   .connectionManagerDirectory(new ExamplesPreconfiguredDirectory())
		//   .connectionManagerProtocol(HTTP)
		//   .AASServerObjectID(...)
		//   .addAASAAS()
		//   	.whateverAASProperty()
		//   	.addSubmodel()
		//			.property()
		//			.endSubModel()
		//		.end();
		
		
		// configure(Map<>...)
		
		// Set registry that will be used by this service
		setRegistry(new AASRegistryProxy("http://localhost:8080/basys.examples/Components/Directory/SQL"));
		
		
		// Set service connection manager and create AAS server connection
		setConnectionManager(new VABConnectionManager(new ExamplesPreconfiguredDirectory(), new HTTPConnectorProvider()));
		// - Create AAS server connection
		aasServerConnection = getConnectionManager().connectToVABElement("AASServer");
		
		
		// Set AAS server VAB object ID, AAS server URL, and AAS server path prefix
		setAASServerObjectID("AASServer");
		setAASServerURL("http://localhost:8080/basys.examples/Components/BaSys/1.0/aasServer");
	}



	/**
	 * Initialize the device, and register it with the backend
	 */
	@Override 
	public void start() {
		// Base implementation
		super.start();
		
		// Create the device AAS and sub model structure
		createDeviceAASAndSubModels();
		
		// Register AAS and sub model descriptors in directory (push AAS descriptor to server)
		getRegistry().register(getAASDescriptor());
	}
	
	
	/**
	 * Get AAS descriptor for managed device
	 */
	@Override 
	protected AASDescriptor getAASDescriptor() {
		// Create AAS and sub model descriptors
		AASDescriptor aasDescriptor = new AASDescriptor(lookupURN("AAS"), getAASEndpoint(lookupURN("AAS")));
		addSubModelDescriptorURI(aasDescriptor, lookupURN("Status"), "Status");
		addSubModelDescriptorURI(aasDescriptor, lookupURN("Controller"), "Controller");
		
		// Return AAS and sub model descriptors
		return aasDescriptor;
	}

	
	
	/**
	 * Create the device AAS and sub model structure
	 */
	@SuppressWarnings("unchecked")
	protected void createDeviceAASAndSubModels() {
		
		// Register URNs of managed VAB objects
		addShortcut("AAS",        new ModelUrn("urn:de.FHG:devices.es.iese:aas:1.0:3:x-509#001"));
		addShortcut("Status",     new ModelUrn("urn:de.FHG:devices.es.iese:statusSM:1.0:3:x-509#001"));
		addShortcut("Controller", new ModelUrn("urn:de.FHG:devices.es.iese:controllerSM:1.0:3:x-509#001"));
		

		// Create device AAS
		AssetAdministrationShell aas = new AssetAdministrationShell();
		// - Populate AAS
		aas.setIdShort("DeviceIDShort");
		// - Transfer device AAS to server
		aasServerConnection.createValue("/aas", aas);

	
		// The device also brings a sub model structure with an own ID that is being pushed on the server
		// - Create generic sub model and add properties
		SubModel statusSM = new SubModel();
		// - Set submodel ID
		statusSM.setIdShort("Status");
		//   - Property status: indicate device status
		Property statusProp = new Property("offline");
		statusProp.setIdShort("status");
		statusSM.addSubModelElement(statusProp);
		//   - Property statistics: export invocation statistics for every service
		//     - invocations: indicate total service invocations. Properties are not persisted in this example,
		//                    therefore we start counting always at 0.
		Property invocationsProp = new Property(0);
		invocationsProp.setIdShort("invocations");
		statusSM.addSubModelElement(invocationsProp);
		// - Transfer device sub model to server
		aasServerConnection.createValue("/aas/submodels/", statusSM);

		
		// The device also brings a sub model structure with an own ID that is being pushed on the server
		// - Create generic sub model 
		SubModel controllerSM = new SubModel();
		// - Set submodel ID
		controllerSM.setIdShort("Controller");
		//   - Create sub model contents manually
		Map<String, Object> listOfControllers = new HashMap<>();
		((Map<String, Object>) controllerSM.get(SubModel.PROPERTIES)).put("controllers", listOfControllers);
		// - Transfer device sub model to server
		aasServerConnection.createValue("/aas/submodels", controllerSM);
	}


	
	
	
	
	/**
	 * Received a string from network
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void onReceive(byte[] rxData) {
		// Do not process null values
		if (rxData == null) return;
		
		// Convert received data to string
		String rxStr = new String(rxData); 
		// - Trim string to remove possibly trailing and leading white spaces
		rxStr = rxStr.trim();
		
		// Check what was being received. This check is performed based on a prefix that he device has to provide);
		// - Update of device status
		if (hasPrefix(rxStr, "status:")) aasServerConnection.setModelPropertyValue("/aas/submodels/Status/dataElements/status/value", removePrefix(rxStr, "status"));
		// - Device indicates service invocation
		if (hasPrefix(rxStr, "invocation:")) {
			// Start of process
			if (hasPrefix(rxStr, "invocation:start")) {
				// Read and increment invocation counter
				HashMap<String, Object> property = (HashMap<String, Object>) aasServerConnection.getModelPropertyValue("/aas/submodels/Status/dataElements/invocations");
				int invocations = (int) property.get("value");
				aasServerConnection.setModelPropertyValue("/aas/submodels/Status/dataElements/invocations/value", ++invocations);
			} 
			// End of process
			if (hasPrefix(rxStr, "invocation:end")) {
				// Do nothing for now
			}
		}
	}
}

package org.eclipse.panda;

import java.util.Map;
import java.io.FileReader;
import java.net.URI;
import java.net.URISyntaxException;

import org.eclipse.panda.nodes.ROSNodeManager;
import org.json.JSONObject;

import com.google.gson.JsonObject;


/********************************************************************************************************
 * This class implements an adapter to receive real panda data
 * 
 * The adapter will establish communication to the hosted kafka world
 * and will publish all received information from PandaDevice class
 * 
 * @author mathias.schmoigl
 ********************************************************************************************************/
public class PandaAdapter {

	ROSNodeManager manager = new ROSNodeManager();
	
	/********************************************************************************************************
	 * CTOR
	 ********************************************************************************************************/
	public PandaAdapter() {
		
		// Panta Rhei configuration members
		// see if required variable environments are set and init vars accordingly
		Map<String, String> env = System.getenv();
		String clientName = (env.containsKey("CLIENT_NAME")) ? System.getenv("CLIENT_NAME") : "ros-adapter";
		String systemName = (env.containsKey("SYSTEM_NAME")) ? System.getenv("SYSTEM_NAME") : "at.srfg.iot.dtz";	
		String sensorthingsHost = (env.containsKey("SENSORTHINGS_HOST")) ? System.getenv("SENSORTHINGS_HOST") : "192.168.48.71:8082";	
		String bootstrapServers = (env.containsKey("BOOTSTRAP_SERVERS")) ? System.getenv("BOOTSTRAP_SERVERS") :
			"192.168.48.71:9092,192.168.48.72:9092,192.168.48.73:9092,192.168.48.74:9092,192.168.48.75:9092";
		
		// build panta rhei json config object // TODO: is this still needed??
		JsonObject config = new JsonObject();
		config.addProperty("client_name", clientName);
		config.addProperty("system", systemName);
		config.addProperty("kafka_bootstrap_servers", bootstrapServers);
		config.addProperty("gost_servers", sensorthingsHost);
		
		
		// try read instances and mapping json files // TODO: is this still needed??
		//JSONParser parser = new JSONParser();
		//try {
		//	JSONObject instances = (JSONObject) parser.parse(new FileReader("./models/instances.json"));
		//	JSONObject mappings = (JSONObject) parser.parse(new FileReader("./models/ds-mappings.json"));
		//} catch (Exception e) {
        //   e.printStackTrace();
        //}
	}

	public void establishROSConnection()
	{
		manager.executeROSNodes();
	}

}


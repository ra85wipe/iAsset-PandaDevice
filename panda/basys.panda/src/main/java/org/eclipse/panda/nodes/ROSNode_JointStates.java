
package org.eclipse.panda.nodes;

import org.apache.commons.logging.Log;
import org.ros.message.MessageListener;
import org.ros.namespace.GraphName;
import org.ros.node.ConnectedNode;
import org.ros.node.Node;
import org.ros.node.NodeMain;
import org.ros.node.topic.Subscriber;



/********************************************************************************************************
 * This class represents a listener node subscribing to required panda topics
 * 
 * tutorials:
 * http://rosjava.github.io/rosjava_core/latest/getting_started.html
 * https://answers.ros.org/question/313257/rosjava-how-to-start-to-use-rosjava_core-with-maven/
 * 
 * @author mathias.schmoigl
 ********************************************************************************************************/
public class ROSNode_JointStates implements NodeMain {
	
	private static String ROS_TOPIC_JOINT_STATES = "/joint_states";


	/********************************************************************************************************
	 * getDefaultNodeName
	 ********************************************************************************************************/
	@Override
	public GraphName getDefaultNodeName() {
		 return GraphName.of(ROS_TOPIC_JOINT_STATES);
	}

	/********************************************************************************************************
	 * onStart
	 ********************************************************************************************************/
	@Override
	public void onStart(ConnectedNode connectedNode) {
	  
		final Log log = connectedNode.getLog();
	  
		Subscriber<std_msgs.String> subscriberJointStates = connectedNode.newSubscriber(ROS_TOPIC_JOINT_STATES, std_msgs.String._TYPE);
		subscriberJointStates.addMessageListener(new MessageListener<std_msgs.String>() 
		{		  
			@Override
			public void onNewMessage(std_msgs.String message) {
				log.info("I heard: \"" + message.getData() + "\"");
			}
		});
	}

	
	/********************************************************************************************************
	 * TODO
	 ********************************************************************************************************/
	@Override
	public void onError(Node arg0, Throwable arg1) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onShutdown(Node arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onShutdownComplete(Node arg0) {
		// TODO Auto-generated method stub
		
	}

}
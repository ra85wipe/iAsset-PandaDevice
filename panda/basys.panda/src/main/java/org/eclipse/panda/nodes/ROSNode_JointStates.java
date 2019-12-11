
package org.eclipse.panda.nodes;

import org.apache.commons.logging.Log;
import org.ros.message.MessageListener;
import org.ros.namespace.GraphName;
import org.ros.node.AbstractNodeMain;
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
public class ROSNode_JointStates extends AbstractNodeMain {
	
	private static String ROS_MASTER_URI = "http://192.168.48.41:11311"; // export ROS_MASTER_URI=http://192.168.48.41:11311
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
	public void onStart(final ConnectedNode connectedNode) {
	  
		final Log log = connectedNode.getLog();
	  
		Subscriber<sensor_msgs.JointState> subscriberJointStates =
				connectedNode.newSubscriber(ROS_MASTER_URI + ROS_TOPIC_JOINT_STATES, sensor_msgs.JointState._TYPE);
		
		subscriberJointStates.addMessageListener(new MessageListener<sensor_msgs.JointState>()
		{		  
			@Override
			public void onNewMessage(sensor_msgs.JointState message) {
				log.info("I heard something");
			}
		});
	}

	
	/********************************************************************************************************
	 * TODO
	 ********************************************************************************************************/
	@Override
	public void onError(Node arg0, Throwable arg1) {
		// TODO Auto-generated method stub
		arg0.getLog().info("I heard somthing!");
	}

	@Override
	public void onShutdown(Node arg0) {
		// TODO Auto-generated method stub
		arg0.getLog().info("I heard somthing!");
	}

	@Override
	public void onShutdownComplete(Node arg0) {
		// TODO Auto-generated method stub
		arg0.getLog().info("I heard somthing!");
	}

}
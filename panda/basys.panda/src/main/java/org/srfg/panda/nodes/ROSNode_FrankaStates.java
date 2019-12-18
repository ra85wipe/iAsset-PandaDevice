
package org.srfg.panda.nodes;

import org.apache.commons.logging.Log;
import org.ros.message.MessageListener;
import org.ros.namespace.GraphName;
import org.ros.node.AbstractNodeMain;
import org.ros.node.ConnectedNode;
import org.ros.node.Node;
import org.ros.node.topic.Subscriber;



/********************************************************************************************************
 * This class represents a listener node subscribing to required panda topics
 * 
 * tutorials:
 * http://rosjava.github.io/rosjava_core/latest/getting_started.html
 * https://answers.ros.org/question/313257/rosjava-how-to-start-to-use-rosjava_core-with-maven/
 * http://docs.ros.org/kinetic/api/franka_msgs/html/msg/FrankaState.html
 * 
 * @author mathias.schmoigl
 ********************************************************************************************************/
public class ROSNode_FrankaStates extends AbstractNodeMain {

	private static String ROS_TOPIC_FRANKA_STATES = "/franka_state_controller/franka_states";

	/********************************************************************************************************
	 * getDefaultNodeName
	 ********************************************************************************************************/
	@Override
	public GraphName getDefaultNodeName() {
		 return GraphName.of(ROS_TOPIC_FRANKA_STATES);
	}

	/********************************************************************************************************
	 * onStart
	 ********************************************************************************************************/
	@Override
	public void onStart(final ConnectedNode connectedNode) {
	  
		final Log log = connectedNode.getLog();
	  
		Subscriber<franka_msgs.FrankaState> subscriberFrankaStates =
				connectedNode.newSubscriber(connectedNode.getName().toString(), franka_msgs.FrankaState._TYPE);
		
		subscriberFrankaStates.addMessageListener(new MessageListener<franka_msgs.FrankaState>()
		{		  
			@Override
			public void onNewMessage(franka_msgs.FrankaState message) {

				log.info(message.getHeader().getSeq()); // log message package seq number
			}
		});
	}

	
	/********************************************************************************************************
	 * TODO
	 ********************************************************************************************************/
	@Override
	public void onError(Node arg0, Throwable arg1) {

		if(arg0 != null) {
			arg0.getLog().info("Error happened!");
		}
	}

	@Override
	public void onShutdown(Node arg0) {

		if(arg0 != null) {
			arg0.getLog().info("Shutdown happened!");
		}
	}

	@Override
	public void onShutdownComplete(Node arg0) {

		if(arg0 != null) {
			arg0.getLog().info("Shutdown Complete happened!");
		}
	}
}
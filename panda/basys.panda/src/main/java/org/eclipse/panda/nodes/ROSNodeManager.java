package org.eclipse.panda.nodes;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import org.ros.exception.RosRuntimeException;
import org.ros.internal.loader.CommandLineLoader;
import org.ros.node.DefaultNodeMainExecutor;
import org.ros.node.NodeConfiguration;
import org.ros.node.NodeMainExecutor;
import java.net.URI;

// This class will run a publisher and subscriber, and relay data between them.
/********************************************************************************************************
 * This class implements manager of all used ROS nodes
 *
 * The manager will init all ROS subscriber and publisher nodes
 * rosjava messages used; converted franka messages used
 *
 * @author mathias.schmoigl
 ********************************************************************************************************/
public class ROSNodeManager {

    private static String ROS_MASTER_URI = "http://192.168.48.41:11311"; // export ROS_MASTER_URI=http://192.168.48.41:11311

    // ROS Nodes listening to messages from their topics
    private ROSNode_FrankaStates nodeFrankaStates = new ROSNode_FrankaStates();
    private ROSNode_JointStates nodeJointStates = new ROSNode_JointStates();
    private ROSNode_PCHeartBeat nodePCHeartBeat = new ROSNode_PCHeartBeat();

    public ROSNodeManager() {}

    public void executeROSNodes()
    {
        URI masteruri = URI.create(ROS_MASTER_URI);
        String host = "192.168.48.41";

        //NodeConfiguration pubNodeConfiguration = NodeConfiguration.newPublic(host, masteruri); // Load the publisher(talker)
        //Preconditions.checkState(pubNodeMain != null); //Check if Talker class correctly instantiated
        //nodeMainExecutor.execute(nodeFrankaStates, pubNodeConfiguration); //execute the nodelet talker (this will run the method onStart of Talker.java)

        NodeMainExecutor nodeMainExecutor = DefaultNodeMainExecutor.newDefault();
        //NodeConfiguration subNodeConfiguration = NodeConfiguration.newPublic(host, masteruri); // Load the subscriber(listener)
        NodeConfiguration subNodeConfiguration = NodeConfiguration.newPrivate(masteruri);

        Preconditions.checkState(nodeFrankaStates != null);
        nodeMainExecutor.execute(nodeFrankaStates, subNodeConfiguration);

        Preconditions.checkState(nodeJointStates != null);
        nodeMainExecutor.execute(nodeJointStates, subNodeConfiguration);

        //Preconditions.checkState(nodePCHeartBeat != null);
        //nodeMainExecutor.execute(nodePCHeartBeat, subNodeConfiguration);

        while(true){}
    }
}
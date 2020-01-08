package org.srfg;

import org.srfg.panda.PandaAdapter;
import org.srfg.panda.PandaDevice;

/********************************************************************************************************
 * This class contains the main function implementation and
 * serves as the basyx panda client starting point
 *
 * @author mathias.schmoigl
 ********************************************************************************************************/
public class MainClass {

    public static void main(String[] args)
    {
        // AAS device implementation of panda
        PandaDevice device = new PandaDevice(1, "http://localhost:8080/basys.examples/Components/Directory/SQL");
        device.start();

        // ROS panda adapter
        PandaAdapter adapter = new PandaAdapter();
        adapter.establishROSConnection();

        // wait forever in main thread
        // worker threads will listen to subscribed ROS nodes
        while(true){}
    }
}

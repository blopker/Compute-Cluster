package client;

import api.RMIUtils;
import api.Result;
import api.SpaceAPI;
import java.rmi.RemoteException;
import java.util.logging.Level;
import java.util.logging.Logger;
import tasks.HelloTask;


/**
 * The default client. Runs the HelloTask and prints the returned string.
 * This should be extended to make new clients.
 * @author karl
 */
class Client {

    private static void run(SpaceAPI space) {
        try {
            space.put(new HelloTask());
            Result reslut = space.take();
            String hello = (String) reslut.getTaskReturnValue();
            System.out.println("Result: " + hello);
        } catch (RemoteException ex) {
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static void main(String[] args) throws Exception {
        SpaceAPI space = RMIUtils.connectToSpace(args[0]);
        run(space);
    }
}
package client;

import api.RMIUtils;
import api.Result;
import api.SpaceAPI;
import java.rmi.RemoteException;
import java.util.logging.Level;
import java.util.logging.Logger;
import tasks.FibTask;

/**
 *
 * @author ninj0x
 */
public class FibClient {
     private static int N = 20;
    
     public static void main(String[] args) {
        SpaceAPI space = RMIUtils.connectToSpace(args[0]);

        int fib;
        long startTime, endTime;
        startTime = System.currentTimeMillis();
        fib = run(space);
        endTime = System.currentTimeMillis() - startTime;
        System.out.println("Tasks Done! f(" + N + ")  = " + fib);
        System.out.println("Total Time: " + endTime);
     }
     
     private static int run(SpaceAPI space) {
        try {
            // Send out the tasks
            FibTask task = new FibTask(N);
            space.put(task);

            // Get the results
            Result<Integer> result = space.take();
            return result.getResult();
            
        } catch (RemoteException ex) {
            Logger.getLogger(FibClient.class.getName()).log(Level.SEVERE, null, ex);
        }
        return 0;
    }
}

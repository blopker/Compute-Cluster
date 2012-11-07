package system;

import api.ComputerAPI;
import api.RMIUtils;
import api.Result;
import api.SpaceAPI;
import api.Task;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

/**
 * Implements the ComputerAPI and can execute tasks when asked nicely.
 *
 * @author karl
 */
class Computer extends UnicastRemoteObject implements ComputerAPI {

    private SpaceAPI space;

    Computer(SpaceAPI space) throws RemoteException {
        this.space = space;
    }

    public static void main(String[] args) throws Exception {
        SpaceAPI space = RMIUtils.connectToSpace(args[0]);
//        int cores = Runtime.getRuntime().availableProcessors();
        int cores = 1;
        for (int i = 0; i < cores; i++) {
            space.register(new Computer(space));
            System.out.println("Computer " + i + ": Ready.");
        }
        System.out.println("Computer.main: Ready.");
    }

    @Override
    public Result execute(Task task) throws RemoteException {
        long startTime = System.currentTimeMillis();
        Result result = task.execute();
        long endTime = System.currentTimeMillis() - startTime;
//        System.out.println("I runs tasks! In: " + endTime + "ms");
        result.setTaskRunTime(endTime);
        return result;
    }

    @Override
    public void exit() throws RemoteException {
        System.out.println("Computer.main: Goin' DOWN!.");
        System.exit(0);
    }
}
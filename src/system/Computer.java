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
 * @author karl
 */
class Computer extends UnicastRemoteObject implements ComputerAPI
{
    private SpaceAPI space;
    Computer(SpaceAPI space) throws RemoteException {
        this.space = space;
    }

    public static void main(String[] args) throws Exception
    {
        SpaceAPI space = RMIUtils.connectToSpace(args[0]);
        ComputerAPI computer = new Computer(space);
        space.register(computer);
        System.out.println("Computer.main: Ready.");
    }

    @Override
    public Result execute(Task task) throws RemoteException {
        long startTime = System.currentTimeMillis();
        Object obj = task.execute(space);
        long endTime = System.currentTimeMillis() - startTime;
        System.out.println("I runs tasks! In: " + endTime + "ms");
        return new Result(obj, endTime);
    }

    @Override
    public void exit() throws RemoteException {
        System.out.println("Computer.main: Goin' DOWN!.");
        System.exit(0);
    }
}
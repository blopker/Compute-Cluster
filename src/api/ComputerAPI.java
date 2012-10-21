package api;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * This represents the methods the client can call to make tasks happen.
 *
 * @author karl
 */
public interface ComputerAPI extends Remote {

    /**
     *  Supply a computer a task to complete.
     * @param task
     * @return Result 
     * @throws RemoteException
     */
    Result execute(Task task) throws RemoteException;

    /**
     *  Stop the computer from running.
     * @throws RemoteException
     */
    void exit() throws RemoteException;
}
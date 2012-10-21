package api;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 *
 * @author ninj0x
 */
public interface SpaceAPI extends Remote {

    /**
     * Name of the service the client will connect to.
     */
    public static String SERVICE_NAME = "ComputeSpace";
    /**
     * Port the computer will bind to.
     */
    public static int SERVICE_PORT = 1099;

    /**
     *  Call to add a task to the compute space.
     * @param task
     * @throws RemoteException
     */
    void put(Task task) throws RemoteException;

    /**
     *  Get a result from the compute space. If no results are avaliable, this
     * method blocks.
     * @return
     * @throws RemoteException
     */
    Result take() throws RemoteException;

    /**
     *  Kill the space and all connected computers. 
     * @throws RemoteException
     */
    void exit() throws RemoteException;

    /**
     *  Let the space know you are a computer and are ready to do work.
     * @param computer
     * @throws java.rmi.RemoteException
     */
    void register(ComputerAPI computer) throws java.rmi.RemoteException;
}

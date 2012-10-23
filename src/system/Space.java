package system;

import api.ComputerAPI;
import api.Result;
import api.SpaceAPI;
import api.Task;
import java.rmi.RMISecurityManager;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author ninj0x
 */
public class Space extends UnicastRemoteObject implements SpaceAPI {

    private final BlockingQueue<Task> tasks;
    private final BlockingQueue<Result> results;
    private final BlockingQueue<Result> unsorted;
    private final Set<ComputerAPI> computers;
    private final BlockingQueue<Task> waitingTasks;

    /**
     * Creates a compute space for computers ant clients to connect.
     *
     * @throws RemoteException
     */
    public Space() throws RemoteException {
        tasks = new LinkedBlockingQueue<Task>();
        results = new LinkedBlockingQueue<Result>();
        unsorted = new LinkedBlockingQueue<Result>();
        computers = Collections.synchronizedSet(new HashSet<ComputerAPI>());
        waitingTasks = new LinkedBlockingQueue<Task>();
        TaskSorter sorter = new TaskSorter();
        new Thread(sorter).start();
    }

    /**
     * Run to set up a compute space and space registry.
     *
     * @param args
     * @throws Exception
     */
    public static void main(String[] args) throws Exception {
        // construct & set a security manager (unnecessary in this case)
        System.setSecurityManager(new RMISecurityManager());

        // construct an rmiregistry within this JVM using the default port
        Registry registry = LocateRegistry.createRegistry(SpaceAPI.SERVICE_PORT);

        SpaceAPI space = new Space();

        registry.rebind(SpaceAPI.SERVICE_NAME, space);

        System.out.println("Space: Ready.");
    }

    @Override
    public void put(Task task) throws RemoteException {
        try {
//            System.out.println("put task! " + task.getID());
            if (task.isReady()) {
                tasks.put(task);
            } else {
//                System.out.println("adding waiting task!");
                waitingTasks.put(task);
            }
        } catch (InterruptedException ex) {
            Logger.getLogger(Space.class.getName()).log(Level.SEVERE, null, ex);
        }
        System.out.println("I has a task!");
    }

    @Override
    public Result take() throws RemoteException {
        try {
            return results.take();
        } catch (InterruptedException ex) {
            Logger.getLogger(Space.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    @Override
    public void exit() throws RemoteException {
        for (ComputerAPI computer : computers) {
            computer.exit();
        }
        System.exit(0);
    }

    @Override
    public void register(ComputerAPI computer) throws RemoteException {
        ComputerProxy proxy = new ComputerProxy(computer);
        computers.add(proxy);
        new Thread(proxy).start();
        System.out.println("I has a computer!");
    }
    
    class TaskSorter implements Runnable {

        @Override
        public void run() {
            while(true){
                try {
                    Result result = unsorted.take();
                    processResult(result);
                } catch (InterruptedException ex) {
                    Logger.getLogger(Space.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
        
        private void processResult(Result result) {
        if (result.getTaskReturnValue() != null) {
            if(waitingTasks.isEmpty()){
                try {
                    results.put(result);
                } catch (InterruptedException ex) {
                    Logger.getLogger(Space.class.getName()).log(Level.SEVERE, null, ex);
                }
            } else{
                distributeArgument((Task)result.getTaskReturnValue());
            }
        }

    }

    private void distributeArgument(Task taskReturnValue) {
        for (Iterator<Task> it = waitingTasks.iterator(); it.hasNext();) {
            Task task = it.next();
            task.addArgument(taskReturnValue);
            if (task.isReady()) {
                System.out.println("task is ready!");
                it.remove();
                try {
                    tasks.put(task);
                } catch (InterruptedException ex) {
                    Logger.getLogger(Space.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            
        }
    }
    
    }

    class ComputerProxy implements ComputerAPI, Runnable {

        private final ComputerAPI realComputer;

        public ComputerProxy(ComputerAPI computer) {
            realComputer = computer;
        }

        @Override
        public Result execute(Task task) throws RemoteException {
            return realComputer.execute(task);
        }

        @Override
        public void exit() throws RemoteException {
            realComputer.exit();
        }

        @Override
        public void run() {
            while (true) {
                try {
                    Task task = tasks.take();
                    Result result;
                    try {
                        result = this.execute(task);
                        unsorted.put(result);
                    } catch (RemoteException ex) {
                        computerDied(task);
                        return;
                    }
                } catch (InterruptedException ex) {
                    Logger.getLogger(Space.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }

        private void computerDied(Task task) {
            System.out.println("OH NOES! Computer dead.");
            computers.remove(this);
            try {
                // Put the task we had back in the space.
                tasks.put(task);
            } catch (InterruptedException ex) {
                Logger.getLogger(Space.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
}

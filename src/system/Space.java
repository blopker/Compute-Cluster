package system;

import api.ComputerAPI;
import api.Result;
import api.Shared;
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
import java.util.List;
import java.util.Set;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author ninj0x
 */
public class Space extends UnicastRemoteObject implements SpaceAPI {

    private final BlockingDeque<Task> tasks;
    private final BlockingQueue<Result> results;
    private final BlockingQueue<Result> unsorted;
    private final Set<ComputerAPI> computers;
    private final BlockingQueue<Task> waitingTasks;
    private Shared shared;

    /**
     * Creates a compute space for computers ant clients to connect.
     *
     * @throws RemoteException
     */
    public Space() throws RemoteException {
        tasks = new LinkedBlockingDeque<Task>();
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

        final SpaceAPI space = new Space();

        registry.rebind(SpaceAPI.SERVICE_NAME, space);

        System.out.println("Space: Ready.");

        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                try {
                    space.exit();
                } catch (RemoteException ex) {
                    Logger.getLogger(Space.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });
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
//        System.out.println("I has a task!");
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
    }

    @Override
    public void register(ComputerAPI computer) throws RemoteException {
        ComputerProxy proxy = new ComputerProxy(computer);
        computers.add(proxy);
        new Thread(proxy).start();
        System.out.println("I has a computer!");
    }

    private synchronized void setShared(Shared shared) {
        if (shared == null) {
            return;
        }
        if (this.shared == null || shared.isBetterThan(this.shared)) {
            this.shared = shared;
            System.out.println("new shared: " + this.shared.getShared());
        }
    }

    class TaskSorter implements Runnable {

        @Override
        public void run() {
            while (true) {
                try {
                    Result result = unsorted.take();
                    processResult(result);
                } catch (InterruptedException ex) {
                    Logger.getLogger(Space.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }

        private <T> void processResult(Result<T> result) {

            if (result.getResult() != null) {
                // no wiating tasks, so this must be the final answer
                if (waitingTasks.isEmpty()) {
                    pushFinalResult(result);
                } else {
                    distributeArgument(result);
                }
            }

            if (result.getNewTasks() != null) {
                addNewTasks(result.getNewTasks());
            }

            if (result.getShared() != null) {
                setShared(result.getShared());
            }

        }

        private <T> void pushFinalResult(Result<T> result) {
            try {
                shared = null;
                results.put(result);
            } catch (InterruptedException ex) {
                Logger.getLogger(Space.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        private void distributeArgument(Result taskReturnValue) {
            for (Iterator<Task> it = waitingTasks.iterator(); it.hasNext();) {
                Task task = it.next();
                task.addResult(taskReturnValue);
                if (task.isReady()) {
//                    System.out.println("task is ready!");
                    it.remove();
                    try {
                        tasks.putLast(task);
                    } catch (InterruptedException ex) {
                        Logger.getLogger(Space.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }

            }
        }

        private void addNewTasks(List<? extends Task> tasks) {
            for (Task task : tasks) {
                try {
                    put(task);
                } catch (RemoteException ex) {
                    Logger.getLogger(Space.class.getName()).log(Level.SEVERE, null, ex);
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
                    Task task = tasks.takeLast();
                    task.setShared(shared);
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

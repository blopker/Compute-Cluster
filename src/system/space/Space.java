package system.space;

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
import java.util.Set;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
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
    private final static Set<ComputerAPI> computers = Collections.synchronizedSet(new HashSet<ComputerAPI>());
    private final ConcurrentHashMap<String, Task> waitingTasks;
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
        waitingTasks = new ConcurrentHashMap<String, Task>();
        new Thread(new ResultSorter(this)).start();
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
                    for (ComputerAPI computer : computers) {
                        computer.exit();
                    }
                } catch (RemoteException ex) {
                    Logger.getLogger(Space.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });
    }

    @Override
    public void put(Task task) throws RemoteException {
        try {
            shared = null;
            addTask(task);
        } catch (InterruptedException ex) {
            Logger.getLogger(Space.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public Result take() throws RemoteException {
        try {
            Result ans = results.take();
            System.out.println("tasks: " + tasks.size());
            System.out.println("results: " + results.size());
            System.out.println("unsorted: " + unsorted.size());
            System.out.println("computers: " + computers.size());
            System.out.println("waitingTasks: " + waitingTasks.size());
            return ans;
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
        ComputerProxy proxy = new ComputerProxy(computer, this);
        computers.add(proxy);
        new Thread(proxy).start();
        System.out.println("I has a computer!");
    }

    protected synchronized void setShared(Shared shared) {
        if (shared == null) {
            return;
        }
        if (this.shared == null || shared.isBetterThan(this.shared)) {
            this.shared = shared;
            System.out.println("new shared: " + this.shared.getShared());
        }
    }

    protected Shared getShared() {
        return shared;
    }

    protected Task getReadyTask() throws InterruptedException {
        return tasks.takeLast();
    }

    protected void addTask(Task task) throws InterruptedException {
        if (task.isReady()) {
            tasks.putLast(task);
        } else {
            waitingTasks.put(task.getID(), task);
        }
    }

    protected void removeComputer(ComputerAPI computer) {
        computers.remove(computer);
    }

    protected void addResult(Result result) throws InterruptedException {
        unsorted.put(result);
    }

    protected Result getResult() throws InterruptedException {
        return unsorted.take();
    }

    protected void setFinalResult(Result result) throws InterruptedException {
        results.put(result);
    }

    protected void addArgument(Result result) {
        if (result.getID() == null || !waitingTasks.containsKey(result.getID())) {
            return;
        }

        Task task = waitingTasks.get(result.getID());
        task.addResult(result);
        if (task.isReady()) {
            waitingTasks.remove(task.getID());
            try {
                addTask(task);
            } catch (InterruptedException ex) {
                Logger.getLogger(Space.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    protected int getWaitingTaskCount() {
        return waitingTasks.size();
    }
}

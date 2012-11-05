/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package system.space;

import api.ComputerAPI;
import api.Result;
import api.Task;
import java.rmi.RemoteException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author ninj0x
 */
class ComputerProxy implements ComputerAPI, Runnable {

    private final ComputerAPI realComputer;
    private final Space space;

    protected ComputerProxy(ComputerAPI computer, Space space) {
        realComputer = computer;
        this.space = space;
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
                Task task = space.getReadyTask();
                task.setShared(space.getShared());
                Result result;
                try {
                    result = this.execute(task);
                    space.addResult(result);
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
        space.removeComputer(this);
        try {
            // Put the task we had back in the space.
            space.addTask(task);
        } catch (InterruptedException ex) {
            Logger.getLogger(Space.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}

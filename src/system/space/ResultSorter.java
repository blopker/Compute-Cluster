/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package system.space;

import api.Result;
import api.Task;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author ninj0x
 */
class ResultSorter implements Runnable {

    private final Space space;

    protected ResultSorter(Space space) {
        this.space = space;
    }

    @Override
    public void run() {
        while (true) {
            try {
                Result result = space.getResult();
                processResult(result);
            } catch (InterruptedException ex) {
                Logger.getLogger(Space.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    private <T> void processResult(Result<T> result) {

        if (result.getResult() != null) {
            // no waiting tasks, so this must be the final answer
            if (space.getWaitingTaskCount() == 0) {
                pushFinalResult(result);
            } else { // or not
                space.addArgument(result);
            }
        }

        if (result.getNewTasks() != null) {
            addNewTasks(result.getNewTasks());
        }

        if (result.getShared() != null) {
            space.setShared(result.getShared());
        }

    }

    private <T> void pushFinalResult(Result<T> result) {
        try {
            space.setFinalResult(result);
        } catch (InterruptedException ex) {
            Logger.getLogger(Space.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void addNewTasks(List<? extends Task> tasks) {
        for (Task task : tasks) {
            try {
                space.addTask(task);
            } catch (InterruptedException ex) {
                Logger.getLogger(ResultSorter.class.getName()).log(Level.SEVERE, null, ex);
            }

        }
    }
}

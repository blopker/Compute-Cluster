/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package tasks;

import api.Result;
import api.Shared;
import api.Task;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 *
 * @author ninj0x
 */
public class FibTask extends Task implements Serializable {
    private String childID = null;
    private final int n;
    private Integer value = 0;
    private final String id;
    private final int LIMIT = 1;

    public FibTask(int n) {
        this.id = UUID.randomUUID().toString();
        this.n = n;
    }

    @Override
    public boolean isReady() {
        return true;
    }

    @Override
    public String getID() {
        return this.id;
    }

    @Override
    public Result execute() {
        if (n <= LIMIT) {
            value = fib(n);
            return new Result<Integer>(getChildID(), value, null, null);
        } else {
            Task minTask = new AddTask(2);
            minTask.setChildID(this.getChildID());
            FibTask one = new FibTask(n - 1);
            FibTask two = new FibTask(n - 2);
            one.setChildID(minTask.getID());
            two.setChildID(minTask.getID());
            List<Task> tasks = new ArrayList<Task>();
            tasks.add(minTask);
            tasks.add(one);
            tasks.add(two);
            return new Result<Integer>(getID(), null, tasks, null);
        }
    }
    
    private int fib(int n){
        if(n == 0 || n == 1){
            return n;
        }
        return fib(n-1) + fib(n-2);
    }

    /**
     * Not used.
     *
     * @param argument
     */
    @Override
    public void addResult(Result argument) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void setShared(Shared shared) {
        return;
    }

    @Override
    public String getChildID() {
        return this.childID;
    }

    @Override
    public void setChildID(String child_id) {
        this.childID = child_id;
    }

    @Override
    public boolean isLocalTask() {
        return n > LIMIT;
    }
}

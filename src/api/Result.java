package api;

import java.io.Serializable;
import java.util.List;

/**
 *
 * @author klopker
 */
public class Result<T> implements Serializable {
    private List<? extends Task> newTasks;
    private T taskReturnValue;
    private long taskRunTime;
    private Shared shared;
    private String id;
    
    /**
     * The result package that is sent to the system.
     * Use null if the task doesn't have some of the values.
     * @param id Dependancy ID
     * @param taskReturnValue Task result value
     * @param newTasks New Tasks to be added to the task queue
     * @param shared The shared object to be updated.
     */
    public Result(String id, T taskReturnValue, List<? extends Task> newTasks, Shared shared) {
        this.taskReturnValue = taskReturnValue;
        this.newTasks = newTasks;
        this.id = id;
        this.shared = shared;
    }

    /**
     *  This must be cast to the correct result type for the task.
     * @return T
     */
    public T getResult() {
        return taskReturnValue;
    }

    
    public List<? extends Task> getNewTasks(){
        return newTasks;
    }
    
    public Shared getShared(){
        return this.shared;
    }
    
    /**
     *  Set the computation time as seen by the computer.
     */
    public void setTaskRunTime(long taskTime) {
        this.taskRunTime = taskTime;
    }
    
    /**
     *  Get the computation time as seen by the computer.
     * @return Time in milliseconds.
     */
    public long getTaskRunTime() {
        return taskRunTime;
    }

    public String getID() {
        return this.id;
    }
}

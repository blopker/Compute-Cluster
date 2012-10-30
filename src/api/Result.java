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
    private String id;
    
    /**
     *  An object that contains a task result and the task runtime.
     * Use null if the task doesn't have some of the values.
     * @param taskReturnValue
     * @param taskRunTime
     */
    public Result(String id, T taskReturnValue, List<? extends Task> newTasks) {
        this.taskReturnValue = taskReturnValue;
        this.newTasks = newTasks;
        this.id = id;
    }

    /**
     *  This must be cast to the correct result type for the task.
     * @return Object
     */
    public T getResult() {
        return taskReturnValue;
    }

    
    public List<? extends Task> getNewTasks(){
        return newTasks;
    }
    
    /**
     *  Set the computation time as seen by the computer.
     * @return Time in milliseconds.
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

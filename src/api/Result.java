package api;

import java.io.Serializable;

/**
 *
 * @author klopker
 */
public class Result implements Serializable {

    private Object taskReturnValue;
    private long taskRunTime;

    /**
     *  An object that contains a task result and the task runtime.
     * A task result must be cast from Object to the required type.
     * @param taskReturnValue
     * @param taskRunTime
     */
    public Result(Object taskReturnValue, long taskRunTime) {
        assert taskReturnValue != null;
        assert taskRunTime >= 0;
        this.taskReturnValue = taskReturnValue;
        this.taskRunTime = taskRunTime;
    }

    /**
     *  This must be cast to the correct result type for the task.
     * @return Object
     */
    public Object getTaskReturnValue() {
        return taskReturnValue;
    }

    /**
     *  Get the computation time as seen by the computer.
     * @return Time in milliseconds.
     */
    public long getTaskRunTime() {
        return taskRunTime;
    }
}

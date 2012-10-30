package api;

/**
 *Represents a task to be completed by a computer.
 * @author klopker
 */
public abstract class Task {
   
    /**
     *  When called, the task should complete its work and return the results.
     * @return T
     */
    public abstract <T> Result<T> execute();
    public abstract void addResult(Result argument);
    public abstract boolean isReady();
    public abstract Object getValue();
    /**
     * @return A unique string for the task. Usually a UUID.
     */
    public abstract String getID();
}

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
    
    /**
     * Used by the system to add result dependancies to waiting tasks
     * @param argument 
     */
    public abstract void addResult(Result argument);
    
    /**
     * True if the task is ready to be sent to a computer.
     * @return boolean
     */
    public abstract boolean isReady();
    
    /**
     * Used by the system to package the shared object before sent out for computation
     * @param shared 
     */
    public abstract void setShared(Shared shared);
    /**
     * @return A unique string for the task. Usually a UUID.
     */
    public abstract String getID();
}

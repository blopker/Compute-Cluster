package api;

/**
 *Represents a task to be completed by a computer.
 * @param <T> 
 * @author klopker
 */
public abstract class Task<T> {
   
    /**
     *  When called, the task should complete its work and return the results.
     * @return T
     */
    public abstract T execute(SpaceAPI space);
    public abstract void addArgument(Task argument);
    public abstract boolean isReady();
    public abstract Object getValue();
    /**
     * @return A unique string for the task. Usually a UUID.
     */
    public abstract String getID();
}

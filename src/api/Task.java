package api;

import java.util.UUID;

/**
 *Represents a task to be completed by a computer.
 * @param <T> 
 * @author klopker
 */
public abstract class Task<T> {
   
    /**
     *  When called, the task should complete its work and return the results.
     * @return
     */
    public abstract T execute(SpaceAPI space);
    public abstract void addArgument(Task argument);
    public abstract boolean isReady();
    public abstract Object getValue();
    public abstract String getID();
}

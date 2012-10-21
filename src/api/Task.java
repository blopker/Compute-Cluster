package api;

/**
 *Represents a task to be completed by a computer.
 * @param <T> 
 * @author klopker
 */
public interface Task<T> {
    
    /**
     *  When called, the task should complete its work and return the results.
     * @return
     */
    public T execute();
}

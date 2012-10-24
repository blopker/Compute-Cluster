/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package tasks;

import api.SpaceAPI;
import api.Task;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 *
 * @author ninj0x
 */
public class AddTask extends Task<AddTask> implements Serializable{
    private final String parentID;
    private final Set<String> joinSet;
    private final List<Integer> arguments;
    private Integer ans;
    
    public AddTask(Task parent, Task... adds) {
        parentID = parent.getID();
        joinSet = new HashSet<String>();
        arguments = new ArrayList<Integer>();
        for (Task addable : adds) {
            joinSet.add(addable.getID().toString());
//            System.out.println("task: " + addable.getID());
        }
    }    
    
    @Override
    public AddTask execute(SpaceAPI space) {
        
        ans = 0;
        for (Integer arg : arguments) {
            ans += (int) arg.doubleValue();
        }
        System.out.println("execute add = " + ans);
        return this;
    }
    
    @Override
    public boolean isReady(){
        return joinSet.isEmpty();
    }

    /**
     * @return Integer with the sum of the arguments' values.
     */
    @Override
    public Object getValue() {
        return ans;
    }

    /**
     * Add a FibTask to the required arguments list.
     * @param argument 
     */
    @Override
    public void addArgument(Task argument) {
        if (joinSet.remove(argument.getID().toString())) {
            arguments.add((Integer)argument.getValue());
        }
    }
    
    @Override
    public String getID(){
        return this.parentID;
    }
}

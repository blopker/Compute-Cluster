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
    private final List<Double> arguments;
    private Integer ans;
    
    public AddTask(Task parent, Task... adds) {
        parentID = parent.getID();
        joinSet = new HashSet<>();
        arguments = new ArrayList<>();
        for (Task addable : adds) {
            joinSet.add(addable.getID().toString());
//            System.out.println("task: " + addable.getID());
        }
    }    
    
    @Override
    public AddTask execute(SpaceAPI space) {
        
        ans = 0;
        for (Double arg : arguments) {
            ans += (int) arg.doubleValue();
        }
//        System.out.println("execute add = " + ans);
        return this;
    }
    
    @Override
    public boolean isReady(){
        return joinSet.isEmpty();
    }

    @Override
    public double getValue() {
        return ans;
    }

    @Override
    public void addArgument(Task argument) {
//        System.out.println("trying add " + argument.getID());
        if (joinSet.remove(argument.getID().toString())) {
            System.out.println("added:" + argument.getValue());
            arguments.add(argument.getValue());
        }
    }
    
    public String getID(){
        return this.parentID;
    }
}

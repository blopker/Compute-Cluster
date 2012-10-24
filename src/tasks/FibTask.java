/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package tasks;

import api.SpaceAPI;
import api.Task;
import java.io.Serializable;
import java.rmi.RemoteException;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author ninj0x
 */
public class FibTask extends Task<FibTask> implements Serializable {
    private final int n;
    private Integer value = 0;
    private final String id;
    public FibTask(int n) {
        this.id = UUID.randomUUID().toString();
        this.n = n;
    }
    
    @Override
    public FibTask execute(SpaceAPI space) {
        System.out.println("execute fib: " + n);
        if (n <= 2) {
            value = (n == 0)?0:1;
            return this;
        } else {
            FibTask one = new FibTask(n - 1);
            FibTask two = new FibTask(n - 2);
//            System.out.println("made: " + (n-1) + " UUID: " + one.getID());
//            System.out.println("made: " + (n-2) + " UUID: " + two.getID());
            try {
                space.put(new AddTask(this, one, two));
                space.put(one);
                space.put(two);
            } catch (RemoteException ex) {
                Logger.getLogger(FibTask.class.getName()).log(Level.SEVERE, null, ex);
            }
            return null;
        }
    }

    /**
     * @return Integer answer of F(n).
     */
    @Override
    public Object getValue() {
        return value;
    }

    /**
     * Not used.
     * @param argument 
     */
    @Override
    public void addArgument(Task argument) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean isReady() {
        return true;
    }
    
    @Override
    public String getID(){
        return this.id;
    }
    
}

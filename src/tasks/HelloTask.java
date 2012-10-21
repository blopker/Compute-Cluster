/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package tasks;

import api.Task;
import java.io.Serializable;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * The default task, used for testing only. Sleeps for 1 second before running.
 * @author ninj0x
 */
public class HelloTask implements Task<String>, Serializable{

    @Override
    public String execute() {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException ex) {
            Logger.getLogger(HelloTask.class.getName()).log(Level.SEVERE, null, ex);
        }
        return "Hello! I have come from space to use your bathing facilities.";
    }
    
}

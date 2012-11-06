/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package tasks;

import api.Result;
import api.Shared;
import api.SpaceAPI;
import api.Task;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

/**
 *
 * @author ninj0x
 */
public class AddTask extends Task implements Serializable {

    private String childID;
    private String ID;
    private int joinCount;
    private final List<Integer> arguments;
    private Integer ans;

    public AddTask(int argumentCount) {
        this.ID = UUID.randomUUID().toString();
        joinCount = argumentCount;
        arguments = new ArrayList<Integer>();
    }

    @Override
    public String getID() {
        return this.ID;
    }

    @Override
    public Result execute() {
        ans = 0;
        for (Integer arg : arguments) {
            ans += (int) arg.doubleValue();
        }
        return new Result<Integer>(this.getChildID(), ans, null, null);
    }

    @Override
    public void addResult(Result argument) {
        if (joinCount > 0) {
            joinCount--;
            arguments.add((Integer) argument.getResult());
        }
    }

    @Override
    public void setShared(Shared shared) {
        return;
    }

    @Override
    public String getChildID() {
        return this.childID;
    }

    @Override
    public void setChildID(String child_id) {
        this.childID = child_id;
    }

    @Override
    public boolean isReady() {
        return joinCount <= 0;
    }

    @Override
    public boolean isLocalTask() {
        return true;
    }
}

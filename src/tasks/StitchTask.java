/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package tasks;

import api.MandelResult;
import api.Result;
import api.Shared;
import api.Task;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 *
 * @author ninj0x
 */
public class StitchTask extends Task implements Serializable {

    private int joinCount;
    List<MandelResult> results = new ArrayList<MandelResult>();
    
    private final double X;
    private final double Y;
    private final double EDGE;
    private final int N_PIXELS;
    private final String ID;
    private String childID;
    
    public StitchTask(double x_whole, double y_whole, double edge, int nPixels, int argument_number) {
        this.ID = UUID.randomUUID().toString();
        this.joinCount = argument_number;
        this.EDGE = edge;
        this.N_PIXELS = nPixels;
        this.X = x_whole;
        this.Y = y_whole;
    }

    @Override
    public Result execute() {
        System.out.println("Stitch");
        MandelResult mand = new MandelResult(X, Y, combineResults(results));
        return new Result<MandelResult>(this.getChildID(), mand, null, null);
    }

    private int[][] combineResults(List<MandelResult> parts) {
        int[][] result = new int[N_PIXELS][N_PIXELS];
        for (MandelResult is : parts) {
            result = instertPart(is.getData(), is.getX(), is.getY(), result);
        }
        return result;
    }

    private int[][] instertPart(int[][] part, double x, double y, int[][] result) {
        int x_pix = map(x, X, X + EDGE, 1, N_PIXELS);
        int y_pix = map(y, Y, Y + EDGE, 1, N_PIXELS);
        for (int i = 0; i < part.length; i++) {
            System.arraycopy(part[i], 0, result[x_pix + i], y_pix, part.length);
        }
        return result;
    }

    private static int map(double x, double in_min, double in_max, double out_min, double out_max) {
        return (int) Math.floor((x - in_min) * (out_max - out_min) / (in_max - in_min) + out_min);
    }

    @Override
    public void addResult(Result argument) {
        if (joinCount > 0) {
            joinCount--;
            results.add((MandelResult) argument.getResult());
        }
    }

    @Override
    public boolean isReady() {
        return joinCount <= 0;
    }

    @Override
    public void setShared(Shared shared) {
        return;
    }

    @Override
    public String getID() {
        return this.ID;
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
    public boolean isLocalTask() {
        return false;
    }
}

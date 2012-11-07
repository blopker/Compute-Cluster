package api;

import java.io.Serializable;

public class MandelResult implements Serializable {


    private final double x, y;
    private int[][] result;
    
    public MandelResult(double x_part, double y_part, int[][] result) {
        this.x = x_part;
        this.y = y_part;

        this.result = result;
    }
    
    public int[][] getData() {
        return result;
    }
    
    public double getX() {
        return x;
    }
    
    public double getY() {
        return y;
    }
}

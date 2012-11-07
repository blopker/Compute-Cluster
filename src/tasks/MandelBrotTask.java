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
 * This task generates an int matrix with the results of a Mandelbrot set.
 *
 * @author klopker
 */
public class MandelBrotTask extends Task implements Serializable {

    private final double x, y, edge;
    private final int n, max_k;
    private int[][] result;
    private final String id;
    private String childID = null;
    private static final int TASKS = 8;

    /**
     * This task helps produce a visualization of the some part of the
     * Mandelbrot set.
     *
     * @param x Lower x of the complex plane
     * @param y Lower y of the complex plane
     * @param edge Represents the edge length of a square in the complex plane,
     * whose sides are parallel to the axes.
     * @param n an int such that the square region of the complex plane is
     * subdivided into n X n squares, each of which is visualized by 1 pixel.
     * @param max_k an int which is the iteration limit: It defines when the
     * representative point of a region is considered to be in the Mandelbrot
     * set.
     */
    public MandelBrotTask(double x, double y, double edge, int n, int max_k) {
        this.id = UUID.randomUUID().toString();
        this.x = x;
        this.y = y;
        this.edge = edge;
        this.n = n;
        this.max_k = max_k;
    }

    /**
     * Generates the Mandelbrot set and returns a matrix with ints from 0 to
     * max_k.
     *
     * @return MandelbrotSetTask with the result filled out: A matrix where
     * int[i][j] = k in the Mandelbrot set.
     */
    @Override
    public Result execute() {
        
        if (this.childID == null) {
            System.out.println("split");
            return new Result<MandelResult>(this.getChildID(), null, splitTask(this), null);
        }
        
        System.out.println("calc");
        int[][] grid = new int[n][n];
        double step = edge / n;

        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                double posx = i * step;
                double posy = j * step;
                grid[i][j] = calcPixel(x + posx, y + posy);
            }
        }
        result = grid;
        MandelResult mand = new MandelResult(x, y, result);
        return new Result<MandelResult>(this.getChildID(), mand, null, null);
    }

    private int calcPixel(double cornerX, double cornerY) {
        int k = 0;
        double j = cornerX;
        double i = cornerY;
        while (Math.pow(j, 2) + Math.pow(i, 2) < 4 && k < max_k) {
            double tempj = j;
            j = j * j - i * i + cornerX;
            i = 2 * tempj * i + cornerY;
            k++;
        }
        return k;
    }
    
    private List<Task> splitTask(MandelBrotTask whole) {
        List<Task> tasks = new ArrayList<Task>();
        StitchTask stitch = new StitchTask(x, y, edge, n, TASKS*TASKS);
        tasks.add(stitch);
        System.out.println(stitch.getID());
        double step = edge / TASKS;
        for (int i = 0; i < TASKS; i++) {
            for (int j = 0; j < TASKS; j++) {
                MandelBrotTask task = new MandelBrotTask((i*step)+x, (j*step)+y, step, (int) n / TASKS, max_k);
                task.setChildID(stitch.getID());
                tasks.add(task);
            }
        }
        return tasks;
    }

    @Override
    public void addResult(Result argument) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean isReady() {
        return true;
    }

    @Override
    public void setShared(Shared shared) {
        return;
    }

    @Override
    public String getID() {
        return this.id;
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

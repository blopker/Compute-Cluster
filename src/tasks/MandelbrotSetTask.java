package tasks;

import api.Task;
import java.io.Serializable;

/**
 * This task generates an int matrix with the results of a Mandelbrot set.
 *
 * @author klopker
 */
public class MandelbrotSetTask implements Task<MandelbrotSetTask>, Serializable {

    private final double x, y, edge;
    private final int n, max_k;
    private int[][] result;

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
    public MandelbrotSetTask(double x, double y, double edge, int n, int max_k) {
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
    public MandelbrotSetTask execute() {
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
        return this;
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

    /**
     *  Get the result of this task, could be null.
     * @return
     */
    public int[][] getResult() {
        return result;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public double getEdge() {
        return edge;
    }

    /**
     * Get the number of pixels in a edge.
     * @return
     */
    public int getN() {
        return n;
    }

    public int getMax_k() {
        return max_k;
    }
}

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package client;

import api.RMIUtils;
import api.Result;
import api.SpaceAPI;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.rmi.RemoteException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import tasks.MandelbrotSetTask;

/**
 * This client sends several Mandelbrot set tasks to a computer and times them.
 * The final result is stitched together from the different tasks and displayed.
 *
 * @author karl
 */
public class MandelClient {

    private static final double X = -0.7510975859375;
    private static final double Y = 0.1315680625;
    private static final double EDGE = 0.01611;
    private static final int N_PIXELS = 1024;
    private static final int ITERATION_LIMIT = 512;
    private static final int TASKS = 8;

    /**
     * Sends several Mandelbrot set tasks to a computer and times them.
     *
     * @param args
     */
    public static void main(String[] args) {
        SpaceAPI space = RMIUtils.connectToSpace(args[0]);

        int[][] mandel;
        long startTime, endTime;
        startTime = System.currentTimeMillis();
        mandel = run(space);
        endTime = System.currentTimeMillis() - startTime;
        System.out.println("Done! Total Time: " + endTime);
        display(mandel);
    }

    private static int[][] run(SpaceAPI space) {
        try {

            // Send out the tasks
            MandelbrotSetTask[] tasks = splitTask(new MandelbrotSetTask(X, Y, EDGE, N_PIXELS, ITERATION_LIMIT));
            for (MandelbrotSetTask mandelbrotSetTask : tasks) {
                space.put(mandelbrotSetTask);
            }

            // Get the results
            long avg = 0;
            for (int i = 0; i < tasks.length; i++) {
                Result result = space.take();
                tasks[i] = (MandelbrotSetTask) result.getTaskReturnValue();
                avg += result.getTaskRunTime();
                System.out.println("Sub Task Done! Time: " + result.getTaskRunTime());
            }
            System.out.println("Tasks Done! Computation average time: " + avg / tasks.length);

            return combineResults(tasks);
        } catch (RemoteException ex) {
            Logger.getLogger(MandelClient.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    private static MandelbrotSetTask[] splitTask(MandelbrotSetTask whole) {
        MandelbrotSetTask[] tasks = new MandelbrotSetTask[TASKS * TASKS];
        double step = EDGE / TASKS;
        for (int i = 0; i < TASKS; i++) {
            for (int j = 0; j < TASKS; j++) {
                tasks[TASKS * j + i] = new MandelbrotSetTask((i*step)+X, (j*step)+Y, step, (int) N_PIXELS / TASKS, ITERATION_LIMIT);
            }
        }
        return tasks;
    }

    private static int[][] combineResults(MandelbrotSetTask[] tasks) {
        int[][] result = new int[N_PIXELS][N_PIXELS];
        MandelbrotSetTask task;
        int[][] part;
        for (int i = 0; i < tasks.length; i++) {
            task = tasks[i];
            part = task.getResult();
            result = instertPart(part, task.getX(), task.getY(), result);
        }
        return result;
    }

    private static int[][] instertPart(int[][] part, double x, double y, int[][] result) {
        int x_pix = map(x, X, X+EDGE, 1, N_PIXELS);
        int y_pix = map(y, Y, Y+EDGE, 1, N_PIXELS);
        for (int i = 0; i < part.length; i++) {
            System.arraycopy(part[i], 0, result[x_pix+i], y_pix, part.length);
        }
        return result;
    }

    private static int map(double x, double in_min, double in_max, double out_min, double out_max) {
        return (int)Math.floor((x - in_min) * (out_max - out_min) / (in_max - in_min) + out_min);
    }

    private static void display(int[][] counts) {
        JLabel mandelbrotLabel = displayMandelbrotSetTaskReturnValue(counts);

        // display JLabels: graphic images
        JFrame frame = new JFrame("Result Visualizations");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        Container container = frame.getContentPane();
        container.setLayout(new BorderLayout());
        container.add(new JScrollPane(mandelbrotLabel), BorderLayout.WEST);
        frame.pack();
        frame.setVisible(true);
    }

    private static JLabel displayMandelbrotSetTaskReturnValue(int[][] counts) {
        Image image = new BufferedImage(N_PIXELS, N_PIXELS, BufferedImage.TYPE_INT_ARGB);
        Graphics graphics = image.getGraphics();
        for (int i = 0; i < counts.length; i++) {
            for (int j = 0; j < counts.length; j++) {
                graphics.setColor(getColor(counts[i][j]));
                graphics.fillRect(i, counts.length - 1 - j, 1, 1);
            }
        }
        ImageIcon imageIcon = new ImageIcon(image);
        return new JLabel(imageIcon);
    }

    private static Color getColor(int i) {
        if (i == ITERATION_LIMIT) {
            return Color.BLACK;
        }
        int c = i * 255 / ITERATION_LIMIT;
        return new Color(c, c, c);
    }
}

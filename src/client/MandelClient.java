/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package client;

import api.MandelResult;
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
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import tasks.MandelBrotTask;

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
    private static final int ITERATION_LIMIT = 4096;

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
            space.put(new MandelBrotTask(X, Y, EDGE, N_PIXELS, ITERATION_LIMIT));
            Result<MandelResult> result = space.take();
            return result.getResult().getData();
        } catch (RemoteException ex) {
            Logger.getLogger(MandelClient.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
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

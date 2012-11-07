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
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import tasks.TSPTask;

/**
 *
 * @author ninj0x
 */
public class TSPClient {
private static int N_PIXELS = 250;

  private static double[][] cities = 
{
	{ 1, 1 },
	{ 8, 1 },
	{ 8, 8 },
	{ 1, 8 },
	{ 2, 2 },
	{ 7, 2 },
	{ 7, 7 },
	{ 2, 7 },
	{ 3, 3 },
	{ 6, 3 },
	{ 6, 6 },
	{ 3, 6 },
	{ 4, 4 },
	{ 5, 4 },
	{ 5, 5 }//,
//	{ 4, 5 }
};

    public static void main(String[] args) {
        SpaceAPI space = RMIUtils.connectToSpace(args[0]);

        List<Integer> tour;
        long startTime, endTime;
        startTime = System.currentTimeMillis();
        tour = run(space);
        endTime = System.currentTimeMillis() - startTime;
        System.out.println("Total Time: " + endTime);
        display(cities, tour.toArray(new Integer[tour.size()]));
    }

    private static List<Integer> run(SpaceAPI space) {
        try {
            // Send out the tasks
            TSPTask task = new TSPTask(cities);
            space.put(task);

            // Get the results
            Result<List<Integer>> result = space.take();
            return result.getResult();

        } catch (RemoteException ex) {
            Logger.getLogger(TSPClient.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public static void display(double[][] cities, Integer[] tour) {
        JLabel euclideanTspLabel = displayEuclideanTspTaskReturnValue(cities, tour);

        // display JLabels: graphic images
        JFrame frame = new JFrame("Result Visualizations");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        Container container = frame.getContentPane();
        container.setLayout(new BorderLayout());
        container.add(new JScrollPane(euclideanTspLabel), BorderLayout.EAST);
        frame.pack();
        frame.setVisible(true);
    }

    private static JLabel displayEuclideanTspTaskReturnValue(double[][] cities, Integer[] tour) {
        // display the graph graphically, as it were
        // get minX, maxX, minY, maxY, assuming they 0.0 <= mins
        double minX = cities[0][0], maxX = cities[0][0];
        double minY = cities[0][1], maxY = cities[0][1];
        for (int i = 0; i < cities.length; i++) {
            if (cities[i][0] < minX) {
                minX = cities[i][0];
            }
            if (cities[i][0] > maxX) {
                maxX = cities[i][0];
            }
            if (cities[i][1] < minY) {
                minY = cities[i][1];
            }
            if (cities[i][1] > maxY) {
                maxY = cities[i][1];
            }
        }

        // scale points to fit in unit square
        double side = Math.max(maxX - minX, maxY - minY);
        double[][] scaledCities = new double[cities.length][2];
        for (int i = 0; i < cities.length; i++) {
            scaledCities[i][0] = (cities[i][0] - minX) / side;
            scaledCities[i][1] = (cities[i][1] - minY) / side;
        }

        Image image = new BufferedImage(N_PIXELS, N_PIXELS, BufferedImage.TYPE_INT_ARGB);
        Graphics graphics = image.getGraphics();

        int margin = 10;
        int field = N_PIXELS - 2 * margin;
        // draw edges
        graphics.setColor(Color.BLUE);
        int x1, y1, x2, y2;
        int city1 = tour[0], city2;
        x1 = margin + (int) (scaledCities[city1][0] * field);
        y1 = margin + (int) (scaledCities[city1][1] * field);
        for (int i = 1; i < cities.length; i++) {
            city2 = tour[i];
            x2 = margin + (int) (scaledCities[city2][0] * field);
            y2 = margin + (int) (scaledCities[city2][1] * field);
            graphics.drawLine(x1, y1, x2, y2);
            x1 = x2;
            y1 = y2;
        }
        city2 = tour[0];
        x2 = margin + (int) (scaledCities[city2][0] * field);
        y2 = margin + (int) (scaledCities[city2][1] * field);
        graphics.drawLine(x1, y1, x2, y2);

        // draw vertices
        int VERTEX_DIAMETER = 6;
        graphics.setColor(Color.RED);
        for (int i = 0; i < cities.length; i++) {
            int x = margin + (int) (scaledCities[i][0] * field);
            int y = margin + (int) (scaledCities[i][1] * field);
            graphics.fillOval(x - VERTEX_DIAMETER / 2,
                    y - VERTEX_DIAMETER / 2,
                    VERTEX_DIAMETER, VERTEX_DIAMETER);
        }
        ImageIcon imageIcon = new ImageIcon(image);
        return new JLabel(imageIcon);
    }
}

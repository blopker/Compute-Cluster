package tasks;

import api.Task;
import com.google.common.collect.Collections2;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * This task generates a minimal solution to the Traveling Salesman Problem.
 *
 * @author klopker
 * @author douglas
 */
public class EuclideanTspTask implements Task<EuclideanTspTask>, Serializable {

    private double[][] cities;
    private List<Integer> start = new ArrayList<Integer>();
    List<Integer> minTour = new ArrayList<Integer>();
    private double min;
    /**
     * This task computes the cost of each permutation that solves the Traveling
     * Salesman Problem, and produces the tour with the minimal cost.
     * 
     * This constructor uses a starting city of {0}.
     *
     * @param cities a double matrix that represents the x and y coordinates of
     * every city represented in a Traveling Salesman Problem. cities[i][0] is
     * the x-coordinate of city[i] and cities[i][1] is the y-coordinate of
     * city[i].
     */
    public EuclideanTspTask(double[][] cities) {
        int[] startTemp = {0};
        init(startTemp, cities);
    }

    /**
     * 
     * This task computes the cost of each permutation that solves the Traveling
     * Salesman Problem, and produces the tour with the minimal cost.
     *
     * @param cities a double matrix that represents the x and y coordinates of
     * every city represented in a Traveling Salesman Problem. cities[i][0] is
     * the x-coordinate of city[i] and cities[i][1] is the y-coordinate of
     * city[i].
     *
     * @param start a int[] that specifies the starting cities. Starting cities are
     * calculated in the distance, but are not iterated over. A min tour with starting
     * of {0, 1, 4} will always start with {0, 1, 4}.
     * 
     */
    public EuclideanTspTask(int[] start, double[][] cities) {
        init(start, cities);
    }
    
    private void init(int[] startTemp, double[][] cities){
        for (int i : startTemp) {
            this.start.add(i);
        }
        this.cities = cities;
    }
    
    @Override
    public EuclideanTspTask execute() {
        min = Double.MAX_VALUE;
        double dist;
        
        List<Integer> tour;
        for (List<Integer> perm : Collections2.permutations(getInitialTour())) {
            tour = addStart(perm);
            dist = getTourLength(tour);
            if (min > dist) {
                min = dist;
                minTour.clear();
                minTour.addAll(tour);
            }
        }
        System.out.println("done!");
        System.out.println("Min found:");
        printCityList(minTour);
        return this;
    }

    private void printCityList(List<Integer> tour) {
//        System.out.print("{" + cities[0][0] + ", " + cities[0][1] + "} ");
        for (int city : tour) {
            System.out.print("{" + cities[city][0] + ", " + cities[city][1] + "} ");
        }
        System.out.println();
    }

    private List<Integer> getInitialTour() {
        List<Integer> tour = new ArrayList<Integer>();
        for (int i = 0; i < cities.length; i++) {
            if (!start.contains(i)) {
                tour.add(i);
            }
        }
        return tour;
    }

    private double getTourLength(List<Integer> tour) {
        double distance = 0;
        for (int i = 0; i < tour.size(); i++) {
            int pos = tour.get(i);
            int pos_next = tour.get((i + 1)%tour.size());
            distance += getDistance(cities[pos], cities[pos_next]);
        }
        return distance;
    }

    private double getDistance(double[] a, double[] b) {
        return Math.sqrt(Math.pow(a[0] - b[0], 2) + Math.pow(a[1] - b[1], 2));
    }

    private List<Integer> addStart(List<Integer> perm) {
        List<Integer> tour = new ArrayList<Integer>(start);
        tour.addAll(perm);
        return tour;
    }
    
    /**
     * Get the results of the min tour for this task.
     * @return List<Integer>
     */
    public List<Integer> getMinTour() {
        return minTour;
    }

    /**
     * Get the distance of the min tour found.
     * @return double
     */
    public double getMin() {
        return min;
    }
}
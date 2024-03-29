package tasks;

import api.Result;
import api.Shared;
import api.Task;
import api.UpperBound;
import com.google.common.collect.Collections2;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class TSPTask extends Task implements Serializable {

    private String childID = null;
    private final int TOUR_SIZE = 4;
    private double[][] cities;
    private List<Integer> start = new ArrayList<Integer>();
    List<Integer> minTour = new ArrayList<Integer>();
    private double min;
    private final String id;
    private UpperBound shared;

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
    public TSPTask(double[][] cities) {
        this.id = UUID.randomUUID().toString();
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
     * @param start a int[] that specifies the starting cities. Starting cities
     * are calculated in the distance, but are not iterated over. A min tour
     * with starting of {0, 1, 4} will always start with {0, 1, 4}.
     *
     */
    public TSPTask(int[] start, double[][] cities) {
        this.id = UUID.randomUUID().toString();
        init(start, cities);
    }

    private void init(int[] startTemp, double[][] cities) {
        for (int i : startTemp) {
            this.start.add(i);
        }
        this.cities = cities;
    }

    @Override
    public Result<List<Integer>> execute() {
        
        if (cities.length - start.size() > TOUR_SIZE) {
            UpperBound lowerBound = getLowerBound();
            if (lowerBound.isBetterThan(shared)) {
                List<Task> tasks = makeTasks();
                return new Result<List<Integer>>(this.getChildID(), null, tasks, null);
            }
            return new Result<List<Integer>>(this.getChildID(), minTour, null, null);
        }

        min = shared.getShared();
        double dist;

        List<Integer> tour = new ArrayList<Integer>();
        for (List<Integer> perm : Collections2.permutations(getInitialTour())) {
            addStart(tour, perm);
            dist = getTourLength(tour);
            if (min > dist) {
                min = dist;
                minTour.clear();
                minTour.addAll(tour);
            }
        }
        return new Result<List<Integer>>(this.getChildID(), minTour, null, new UpperBound(min));
    }

    private List<Task> makeTasks() {
        List<TSPTask> tspTasks = new ArrayList<TSPTask>();
        int[] start_tmp = new int[start.size() + 1];
        for (int i = 0; i < start.size(); i++) {
            start_tmp[i] = start.get(i);
        }

        for (int i = 0; i < cities.length; i++) {
            if (!start.contains(i)) {
                start_tmp[start.size()] = i;
                tspTasks.add(new TSPTask(start_tmp, cities));
            }
        }

        MinTask minTask = new MinTask(cities, tspTasks.size());
        minTask.setChildID(this.getChildID());

        for (Task task : tspTasks) {
            task.setChildID(minTask.getID());
        }

        List<Task> tasks = new ArrayList<Task>();
        tasks.addAll(tspTasks);
        tasks.add(minTask);
        return tasks;
    }

    private void printCityList(List<Integer> tour) {
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
            int pos_next = tour.get((i + 1) % tour.size());
            distance += getDistance(cities[pos], cities[pos_next]);
        }
        return distance;
    }

    private double getDistance(double[] a, double[] b) {
        return Math.sqrt(Math.pow(a[0] - b[0], 2) + Math.pow(a[1] - b[1], 2));
    }

    private void addStart(List<Integer> out, List<Integer> in) {
        out.clear();
        out.addAll(start);
        out.addAll(in);
    }

    /**
     * Get the results of the min tour for this task.
     *
     * @return List<Integer>
     */
    public List<Integer> getMinTour() {
        return minTour;
    }

    /**
     * Get the distance of the min tour found.
     *
     * @return double
     */
    public double getMin() {
        return min;
    }

    /**
     * Not used.
     *
     * @param argument
     */
    @Override
    public void addResult(Result argument) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean isReady() {
        return true;
    }

    @Override
    public String getID() {
        return this.id;
    }

    @Override
    public void setShared(Shared shared) {
        if (shared == null) {
            shared = new UpperBound(Double.MAX_VALUE);
        }
        this.shared = (UpperBound) shared;
    }

    public String getChildID() {
        return childID;
    }

    public void setChildID(String child_id) {
        this.childID = child_id;
    }

    @Override
    public boolean isLocalTask() {
        return cities.length - start.size() > TOUR_SIZE;
    }

    private UpperBound getLowerBound() {
        
        List<double[]> points = new ArrayList<double[]>();
        for (int i = 0; i < cities.length; i++) {
            if (!start.contains(i)) {
                points.add(cities[i]);
            }
        }
        points.remove(0);
        
        int n = points.size();
        double[] x = new double[n], y = new double[n];
        for (int i = 0; i < n; i++) {
            x[i] = points.get(i)[0];
            y[i] = points.get(i)[1];
        }
        
        double[] cost = new double[n]; // distance to MST
        boolean[] visit = new boolean[n];
        Arrays.fill(cost, Double.MAX_VALUE);
        cost[0] = 0.0D;
        double total = 0.0;
        for (int i = 0; i < cost.length; i++) {
            // Find next node to visit: minimum distance to MST
            double m = Double.MAX_VALUE;
            int v = -1;
            for (int j = 0; j < cost.length; j++) {
                if (!visit[j] && cost[j] < m) {
                    v = j;
                    m = cost[j];
                }
            }
            visit[v] = true;
            total += m;
            for (int j = 0; j < cost.length; j++) {
                final double d = Math.hypot(x[v] - x[j], y[v] - y[j]);
                if (d < cost[j]) {
                    cost[j] = d;
                }
            }
        }
        return new UpperBound(total + getTourLength(start));
    }
}
/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package tasks;

import api.Result;
import api.Shared;
import api.Task;
import api.UpperBound;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 *
 * @author ninj0x
 */
class MinTask extends Task implements Serializable {

    private String childID = null;
    private final String ID;
    private int joinCount;
    private final List<List<Integer>> arguments;
    private List<Integer> ans;
    private double[][] cities;
    private UpperBound shared;

    public MinTask(double[][] cities, int number_of_arguments) {
        this.cities = cities;
        this.ID = UUID.randomUUID().toString();
        arguments = new ArrayList<List<Integer>>();
        ans = new ArrayList<Integer>();
        this.joinCount = number_of_arguments;
    }

    @Override
    public Result<List<Integer>> execute() {
        double min = Double.MAX_VALUE;
        for (List<Integer> tour : arguments) {
            if (tour == null || tour.isEmpty()) {
                continue;
            }
            double dist = getTourLength(tour);
            if (dist < min) {
                min = dist;
                ans.clear();
                ans.addAll(tour);
            }
        }
        return new Result<List<Integer>>(this.getChildID(), ans, null, new UpperBound(min));
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

    /**
     * Add a finished TSPTask to be compared with other TSP results.
     *
     * @param argument
     */
    @Override
    public void addResult(Result argument) {
        if (joinCount > 0) {
            joinCount--;
            arguments.add((List<Integer>) argument.getResult());
        }
    }

    @Override
    public boolean isReady() {
        return joinCount <= 0;
    }

    @Override
    public String getID() {
        return this.ID;
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
        return true;
    }
}

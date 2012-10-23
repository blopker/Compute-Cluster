/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package tasks;

import api.SpaceAPI;
import api.Task;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 *
 * @author ninj0x
 */
class MinTask extends Task<MinTask> implements Serializable{

    private final String parentID;
    private final Set<String> joinSet;
    private final List<List<Integer>> arguments;
    private List<Integer> ans;
    private double[][] cities;

    public MinTask(double[][] cities, TSPTask aThis, List<TSPTask> tasks) {
        this.cities = cities;
        parentID = aThis.getID();
        joinSet = new HashSet<>();
        arguments = new ArrayList<>();
        ans = new ArrayList<>();
        for (Task addable : tasks) {
            joinSet.add(addable.getID().toString());
//            System.out.println("task: " + addable.getID());
        }
    }

    @Override
    public MinTask execute(SpaceAPI space) {
        double min = Double.MAX_VALUE;
        for (List<Integer> tour : arguments) {
            double dist = getTourLength(tour);
            if (dist < min) {
                min = dist;
                ans.clear();
                ans.addAll(tour);
            }
        }
        return this;
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

    @Override
    public void addArgument(Task argument) {
        if (joinSet.remove(argument.getID().toString())) {
//            System.out.println("added:" + argument.getValue());
            arguments.add((List<Integer>)argument.getValue());
        }
    }

    @Override
    public boolean isReady() {
        return joinSet.isEmpty();
    }

    @Override
    public Object getValue() {
        return ans;
    }

    @Override
    public String getID() {
        return this.parentID;
    }
}

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package api;

/**
 *
 * @author ninj0x
 */
public class UpperBound extends Shared<Double> {

    public UpperBound(Double bound) {
        super(bound);
    }

    @Override
    public boolean isBetterThan(Shared<Double> other) {
        return this.getShared() < other.getShared();
    }
}

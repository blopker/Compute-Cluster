package api;

/**
 * The upper bound shared object. Used to find minium upper bounds of doubles.
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

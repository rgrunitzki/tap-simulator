package scenario.network;

/**
 * This object represents the cost function of the edge.
 *
 * @author Ricardo Grunitzki
 */
public abstract class AbstractCostFunction {

    /**
     * Returns the the cost function of the edge.
     *
     * @param edge evaluated edge
     * @return cost value
     */
    public abstract double evalCost(AbstractEdge edge);

    /**
     * Returns the cost function of the edge for a specific flow.
     *
     * @param edge evaluated edge
     * @param desirableFlow desirable flow
     * @return cost value
     */
    public abstract double evalDesirableCost(AbstractEdge edge, double desirableFlow);

    /**
     * Converts an String object to a double object
     *
     * @param value String value
     * @return Double value
     */
    protected Double stringToDouble(String value) {
        if (value.equals("")) {
            return 0.0;
        } else {
            return Double.parseDouble(value);
        }
    }
}

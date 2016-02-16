package scenario;

/**
 *
 * @author Ricardo Grunitzki
 */
public abstract class AbstractCostFunction {

    public abstract double evalCost(Edge edge);
    public abstract double evalDesirableCost(Edge edge, double desirableFlow);

    protected Double stringToDouble(String value) {
        if (value.equals("")) {
            return 0.0;
        } else {
            return Double.parseDouble(value);
        }
    }
}

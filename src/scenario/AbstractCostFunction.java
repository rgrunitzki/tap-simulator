package scenario;

/**
 *
 * @author Ricardo Grunitzki
 */
public abstract class AbstractCostFunction {

    public abstract double evalCost(AbstractEdge edge);
    public abstract double evalDesirableCost(AbstractEdge edge, double desirableFlow);

    protected Double stringToDouble(String value) {
        if (value.equals("")) {
            return 0.0;
        } else {
            return Double.parseDouble(value);
        }
    }
}

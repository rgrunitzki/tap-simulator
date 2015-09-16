package scenario;

/**
 *
 * @author Ricardo Grunitzki
 */
public abstract class AbstractCostFunction {

    public abstract float evalCost(Edge edge);
    public abstract float evalDesirableCost(Edge edge, int desirableFlow);

    protected Float stringToFloat(String value) {
        if (value.equals("")) {
            return 0f;
        } else {
            return Float.parseFloat(value);
        }
    }
}

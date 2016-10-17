package scenario.network;

/**
 * A linear cost function that represents the travel time in minutes. This
 * function is defined by t=fftime+flow*alpha, where:
 * <ul>
 * <li>t = travel time in minutes;
 * <li>fftime = travel time under free-flow condition;
 * <li>flow = flow of drivers;
 * <li>alpha = constant factor.
 * </ul>
 * This function is incremented linearly by c for each unit of flow (f).
 *
 * @author Ricardo Grunitzki
 */
public class LinearCostFunction extends AbstractCostFunction {

    @Override
    public double evalCost(AbstractEdge edge) {
        return (stringToDouble((String) edge.getParams().get("fftime")) + (stringToDouble((String) edge.getParams().get("alpha"))) * edge.getTotalFlow());
    }

    @Override
    public double evalDesirableCost(AbstractEdge edge, double desirableFlow) {
        return (stringToDouble((String) edge.getParams().get("fftime")) + (stringToDouble((String) edge.getParams().get("alpha"))) * desirableFlow);
    }
}

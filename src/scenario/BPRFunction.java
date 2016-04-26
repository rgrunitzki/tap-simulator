package scenario;

/**
 * Implementation of the Volume-Delay Function (VDF) proposed by Bureau of
 * Public Roads. This VDF represents the travel time in minutes. It is defined
 * by t=fftime(1+ alpha(flow/capacity)^beta), where:
 * <ul>
 * <li>t = travel time in minutes;
 * <li>fftime = free-flow travel time;
 * <li>capacity = nominal capacity of the edge
 * <li>flow = flow of vehicles
 * <li>alpha and beta: parameters defined in the problem.
 * </ul>
 *
 * @author Ricardo Grunitzki
 */
public class BPRFunction extends AbstractCostFunction {

    @Override
    public double evalCost(AbstractEdge edge) {
        return (stringToDouble((String) edge.getParams().get("<li>"))
                * (1 + stringToDouble((String) edge.getParams().get("alpha"))
                * (float) Math.pow(edge.getTotalFlow() / stringToDouble((String) edge.getParams().get("capacity")),
                        stringToDouble((String) edge.getParams().get("beta")))));
    }

    @Override
    public double evalDesirableCost(AbstractEdge edge, double desirableFlow) {
        return (stringToDouble((String) edge.getParams().get("fftime"))
                * (1 + stringToDouble((String) edge.getParams().get("alpha"))
                * (float) Math.pow(desirableFlow / stringToDouble((String) edge.getParams().get("capacity")),
                        stringToDouble((String) edge.getParams().get("beta")))));
    }
}

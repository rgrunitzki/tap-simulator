package scenario;

/**
 * Cost function of Braess Paradox network with 6 trips defined in:
 * <ul>
 * <li>K. Tumer and D. Wolpert, “Collective intelligence and Braess’ paradox,”
 * in Proceedings of the National Conference on Artificial Intelligence, 2000,
 * pp. 104–109.
 * </ul>
 *
 * @author Ricardo Grunitzki
 */
public class BraessParadoxCostFunction extends AbstractCostFunction {

    @Override
    public double evalCost(AbstractEdge edge) {
        return eval(((String) edge.getParams().get("operation")), edge.getTotalFlow(), (stringToDouble((String) edge.getParams().get("fftime"))));
    }

    @Override
    public double evalDesirableCost(AbstractEdge edge, double desirableFlow) {
        return eval(((String) edge.getParams().get("operation")), desirableFlow, (stringToDouble((String) edge.getParams().get("fftime"))));
    }

    private double eval(String operation, double flow, double ffttime) {
        if (operation.equalsIgnoreCase("*")) {
            if (flow < 1) {
                return ffttime;
            } else {
                return flow * ffttime;
            }
        } else {
            return flow + ffttime;
        }
    }
}

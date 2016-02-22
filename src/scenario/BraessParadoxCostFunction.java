/*
 * To change edge license header, choose License Headers in Project Properties.
 * To change edge template file, choose Tools | Templates
 * and open the template in the editor.
 *//*
 * To change edge license header, choose License Headers in Project Properties.
 * To change edge template file, choose Tools | Templates
 * and open the template in the editor.
 */
package scenario;

/**
 *
 * @author rgrunitzki
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

    public double eval(String operation, double flow, double ffttime) {
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

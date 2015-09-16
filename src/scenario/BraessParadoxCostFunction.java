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
    public float evalCost(Edge edge) {
        return eval(((String) edge.getParams().get("operation")), edge.getTotalFlow(), (stringToFloat((String) edge.getParams().get("fftime"))));
    }

    @Override
    public float evalDesirableCost(Edge edge, int desirableFlow) {
        return eval(((String) edge.getParams().get("operation")), desirableFlow, (stringToFloat((String) edge.getParams().get("fftime"))));
    }

    public float eval(String operation, int flow, float ffttime) {
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

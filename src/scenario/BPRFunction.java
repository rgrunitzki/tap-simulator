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
public class BPRFunction extends AbstractCostFunction {

    @Override
    public float evalCost(Edge edge) {
        return (stringToFloat((String) edge.getParams().get("fftime")) * 
                (1 + stringToFloat((String) edge.getParams().get("alpha")) * 
                (float) Math.pow(edge.getTotalFlow() / stringToFloat((String) edge.getParams().get("capacity")), 
                        stringToFloat((String) edge.getParams().get("beta")))));
    }

    @Override
    public float evalDesirableCost(Edge edge, int desirableFlow) {
        return (stringToFloat((String) edge.getParams().get("fftime")) * 
                (1 + stringToFloat((String) edge.getParams().get("alpha")) * 
                (float) Math.pow(desirableFlow / stringToFloat((String) edge.getParams().get("capacity")), 
                        stringToFloat((String) edge.getParams().get("beta")))));
    }
}

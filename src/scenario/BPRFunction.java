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
    public double evalCost(AbstractEdge edge) {
        return (stringToDouble((String) edge.getParams().get("fftime")) * 
                (1 + stringToDouble((String) edge.getParams().get("alpha")) * 
                (float) Math.pow(edge.getTotalFlow() / stringToDouble((String) edge.getParams().get("capacity")), 
                        stringToDouble((String) edge.getParams().get("beta")))));
    }

    @Override
    public double evalDesirableCost(AbstractEdge edge, double desirableFlow) {
        return (stringToDouble((String) edge.getParams().get("fftime")) * 
                (1 + stringToDouble((String) edge.getParams().get("alpha")) * 
                (float) Math.pow(desirableFlow / stringToDouble((String) edge.getParams().get("capacity")), 
                        stringToDouble((String) edge.getParams().get("beta")))));
    }
}

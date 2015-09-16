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
public class LinearCostFunction extends AbstractCostFunction {

    @Override
    public float evalCost(Edge edge) {
        return (stringToFloat((String) edge.getParams().get("fftime")) + (stringToFloat((String) edge.getParams().get("alpha"))) * edge.getTotalFlow());
    }

    @Override
    public float evalDesirableCost(Edge edge, int desirableFlow) {
        return (stringToFloat((String) edge.getParams().get("fftime")) + (stringToFloat((String) edge.getParams().get("alpha"))) * desirableFlow);
    }
}

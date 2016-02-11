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
    public double evalCost(Edge edge) {
        return (stringToDouble((String) edge.getParams().get("fftime")) + (stringToDouble((String) edge.getParams().get("alpha"))) * edge.getTotalFlow());
    }

    @Override
    public double evalDesirableCost(Edge edge, double desirableFlow) {
        return (stringToDouble((String) edge.getParams().get("fftime")) + (stringToDouble((String) edge.getParams().get("alpha"))) * desirableFlow);
    }
}

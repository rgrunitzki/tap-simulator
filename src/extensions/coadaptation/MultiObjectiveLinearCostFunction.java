/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package extensions.coadaptation;

import scenario.network.AbstractCostFunction;
import scenario.network.AbstractEdge;

/**
 *
 * @author rgrunitzki
 */
public class MultiObjectiveLinearCostFunction extends AbstractCostFunction {

    @Override
    public double evalCost(AbstractEdge edge) {
        return getTravelCost(edge, edge.getTotalFlow()) + getMonetaryCost(edge, edge.getTotalFlow());
//        return getTravelCost(edge, edge.getTotalFlow());
//        return getMonetaryCost(edge, edge.getTotalFlow());
    }

    @Override
    public double evalDesirableCost(AbstractEdge edge, double desirableFlow) {
        //return (stringToDouble((String) edge.getParams().get("fftime")) + (stringToDouble((String) edge.getParams().get("alpha"))) * desirableFlow);
        return getTravelCost(edge, desirableFlow)
                + getMonetaryCost(edge, desirableFlow);
    }

    public final double getMonetaryCost(AbstractEdge edge, double flow) {
        double cost = 0;
        if (edge instanceof LearnerEdge) {
            return ((LearnerEdge) edge).getpMax() * ((LearnerEdge) edge).getPriceProportion();
        } else {
            return cost;
        }
    }

    public final double getTravelCost(AbstractEdge edge, double flow) {
        return (stringToDouble((String) edge.getParams().get("fftime")) + (stringToDouble((String) edge.getParams().get("alpha"))) * flow);
    }
}

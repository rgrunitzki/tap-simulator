/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package scenario.network;

/**
 *
 * @author rgrunitzki
 */
public class BraessBazzanCostFunction extends AbstractCostFunction {

    //function BraessG (f) m*f+n 

    @Override
    public double evalCost(AbstractEdge edge) {
        return stringToDouble((String) edge.getParams().get("m")) * edge.getTotalFlow() + stringToDouble((String) edge.getParams().get("n"));
    }

    @Override
    public double evalDesirableCost(AbstractEdge edge, double desirableFlow) {
        return stringToDouble((String) edge.getParams().get("m")) * desirableFlow + stringToDouble((String) edge.getParams().get("n"));
    }
}
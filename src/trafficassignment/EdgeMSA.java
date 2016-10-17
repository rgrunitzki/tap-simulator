/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package trafficassignment;

import scenario.network.AbstractCostFunction;
import scenario.network.AbstractEdge;

/**
 *
 * @author Ricardo Grunitzki
 */
public class EdgeMSA extends AbstractEdge{
    
    private double msaFlow;

    public EdgeMSA(AbstractCostFunction costFunction) {
        super(costFunction);
        msaFlow = 0;
    }

    @Override
    protected double getWeight() {
        return getCostFunction().evalDesirableCost(this, msaFlow); //To change body of generated methods, choose Tools | Templates.
    }
    
    public double getMsaFlow() {
        return msaFlow;
    }

    public synchronized void setMsaFlow(double msaFlow) {
        this.msaFlow = msaFlow;
    }
    
    
}

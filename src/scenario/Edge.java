package scenario;

import java.util.HashMap;
import java.util.Map;
import org.jgrapht.graph.DefaultWeightedEdge;
import simulation.Params;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author rgrunitzki
 */
public class Edge extends DefaultWeightedEdge {

    private Map<String, Object> params;

    private int currentFlow;

    private int totalFlow;

    private int auxFlow;

    private AbstractCostFunction costFunction;

    public Edge(AbstractCostFunction costFunction) {
        this.params = new HashMap<>();
        this.currentFlow = 0;
        this.totalFlow = 0;
        this.costFunction = costFunction;
        this.auxFlow = 0;
    }

    public void setParams(Map<String, Object> params) {
        this.params = params;
    }

    public Map<String, Object> getParams() {
        return params;
    }

    public int getCurrentFlow() {
        return currentFlow;
    }

    public void setCurrentFlow(int currentFlow) {
        this.currentFlow = currentFlow;
    }

    public int getTotalFlow() {
        return totalFlow;
    }

    public void setTotalFlow(int totalFlow) {
        this.totalFlow = totalFlow;
    }

    @Override
    protected double getWeight() {
        return this.costFunction.evalCost(this); //To change body of generated methods, choose Tools | Templates.
    }

    public synchronized void clearCurrentFlow() {
        this.currentFlow = 0;
    }

    public synchronized void reset() {
        float param = 1.0f/Params.EPISODE;
        this.auxFlow =  (int) ((1-param)*this.auxFlow + param*totalFlow);
        this.currentFlow = 0;
        this.totalFlow = 0;
    }

    public synchronized void resetAll() {
        this.reset();
        totalFlow = 0;
    }

    public synchronized void incrementFlow() {
        this.currentFlow++;
        this.totalFlow++;
    }

    public String getName() {
        return "(" + this.getSource().toString() + " : " + this.getTarget().toString() + ")";
    }

    public String getSourceVertex() {
        return this.getSource().toString();
    }

    public String getTargetVertex() {
        return this.getTarget().toString();
    }

    public synchronized double getCost() {
        return this.getWeight();
    }

    public AbstractCostFunction getCostFunction() {
        return costFunction;
    }

    public int getAuxFlow() {
        return auxFlow;
    }

}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package scenario;

import driver.Driver;
import java.util.HashMap;
import java.util.Map;
import org.jgrapht.graph.DefaultWeightedEdge;
import simulation.Params;

/**
 *
 * @author Ricardo Grunitzki
 */
public abstract class AbstractEdge extends DefaultWeightedEdge implements Comparable<AbstractEdge> {

    private Map<String, Object> params;

    private int currentFlow;

    private int totalFlow;

    protected AbstractCostFunction costFunction;

    public AbstractEdge(AbstractCostFunction costFunction) {
        this.params = new HashMap<>();
        this.currentFlow = 0;
        this.totalFlow = 0;
        this.costFunction = costFunction;
    }

//    public synchronized void afterEpisode(){}
//    
//    public synchronized void beforeEpisode(){}
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
        return costFunction.evalCost(this);
    }

    public synchronized void clearCurrentFlow() {
        this.currentFlow = 0;
    }

    public synchronized void reset() {
        this.currentFlow = 0;
        this.totalFlow = 0;
    }

    public synchronized void resetAll() {
        this.reset();
        totalFlow = 0;
    }

    public synchronized void proccess(Driver driver) {
        //increment the flow of drivers on edge
        this.currentFlow += Params.PROPORTION;
        this.totalFlow += Params.PROPORTION;
    }

    public String getName() {
        return this.getSource().toString() + "|" + this.getTarget().toString();
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

    @Override
    public int compareTo(AbstractEdge o) {
        return this.getName().compareTo(o.getName());
    }

    public synchronized void incrementTotalFlow(int flow) {
        this.totalFlow += flow;
    }

}
